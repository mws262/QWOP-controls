package samplers.rollout;

import actions.Action;
import game.IGameInternal;
import tree.NodeQWOPExplorableBase;
import value.ValueFunction_TensorFlow_StateOnly;

public class RolloutPolicy_ValueFunctionDecayingHorizon extends RolloutPolicy {

    // Decaying horizon kernel parameters.
    // Kernel is an s-curve meant to be evaluated from 0 to 1 and producing values from 0 to 1.
    private static final float kernelCenter = 0.5f; // Where the center of the s lies. Greater values shift the drop
    // til later, smaller values make the drop occur earlier.
    private static final float kernelSteepness = 5; // Steepness of the drop. Higher values == more steep.

    public int maxTimestepsToSim = 100;
    public float valueFunctionWeight = 0.8f;

    ValueFunction_TensorFlow_StateOnly valFun;
    public RolloutPolicy_ValueFunctionDecayingHorizon(ValueFunction_TensorFlow_StateOnly valFun) {
        super(null);
        this.valFun = valFun;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public float rollout(NodeQWOPExplorableBase<?> startNode, IGameInternal game) {
        int timestepCounter = 0;
        NodeQWOPExplorableBase<?> rolloutNode = startNode;
        float accumulatedValue = 0f;

        float previousValue = game.getCurrentState().body.getX(); // TODO Stop hardcoding this as body x and instead
        // use the evaluation function.
        while (!rolloutNode.getState().isFailed() && timestepCounter < maxTimestepsToSim) {
            Action childAction = valFun.getMaximizingAction(rolloutNode); //rolloutNode.uncheckedActions.getRandom();
            actionQueue.addAction(childAction);

            while (!actionQueue.isEmpty() && !game.getFailureStatus() && timestepCounter < maxTimestepsToSim) {
                game.step(actionQueue.pollCommand());
                float currentValue = game.getCurrentState().body.getX();
                float multiplier = getKernelMultiplier(timestepCounter / (float) maxTimestepsToSim);

                accumulatedValue += multiplier * (currentValue - previousValue);
                previousValue = currentValue;

                timestepCounter++;
            }
            rolloutNode = rolloutNode.addBackwardsLinkedChild(childAction, game.getCurrentState());
        }
        return (1 - valueFunctionWeight) * accumulatedValue + valueFunctionWeight * valFun.evaluate(startNode);
    }

    @Override
    public RolloutPolicy getCopy() {
        RolloutPolicy_ValueFunctionDecayingHorizon rolloutCopy =
                new RolloutPolicy_ValueFunctionDecayingHorizon(valFun.getCopy());
        rolloutCopy.maxTimestepsToSim = this.maxTimestepsToSim;
        rolloutCopy.valueFunctionWeight = this.valueFunctionWeight;
        return rolloutCopy;
    }

    private float getKernelMultiplier(float timestepsIn) {
        return (float) (-0.5 * Math.tanh(kernelSteepness * (timestepsIn - kernelCenter)) + 0.5);
    }
}
