package tree.sampler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.IGameInternal;
import game.action.Action;
import game.action.Command;
import game.qwop.QWOPConstants;
import game.state.IState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jblas.util.Random;
import tree.node.NodeGameExplorableBase;
import tree.node.evaluator.IEvaluationFunction;
import tree.sampler.rollout.IRolloutPolicy;
import value.updaters.IValueUpdater;

/**
 * Implements upper confidence bound for trees (UCBT, UCT, UCB, depending on who you ask).
 * Key feature is that it lets the user define a value, c, that weights exploration vs exploitation.
 *
 * @author Matt
 */
public class Sampler_UCB<C extends Command<?>, S extends IState> extends Sampler_DeadlockDelay<C, S> implements AutoCloseable {

    /**
     * Constant term on UCB exploration factor. Higher means more exploration.
     */
    public final float explorationConstant;

    public final float explorationRandomFactor;

    /**
     * Evaluation function used to score single nodes after rollouts are done.
     */
    private final IEvaluationFunction<C, S> evaluationFunction;

    /**
     * Policy used to evaluateActionDistribution the score of a tree expansion Node by doing rollout(s).
     */
    private final IRolloutPolicy<C, S> rolloutPolicy;

    private final IValueUpdater<C, S> valueUpdater;

    /**
     * Explore/exploit trade-off parameter. Higher means more exploration. Lower means more exploitation.
     */
    private final float c;

    /**
     * Are we done with the tree policy?
     */
    private boolean treePolicyDone = false;

    /**
     * Are we done with the expansion policy?
     */
    private boolean expansionPolicyDone = false;

    /**
     * Are we done with the rollout policy?
     */
    private boolean rolloutPolicyDone = false;

    private static final Logger logger = LogManager.getLogger(Sampler_UCB.class);

    /**
     * Must provide an evaluationFunction to get a numeric score for nodes after a rollout.
     * Also specify a rollout policy to use.
     */
    public Sampler_UCB(
            @JsonProperty("evaluationFunction") IEvaluationFunction<C, S> evaluationFunction,
            @JsonProperty("rolloutPolicy") IRolloutPolicy<C, S> rolloutPolicy,
            @JsonProperty("valueUpdater") IValueUpdater<C, S> valueUpdater,
            @JsonProperty("explorationConstant") float explorationConstant,
            @JsonProperty("explorationRandomFactor") float explorationRandomFactor) {
        this.evaluationFunction = evaluationFunction;
        this.rolloutPolicy = rolloutPolicy;
        this.valueUpdater = valueUpdater;
        this.explorationConstant = explorationConstant;
        this.explorationRandomFactor = explorationRandomFactor;
        c = explorationRandomFactor * Random.nextFloat() + explorationConstant;
    }

    /**
     * Propagate the score and visit count back up the tree.
     */
    private void propagateScore(NodeGameExplorableBase<?, C, S> failureNode, float score) {
        // Do evaluation and propagation of scores.
        failureNode.recurseUpTreeInclusive(n -> n.updateValue(score, valueUpdater));
    }

