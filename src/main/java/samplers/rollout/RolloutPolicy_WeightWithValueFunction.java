package samplers.rollout;

import game.IGameInternal;
import tree.NodeQWOPExplorableBase;
import value.IValueFunction;

/**
 * Meta-rollout policy which weights the rollout result with a straight-up evaluation of the value function at the
 * starting node.
 *
 * @author matt
 *
 */
public class RolloutPolicy_WeightWithValueFunction extends RolloutPolicy {

    public float valueFunctionWeight = 0.8f;

    private RolloutPolicy individualRollout;

    private IValueFunction valueFunction;

    public RolloutPolicy_WeightWithValueFunction(RolloutPolicy individualRollout, IValueFunction valueFunction) {
        super(individualRollout.evaluationFunction);
        this.individualRollout = individualRollout;
        this.valueFunction = valueFunction;
    }

    @Override
    public float rollout(NodeQWOPExplorableBase<?> startNode, IGameInternal game) {
        float rolloutValue = individualRollout.rollout(startNode, game);

        return (1 - valueFunctionWeight) * rolloutValue + valueFunctionWeight * valueFunction.evaluate(startNode);    }

    @Override
    public RolloutPolicy getCopy() {
        RolloutPolicy_WeightWithValueFunction rolloutCopy =
                new RolloutPolicy_WeightWithValueFunction(individualRollout.getCopy(), valueFunction);
        rolloutCopy.valueFunctionWeight = valueFunctionWeight;
        return rolloutCopy;
    }
}