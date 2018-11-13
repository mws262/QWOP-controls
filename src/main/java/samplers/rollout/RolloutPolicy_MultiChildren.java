package samplers.rollout;

import actions.Action;
import evaluators.IEvaluationFunction;
import game.GameLoader;
import tree.Node;

/**
 * To evaluate the score of a node. Run a rollout for each of its potential child nodes. Average them out. If run for
 * all potential children, this can be quite slow.
 *
 * @author matt
 */
public class RolloutPolicy_MultiChildren extends RolloutPolicy {

    /**
     * Maximum number of allowed rollouts.
     */
    public int maxRollouts = 3;

    /**
     * Do repeated rollouts as cold-starts? This will be faster for deep trees.
     */
    public boolean doColdStarts = true;

    public RolloutPolicy_MultiChildren(IEvaluationFunction evaluationFunction) {
        super(evaluationFunction);
    }

    @Override
    public float rollout(Node startNode, GameLoader game) {
        // See how we should advance through untried actions. If the number of unchecked actions is less than the
        // number of rollouts allowed, we run all of them. Otherwise, we try to evenly-space them.
        float advancement = 1f;
        int numUnchecked = startNode.uncheckedActions.size();
        if (numUnchecked > maxRollouts) {
            advancement = numUnchecked / (float)maxRollouts;
        }

        actionQueue.clearAll();
        float score = 0;
        int count = 0; // First loop, we don't need to re-simulate back to the start node since we're already there.
        for (float i = 0; i < numUnchecked; i += advancement) {
            if (count++ > 0) {
                // Re-sim back to the fringe of the tree.
                game.makeNewWorld();
                if (doColdStarts) {
                    coldStartGameToNode(startNode, game);
                } else {
                    simGameToNode(startNode, game);
                }
            }

            // Try one of the child actions.
            actionQueue.addAction(startNode.uncheckedActions.get((int)i));
            Node childNode = new Node(startNode, startNode.uncheckedActions.get((int)i), false);
            while (!actionQueue.isEmpty() && !game.getFailureStatus()) {
                game.stepGame(actionQueue.pollCommand());
            }
            childNode.setState(game.getCurrentState());

            // Continue rollout randomly.
            Node terminalNode = randomRollout(childNode, game);

            // Accumulate score.
            score += evaluationFunction.getValue(terminalNode);
        }

        return score/(float)(maxRollouts > startNode.uncheckedActions.size() ? startNode.uncheckedActions.size() :
                maxRollouts);
    }
}