    @Override
    public NodeGameExplorableBase<?, C, S> treePolicy(NodeGameExplorableBase<?, C, S> startNode) {

        if (startNode.getTreeDepth() == 0 && (startNode.getChildCount() == 0 || startNode.isLocked())) {
            if (startNode.reserveExpansionRights()) {
                resetDeadlockDelay();
                return startNode;
            } else {
                deadlockDelay();
                return treePolicy(startNode);
            }
        }

        if (startNode.getUntriedActionCount() > 0) {
            if (startNode.reserveExpansionRights()) { // We immediately expand
                // if there's an untried command.
                assert startNode.isLocked();
                resetDeadlockDelay();
                return startNode;
            } else {
                if (startNode.getTreeDepth() > 0) {
                    return treePolicy(startNode.getParent()); // TODO this could cause it to back up beyond the point
                    // we want to expand. Just keep that in mind.
                } else {
                    deadlockDelay();
                    return treePolicy(startNode);
                }
            }
        }

        double bestScoreSoFar = -Double.MAX_VALUE;
        NodeGameExplorableBase<?, C, S> bestNodeSoFar = null;

        // Otherwise, we go through the existing tree picking the "best."
        for (NodeGameExplorableBase<?, C, S> child : startNode.getChildren()) {

            if (!child.isFullyExplored() && !child.isLocked() && child.getUpdateCount() > 0) {
                float val = (child.getValue() + c * (float) Math.sqrt(2. * Math.log((double) startNode.getUpdateCount()) / (double) child.getUpdateCount()));
                assert !Float.isNaN(val);
                if (val > bestScoreSoFar) {
                    bestNodeSoFar = child;
                    bestScoreSoFar = val;
                }
            }
        }

        if (bestNodeSoFar == null) { // This worker can't get a lock on any of the children it wants. Starting back
        	deadlockDelay();
            bestNodeSoFar = startNode;
        } else {
            resetDeadlockDelay();
        }

        return treePolicy(bestNodeSoFar); // Recurse until we reach a node with an unchecked command.
    }

    @Override
    public void treePolicyActionDone(NodeGameExplorableBase<?, C, S> currentNode) {
        treePolicyDone = true; // Enable transition to next through the guard.
        expansionPolicyDone = false; // Prevent transition before it's done via the guard.
    }

    @Override
    public boolean treePolicyGuard(NodeGameExplorableBase<?, C, S> currentNode) {
        return treePolicyDone; // True means ready to move on to the next.
    }

    @Override
    public Action<C> expansionPolicy(NodeGameExplorableBase<?, C, S> startNode) {
        if (startNode.getUntriedActionCount() == 0)
            throw new IndexOutOfBoundsException("Expansion policy received a node from which there are no new nodes to try!");
        return startNode.getUntriedActionOnDistribution();
    }

    @Override
    public void expansionPolicyActionDone(NodeGameExplorableBase<?, C, S> currentNode) {
        treePolicyDone = false;
        expansionPolicyDone = true; // We move on after adding only one node.
        if (currentNode.getState().isFailed() || currentNode.getState().getCenterX() >= QWOPConstants.goalDistance) {
            // TODO shouldn't use a strictly QWOP constant here.
            rolloutPolicyDone = true;
            propagateScore(currentNode, evaluationFunction.getValue(currentNode));
        } else {
            rolloutPolicyDone = false;
        }
    }

    @Override
    public boolean expansionPolicyGuard(NodeGameExplorableBase<?, C, S> currentNode) {
        return expansionPolicyDone;
    }

    @Override
    public void rolloutPolicy(NodeGameExplorableBase<?, C, S> startNode, IGameInternal<C, S> game) {
        if (startNode.getState().isFailed())
            throw new IllegalStateException("Rollout policy received a starting node which corresponds to an already failed " +
                    "state.");
        float score = rolloutPolicy.rollout(startNode, game);
        propagateScore(startNode, score);

        rolloutPolicyDone = true;
    }

    @Override
    public boolean rolloutPolicyGuard(NodeGameExplorableBase<?, C, S> currentNode) {
        return rolloutPolicyDone;
    }

    @JsonIgnore
    @Override
    public Sampler_UCB<C, S> getCopy() {
        return new Sampler_UCB<>(evaluationFunction.getCopy(), rolloutPolicy.getCopy(),
                valueUpdater.getCopy(), explorationConstant, explorationRandomFactor);
    }

    public IEvaluationFunction<C, S> getEvaluationFunction() {
        return evaluationFunction;
    }

    public IRolloutPolicy<C, S> getRolloutPolicy() {
        return rolloutPolicy;
    }

    public IValueUpdater<C, S> getValueUpdater() {
        return valueUpdater;
    }

    @JsonIgnore
    public float getC() {
        return c;
    }

    @Override
    public void close() {
        evaluationFunction.close();
        rolloutPolicy.close();
    }
}
