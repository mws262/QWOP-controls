package samplers.rollout;

import actions.Action;
import game.IGameInternal;
import tree.NodeQWOPExplorableBase;

/**
 * This is a meta-rollout policy. It does multiple other rollouts and aggregates the results.
 *
 * @author matt
 */
public class RolloutPolicy_WorstCaseWindow extends RolloutPolicy {

    private RolloutPolicy individualRollout;

    public RolloutPolicy_WorstCaseWindow(RolloutPolicy individualRollout) {
        super(individualRollout.evaluationFunction);
        this.individualRollout = individualRollout;
    }

    @Override
    public float rollout(NodeQWOPExplorableBase<?> startNode, IGameInternal game) {

        // Need to do a rollout for the actual node we landed on.
        Action middleAction = startNode.getAction();
        Action aboveAction = new Action(middleAction.getTimestepsTotal() + 1, middleAction.peek());
        Action belowAction = new Action(middleAction.getTimestepsTotal() - 1, middleAction.peek());


        float startValue = individualRollout.evaluationFunction.getValue(startNode);

        float valMid = individualRollout.rollout(startNode, game);

        if (startNode.getTreeDepth() > 1)
            simGameToNode(startNode.getParent(), game);
        actionQueue.addAction(aboveAction);
        while (!actionQueue.isEmpty())
            game.step(actionQueue.pollCommand());

        NodeQWOPExplorableBase<?> nodeAbove = startNode.getParent().addBackwardsLinkedChild(aboveAction, game.getCurrentState());
        float valAbove =
                individualRollout.rollout(nodeAbove, game) + individualRollout.evaluationFunction.getValue(nodeAbove) - startValue;

        if (startNode.getTreeDepth() > 1)
            simGameToNode(startNode.getParent(), game);
        actionQueue.addAction(belowAction);
        while (!actionQueue.isEmpty())
            game.step(actionQueue.pollCommand());

        NodeQWOPExplorableBase<?> nodeBelow = startNode.getParent().addBackwardsLinkedChild(belowAction, game.getCurrentState());


        float valBelow =
                individualRollout.rollout(nodeBelow, game) + individualRollout.evaluationFunction.getValue(nodeBelow) - startValue;

        return Math.max(valMid, Math.max(valAbove, valBelow)); // Gets the worst out of three.
    }

    @Override
    public RolloutPolicy getCopy() {
        return new RolloutPolicy_WorstCaseWindow(individualRollout.getCopy());
    }
}