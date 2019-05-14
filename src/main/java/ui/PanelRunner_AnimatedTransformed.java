package ui;

import filters.NodeFilter_Downsample;
import game.GameUnified;
import game.State;
import transformations.ITransform;
import transformations.Transform_Autoencoder;
import transformations.Transform_PCA;
import tree.NodeQWOPExplorableBase;
import tree.NodeQWOPGraphicsBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * For running animations of the QWOP runner side-by-side with compressed then decompressed versions of those states.
 * Useful as a way to gauge how well a reduced model is approximating the full state.
 *
 * @author matt
 */
public class PanelRunner_AnimatedTransformed extends PanelRunner_Animated implements ActionListener {

    /**
     * Some {@link ITransform} require an initial calculation before working. This flag records whether this
     * initialization has occurred.
     */
    private boolean transformsInitialized = false;

    /**
     * Maximum number of nodes used for updating transforms. These will try to be
     * evenly spaced around the tree. Keeps speed up especially for PCA.
     */
    private NodeFilter_Downsample transformDownsampler = new NodeFilter_Downsample(2000);

    private List<ITransform> encoders;
    private List<State> inStates = new ArrayList<>();

    /**
     * Checkbox for enabling the drawing of transformed runners.
     */
    private JCheckBox enableTransformedRunners;

    /**
     * Whether or not the extra transformed runners are being drawn right now.
     */
    private boolean drawTransformedRunners = false;

    public PanelRunner_AnimatedTransformed() {
        super();
        String modelDir = "src/main/resources/tflow_models/";
        encoders = new ArrayList<>();
        encoders.add(new Transform_Autoencoder(modelDir + "AutoEnc_72to1_6layer.pb", 1));
        encoders.add(new Transform_Autoencoder(modelDir + "AutoEnc_72to2_6layer.pb", 2));
        encoders.add(new Transform_Autoencoder(modelDir + "AutoEnc_72to6_6layer.pb", 6));
        encoders.add(new Transform_Autoencoder(modelDir + "AutoEnc_72to8_6layer.pb", 8));
        encoders.add(new Transform_Autoencoder(modelDir + "AutoEnc_72to12_6layer.pb", 12));
        encoders.add(new Transform_Autoencoder(modelDir + "AutoEnc_72to16_6layer.pb", 16));
        encoders.add(new Transform_PCA(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}));

        /* Add a checkbox for enabling or disabling the transformed runner display. */
        BorderLayout borderLayout = new BorderLayout(); // Border layout for the whole panel to set alignment to the
        // bottom of the panel.
        setLayout(borderLayout);

        enableTransformedRunners = new JCheckBox("Show transformed runners?");
        FlowLayout checkLayout = new FlowLayout();
        checkLayout.setAlignment(FlowLayout.LEFT); // Flow layout for the checkbox to get the left alignment.
        enableTransformedRunners.setLayout(checkLayout);

        enableTransformedRunners.setSelected(drawTransformedRunners);
        enableTransformedRunners.addActionListener(this);
        add(enableTransformedRunners, BorderLayout.PAGE_END);
    }

    @Override
    public void simRunToNode(NodeQWOPExplorableBase<?> node) {
        List<NodeQWOPExplorableBase<?>> nodeList = new ArrayList<>();
        node.getRoot().recurseDownTreeInclusive(nodeList::add);

        transformDownsampler.filter(nodeList);
        List<State> stateList = nodeList.stream().map(NodeQWOPExplorableBase::getState).collect(Collectors.toList());

        for (ITransform trans : encoders) {
            trans.updateTransform(stateList);
        }
        transformsInitialized = true;
        super.simRunToNode(node);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (drawTransformedRunners && super.isActive() && transformsInitialized) {
            State currState = game.getCurrentState();
            if (currState != null) inStates.add(currState);
            for (int i = 0; i < encoders.size(); i++) {
                List<State> predictedStateList = encoders.get(i).compressAndDecompress(inStates);
                State predictedState = predictedStateList.get(0);
                GameUnified.drawExtraRunner((Graphics2D) g, predictedState, encoders.get(i).getName(), super.runnerScaling,
                        super.xOffsetPixels + i * 100 + 150, super.yOffsetPixels,
                        NodeQWOPGraphicsBase.getColorFromTreeDepth(i, NodeQWOPGraphicsBase.lineBrightnessDefault),
                        normalStroke);
            }
            inStates.clear();
        }
    }

    @Override
    public void deactivateTab() {
        super.deactivateTab();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(enableTransformedRunners)) {
            drawTransformedRunners = enableTransformedRunners.isSelected();
        }
    }
}
