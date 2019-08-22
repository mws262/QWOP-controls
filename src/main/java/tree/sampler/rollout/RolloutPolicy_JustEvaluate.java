package tree.sampler.rollout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.IGameInternal;
import game.action.Command;
import tree.node.NodeQWOPExplorableBase;
import tree.node.evaluator.IEvaluationFunction;

/**
 * Rollout policy which just evaluates the node given, rather than actually doing any additional investigation.
 * Mostly useful as a sanity check.
 */
public class RolloutPolicy_JustEvaluate<C extends Command<?>> implements IRolloutPolicy<C> {

    public final IEvaluationFunction evaluationFunction;

    @JsonCreator
    public RolloutPolicy_JustEvaluate(@JsonProperty("evaluationFunction") IEvaluationFunction evaluationFunction) {
        this.evaluationFunction = evaluationFunction;
    }

    @Override
    public float rollout(NodeQWOPExplorableBase<?, C> startNode, IGameInternal<C> game) {
        return evaluationFunction.getValue(startNode);
    }

    @Override
    public IRolloutPolicy<C> getCopy() {
        return new RolloutPolicy_JustEvaluate<>(evaluationFunction);
    }


    @Override
    public void close() {
        evaluationFunction.close();
    }
}
