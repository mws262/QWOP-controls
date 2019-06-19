package tree.sampler.rollout;

import game.actions.Action;
import game.actions.IActionGenerator;
import tree.node.evaluator.EvaluationFunction_Distance;
import game.state.IState;
import tree.node.NodeQWOPExplorableBase;

public class RolloutPolicy_RandomDecayingHorizon extends RolloutPolicy_DecayingHorizon {

    /**
     * Chooses which potential game.actions to add to temporary nodes created during the rollout process.
     */
    private final IActionGenerator actionGenerator = getRolloutActionGenerator();

    public RolloutPolicy_RandomDecayingHorizon() {
        super(new EvaluationFunction_Distance());
    }

    @Override
    float calculateFinalValue(float accumulatedValue, NodeQWOPExplorableBase<?> startNode) {
        return accumulatedValue;
    }

    @Override
    NodeQWOPExplorableBase<?> addNextRolloutNode(NodeQWOPExplorableBase<?> currentNode, Action action, IState state) {
        return currentNode.addBackwardsLinkedChild(action, state, actionGenerator);
    }

    @Override
    Action getNextAction(NodeQWOPExplorableBase<?> currentNode) {
        return currentNode.getUntriedActionRandom();
    }

    @Override
    public RolloutPolicy getCopy() {
        RolloutPolicy_RandomDecayingHorizon rolloutCopy = new RolloutPolicy_RandomDecayingHorizon();
        rolloutCopy.maxTimestepsToSim = this.maxTimestepsToSim;
        return rolloutCopy;
    }


}
