package ui.runner;

import com.fasterxml.jackson.annotation.JsonProperty;
import game.qwop.CommandQWOP;
import game.qwop.GameQWOP;
import game.qwop.IStateQWOP;
import tree.node.NodeGameExplorableBase;
import tree.node.NodeGameGraphicsBase;
import tree.node.evaluator.EvaluationFunction_SqDistFromOther;

import java.awt.*;
import java.util.List;
import java.util.*;

public class PanelRunner_Comparison<S extends IStateQWOP> extends PanelRunner<S> {

    /**
     * Maximum number of similar node states to display.
     */
    public int maxNumStatesToShow = 50;

    /**
     * Node used for base comparison.
     */
    private NodeGameExplorableBase<?, CommandQWOP, S> selectedNode;

    private List<NodeGameExplorableBase<?, CommandQWOP, S>> focusNodes = new ArrayList<>();
    private List<S> states = new ArrayList<>();
    private List<Stroke> strokes = new ArrayList<>();
    private List<Color> colors = new ArrayList<>();

    private final String name;

    public PanelRunner_Comparison(@JsonProperty("name") String name) {
        this.name = name;
    }

    @Override
    public void update(NodeGameGraphicsBase<?, CommandQWOP, S> node) {
        //NodeGameGraphicsBase<?, CommandQWOP, StateQWOP> root = node.getRoot();
        // todo
//        if (root instanceof NodeGameGraphicsBase) {
//            NodeGameGraphicsBase graphicsRoot = ((NodeGameGraphicsBase) root);
//            graphicsRoot.clearN();
//        }
        states.clear();
        focusNodes.clear();
        strokes.clear();
        colors.clear();

        /* Focused node first */
        selectedNode = node;
        // TODO
//        selectedNode.overrideNodeColor = Color.PINK; // Restore its red color
//        selectedNode.displayPoint = true;
        S nodeState = selectedNode.getState();

        // Make the sequence centered around the selected node state.
        states.add(nodeState);
        strokes.add(boldStroke);
        colors.add(Color.PINK);
        focusNodes.add(node);

        // Get the nearest ones, according to the provided metric.
        EvaluationFunction_SqDistFromOther<CommandQWOP, S> evFun =
                new EvaluationFunction_SqDistFromOther<>(selectedNode.getState());

        Map<Float, NodeGameExplorableBase<?, CommandQWOP, S>> evaluatedNodeList = new TreeMap<>();

        List<NodeGameExplorableBase<?, CommandQWOP, S>> allNodes = new ArrayList<>();
        node.getRoot().recurseDownTreeInclusive(allNodes::add);


        for (NodeGameExplorableBase<?, CommandQWOP, S> n : allNodes) {
            evaluatedNodeList.put(-evFun.getValue(n), n); // Low is better, so reverse so the lowest are at the top.
        }

        Iterator<NodeGameExplorableBase<?, CommandQWOP, S>> orderedNodes = evaluatedNodeList.values().iterator();

        for (int i = 0; i < maxNumStatesToShow; i++) {
            if (orderedNodes.hasNext()) {
                NodeGameExplorableBase<?, CommandQWOP, S> closeNode = orderedNodes.next();
                focusNodes.add(closeNode);
                states.add(closeNode.getState());
                strokes.add(normalStroke);
                Color matchColor = NodeGameGraphicsBase.getColorFromTreeDepth(i * 5, NodeGameGraphicsBase.lineBrightnessDefault);
                colors.add(matchColor);
                //TODO
//                closeNode.overrideNodeColor = matchColor;
//                closeNode.displayPoint = true;
            } else {
                break;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (!active) return;
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (selectedNode != null && selectedNode.getState() != null) {
            for (int i = 0; i < states.size(); i++) {
                GameQWOP.drawExtraRunner(g2, states.get(i).getPositionCoordinates(), "", runnerScaling,
                        xOffsetPixels + (int) (-runnerScaling * focusNodes.get(i).getState().getCenterX()), yOffsetPixels,
                        colors.get(i), strokes.get(i));
            }
        }
    }

    @Override
    public void activateTab() {
        active = true;
    }

    @Override
    public void deactivateTab() {
        // TODO
//        if (selectedNode != null) {
//            selectedNode.getRoot().clearNodeOverrideColor();
//        }
        active = false;
        states.clear();
        focusNodes.clear();
        strokes.clear();
        colors.clear();
    }

    @Override
    public String getName() {
        return name;
    }
}
