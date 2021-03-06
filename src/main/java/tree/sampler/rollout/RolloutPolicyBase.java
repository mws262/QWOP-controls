package tree.sampler.rollout;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import controllers.IController;
import distributions.Distribution;
import distributions.Distribution_Normal;
import game.IGameInternal;
import game.IGameSerializable;
import game.action.*;
import game.qwop.CommandQWOP;
import game.qwop.QWOPConstants;
import game.state.IState;
import org.jetbrains.annotations.NotNull;
import tree.node.NodeGameBase;
import tree.node.NodeGameExplorable;
import tree.node.NodeGameExplorableBase;
import tree.node.evaluator.IEvaluationFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Sometimes, rollouts need to be a composite of many game.command, some which may involve multiple simulations. This
 * interface allows for more complicated evaluations to be run.
 *
 * @author matt
 */
public abstract class RolloutPolicyBase<C extends Command<?>, S extends IState> implements IRolloutPolicy<C, S> {

    public final IEvaluationFunction<C, S> evaluationFunction;

    public final IActionGenerator<C> rolloutActionGenerator;

    private ActionQueue<C> actionQueue = new ActionQueue<>();

    private final List<Action<C>> actionSequence = new ArrayList<>(); // Reused local list.

    public final int maxTimesteps;

    @JsonProperty
    public boolean useSerializedState = false;

    RolloutPolicyBase(
            @JsonProperty("evaluationFunction") @NotNull IEvaluationFunction<C, S> evaluationFunction,
            @JsonProperty("rolloutActionGenerator") @NotNull IActionGenerator<C> rolloutActionGenerator,
            @JsonProperty("maxTimesteps") int maxTimesteps) {
        this.evaluationFunction = evaluationFunction;
        this.rolloutActionGenerator = rolloutActionGenerator;
        this.maxTimesteps = maxTimesteps;
    }

    /**
     * Run the simulation to get back to a specified node.
     * @param targetNode Node we want to simulate to.
     * @param game Game used for simulation. Will be resetGame before simulating.
     */
    void simGameToNode(@NotNull NodeGameBase<?, C, S> targetNode,
                       @NotNull IGameInternal<C, S> game) {
        // Reset the game and command queue.
        game.resetGame();
        actionQueue.clearAll();
        targetNode.getSequence(actionSequence);
        actionQueue.addSequence(actionSequence);

        while (!actionQueue.isEmpty()) {
            game.step(actionQueue.pollCommand());
        }
    }

    /**
     * Force-set the state of the game to the state at a node. This is not the same thing since the warm-start states
     * will not be set.
     * @param target Node to set the game's state to.
     * @param game Game used for simulation. Will be resetGame before setting the state.
     */
    void coldStartGameToNode(@NotNull NodeGameBase<?, C, S> target,
                             @NotNull IGameInternal<C, S> game) {
        // Reset the game.
        game.resetGame();
        actionQueue.clearAll();
        game.setState(target.getState());
    }


    private NodeGameExplorableBase<?, C, S> recentRolloutNode; // For unit test.
    /**
     * Do a rollout from a given node. Assumes that the given game is in the state of startNode! Be careful!
     * @param startNode Starting Node to rollout from.
     * @param game Instance of the game to use. Must already be at the state of startNode.
     * @return The reward associated with how good this rollout was.
     */
    @Override
    public float rollout(@NotNull NodeGameExplorableBase<?, C, S> startNode, IGameInternal<C, S> game) {
        Preconditions.checkNotNull(game);

        if (maxTimesteps < 1) {
            throw new IllegalArgumentException("Maximum timesteps for rollout must be at least one. Was: " + maxTimesteps);
        }
        assert startNode.getState().equals(game.getCurrentState());

        // Create a duplicate of the start node, but with the specific ActionGenerator for rollouts. References from
        // parent so as not to screw up the tree depth.
        NodeGameExplorableBase<?, C, S> rolloutNode;
        if (startNode.getTreeDepth() == 0) { // Prevent null pointer due to no action at root.
            rolloutNode = new NodeGameExplorable<>(startNode.getState(), rolloutActionGenerator);
        } else {
             rolloutNode = startNode.getParent().addBackwardsLinkedChild(startNode.getAction(),
                    startNode.getState(), rolloutActionGenerator);
        }

        float totalScore = startScore(startNode);

        int timestepCounter = 0;
        // Falling, too many timesteps, reaching the finish line.
        while (!rolloutNode.getState().isFailed() && timestepCounter < maxTimesteps && rolloutNode.getState().getCenterX() < QWOPConstants.goalDistance) { // TODO qwop specific remove
            Action<C> childAction = useSerializedState ? getRolloutController().policy(rolloutNode, (IGameSerializable<C, S>) game) :
                    getRolloutController().policy(rolloutNode);

            actionQueue.addAction(childAction);

            NodeGameBase<?, C, S> intermediateNodeBefore = rolloutNode;
            while (!actionQueue.isEmpty() && !game.isFailed() && timestepCounter < maxTimesteps) {
                game.step(actionQueue.pollCommand());
                NodeGameBase<?, C, S> intermediateNodeAfter =
                        intermediateNodeBefore.addBackwardsLinkedChild(childAction,
                        game.getCurrentState());
                totalScore += accumulateScore(timestepCounter, intermediateNodeBefore, intermediateNodeAfter);
                intermediateNodeBefore = intermediateNodeAfter;
                timestepCounter++;
            }

            rolloutNode = rolloutNode.addBackwardsLinkedChild(childAction, game.getCurrentState(), rolloutActionGenerator);
        }
        totalScore += endScore(rolloutNode);
        recentRolloutNode = rolloutNode; // Mostly just stored for unit tests.
        return calculateFinalScore(totalScore, startNode, rolloutNode, timestepCounter);
    }

