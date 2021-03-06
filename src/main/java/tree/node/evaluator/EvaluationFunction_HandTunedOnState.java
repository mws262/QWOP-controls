package tree.node.evaluator;

import game.action.Command;
import game.qwop.IStateQWOP.ObjectName;
import game.qwop.StateQWOP;
import game.state.IState;
import tree.node.NodeGameBase;

import java.util.Objects;

/**
 * Implementation of a node evaluation function which is a hand-tuned combination of state information, having to do
 * with torso angle, x position, and x velocity.
 *
 * @author matt
 */
@SuppressWarnings("unused")
public class EvaluationFunction_HandTunedOnState<C extends Command<?>, S extends IState> implements IEvaluationFunction<C, S> {

    @Override
    public float getValue(NodeGameBase<?, C, S> nodeToEvaluate) {
        Objects.requireNonNull(nodeToEvaluate.getState());

        float value = 0.f;
        value += getAngleValue(nodeToEvaluate);
        value += getDistanceValue(nodeToEvaluate);
        value += getVelocityValue(nodeToEvaluate);

        return value;
    }

    @Override
    public String getValueString(NodeGameBase<?, C, S> nodeToEvaluate) {
        Objects.requireNonNull(nodeToEvaluate.getState());

        String value = "";
        value += "Angle value: ";
        value += getAngleValue(nodeToEvaluate);
        value += ", Distance value: ";
        value += getDistanceValue(nodeToEvaluate);
        value += ", Velocity value: ";
        value += getVelocityValue(nodeToEvaluate);
        return value;
    }

    /**
     * Value component based on angles.
     *
     * @param nodeToEvaluate Node being scored.
     * @return A scalar value associated with state angles.
     */
    private float getAngleValue(NodeGameBase<?, C, S> nodeToEvaluate) {
        // TODO fix cast
        return ((StateQWOP) nodeToEvaluate.getState()).getStateVariableFromName(ObjectName.BODY).getTh();
    }

    /**
     * Value component based on x distances.
     *
     * @param nodeToEvaluate Node being scored.
     * @return A scalar value associated with horizontal positions.
     */
    private float getDistanceValue(NodeGameBase<?, C, S> nodeToEvaluate) {
        return nodeToEvaluate.getState().getCenterX();
    }

    /**
     * Value component based on velocities.
     *
     * @param nodeToEvaluate Node being scored.
     * @return A scalar value associated with state velocities.
     */
    private float getVelocityValue(NodeGameBase<?, C, S> nodeToEvaluate) {
        // TODO fix cast
        return ((StateQWOP) nodeToEvaluate.getState()).getStateVariableFromName(ObjectName.BODY).getDx();
    }

    @Override
    public EvaluationFunction_HandTunedOnState<C, S> getCopy() {
        return new EvaluationFunction_HandTunedOnState<>();
    }

    @Override
    public void close() {}
}
