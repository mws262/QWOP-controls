package samplers.rollout;

import actions.Action;
import game.IGameInternal;
import tree.NodeQWOPExplorableBase;

/**
 * This is a meta-rollout policy. It does multiple other rollouts and aggregates the results.
 *
 * For now, this is either a best or worst out of three. Intuition says worst of three should be more robust, but so
 * far, best of three has been better. Perhaps because overinflated scores tend to be explored by UCB and toned down,
 * whereas underestimated scores might never be touched again.
 *
 * @author matt
 */
public class RolloutPolicy_Window extends RolloutPolicy {

    private RolloutPolicy individualRollout;

    public RolloutPolicy_Window(RolloutPolicy individualRollout) {
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

        return (0.6f * valMid + 0.2f * valAbove + 0.2f * valBelow); // Gets the best out of three. or avg or worst
    }

    @Override
    public RolloutPolicy getCopy() {
        return new RolloutPolicy_Window(individualRollout.getCopy());
    }
}