    /**
     * Component of the score that comes from the starting node. Note that this is added to the score. If you want to
     * subtract off an initial distance, then remember to include the minus sign in here somewhere.
     * @param startNode Node at the beginning of the rollout.
     * @return Component of the score that comes from the starting node.
     */
    abstract float startScore(NodeGameExplorableBase<?, C, S> startNode);

    /**
     * An "integrated" part of the score. This gets called every timestep of the rollout, and the particular rollout
     * policy may choose to add some score accordingly. This gets strictly added into the total score. If you want to
     * normalize by time or something similar, then it needs to happen in here.
     * @param timestepSinceRolloutStart Number of simulated timesteps in the rollout so far.
     * @param before Node representing the runner at the previous timestep.
     * @param after Node representing the runner at this timestep.
     * @return A score component having to do with a single timestep.
     */
    abstract float accumulateScore(int timestepSinceRolloutStart, NodeGameBase<?, C, S> before,
                                   NodeGameBase<?, C, S> after);

    /**
     * Component of the score that comes from the final node in the rollout. For all rollouts that inherit from
     * {@link RolloutPolicyBase}, this final node is either fallen, at the finish line, or has reached max specified
     * rollout timesteps. For any old rollout inheriting from {@link IRolloutPolicy} this may not be the case.
     * @param endNode Terminal node in this rollout execution.
     * @return A component of the score having to do with the final node in the rollout.
     */
    abstract float endScore(NodeGameExplorableBase<?, C, S> endNode);

    /**
     * Handles any final adjustments to score that you'd like to do.
     * @param accumulatedValue The accumulated value so far already includes (final_score - initial_score +
     *                         accumulated_score).
     * @param startNode Node at the start of this rollout.
     * @param endNode Final node reached. Either is at the max timestep limit, is fallen, or has reached the finish
     *                line.
     * @param rolloutDurationTimesteps Number of timesteps simulated DURING the rollout.
     * @return Final adjusted score. If you are satisfied with the accumulated value so far, then just return it.
     */
    abstract float calculateFinalScore(float accumulatedValue, NodeGameExplorableBase<?, C, S> startNode,
                                       NodeGameExplorableBase<?, C, S> endNode, int rolloutDurationTimesteps);

    public abstract IController<C, S> getRolloutController();

    @Override
    public abstract RolloutPolicyBase<C, S> getCopy();

    public static IActionGenerator<CommandQWOP> getQWOPRolloutActionGenerator() {
        /* Space of allowed game.command to sample */
        //Distribution<Action> uniform_dist = new Distribution_Equal();

        /* Repeated command 1 -- no keys pressed. */
        Distribution<Action<CommandQWOP>> dist1 = new Distribution_Normal<>(12, 5f);
        ActionList<CommandQWOP> actionList1 = ActionList.makeActionList(IntStream.range(2, 20).toArray(),
                CommandQWOP.NONE, dist1);

        /*  Repeated command 2 -- W-O pressed */
        Distribution<Action<CommandQWOP>> dist2 = new Distribution_Normal<>(20, 5f);
        ActionList<CommandQWOP> actionList2 = ActionList.makeActionList(IntStream.range(15, 30).toArray(),
                CommandQWOP.WO, dist2);

        /* Repeated command 3 -- W-O pressed */
        Distribution<Action<CommandQWOP>> dist3 = new Distribution_Normal<>(12f, 5f);
        ActionList<CommandQWOP> actionList3 = ActionList.makeActionList(IntStream.range(2, 20).toArray(),
                CommandQWOP.NONE, dist3);

        /*  Repeated command 4 -- Q-P pressed */
        Distribution<Action<CommandQWOP>> dist4 = new Distribution_Normal<>(20, 5f);
        ActionList<CommandQWOP> actionList4 = ActionList.makeActionList(IntStream.range(15, 30).toArray(), CommandQWOP.QP,
                dist4);

        return new ActionGenerator_FixedSequence<>(actionList1, actionList2, actionList3, actionList4);
    }

    public IEvaluationFunction<C, S> getEvaluationFunction() {
        return evaluationFunction;
    }
}
