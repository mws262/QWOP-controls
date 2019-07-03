package tree.node.evaluator;

import tree.node.NodeQWOPBase;

import java.util.Objects;

/**
 * Evaluation of a node based strictly on torso horizontal position.
 *
 * @author Matt
 */
public class EvaluationFunction_Distance implements IEvaluationFunction {

    public float scalingFactor = 1f;

    @Override
    public float getValue(NodeQWOPBase<?> nodeToEvaluate) {
        return Objects.requireNonNull(nodeToEvaluate.getState()).getCenterX() * scalingFactor;
    }

    @Override
    public String getValueString(NodeQWOPBase<?> nodeToEvaluate) {
        return String.valueOf(getValue(nodeToEvaluate));
    }

    @Override
    public EvaluationFunction_Distance getCopy() {
        return new EvaluationFunction_Distance();
    }
}