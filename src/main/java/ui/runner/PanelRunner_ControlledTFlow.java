package ui.runner;

import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.Controller_ValueFunction;
import game.IGameSerializable;
import game.qwop.CommandQWOP;
import game.qwop.GameQWOP;
import game.qwop.GameQWOPCaching;
import game.qwop.IStateQWOP;
import game.state.transform.ITransform;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import value.ValueFunction_TensorFlow;
import value.ValueFunction_TensorFlow_StateOnly;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

/**
 * Panel for showing the behavior of controllers based around {@link ValueFunction_TensorFlow_StateOnly}. Has GUI
 * menus for selecting which Tensorflow model to use and which checkpoint file to load.
 */
public class PanelRunner_ControlledTFlow<S extends IStateQWOP>
        extends PanelRunner_Controlled<Controller_ValueFunction<CommandQWOP, S,
        ValueFunction_TensorFlow<CommandQWOP, S>>, S, IGameSerializable<CommandQWOP, S>>
        implements MouseListener, MouseMotionListener, ActionListener {

    /**
     * Name of the directory containing all the model (.pb) files.
     */
    @JsonProperty("modelLocation")
    public final String modelLocation;

    /**
     * Name of the directory containing all the checkpoint files.
     */
    @JsonProperty("checkpointLocation")
    public final String checkpointLocation;

    @JsonProperty("stateNormalizer")
    private final ITransform<S> stateNormalizer;
    /**
     * Drop-down menu for selecting Tensorflow model.
     */
    private final JComboBox<String> modelSelection;

    /**
     * Drop-down menu for selecting the Tensorflow checkpoint.
     */
    private final JComboBox<String> checkpointSelection;

    /**
     * Red label that appears when an invalid checkpoint is selected (either not present or not compatible with the
     * model).
     */
    private final JLabel badCheckpointMsg;

    /**
     * Red label that appears when a bad model is selected (can't be loaded, not found, or not compatible with the
     * given game.
     */
    private final JLabel badModelMsg;

    private static final Logger logger = LogManager.getLogger(PanelRunner_ControlledTFlow.class);

    /**
     * Parameters for the disturbance impulse arrow.
     */
    private boolean mouseActive;
    private Point mousePoint;
    private Shape arrowShape;
    private final Color arrowColor = new Color(0,0,0,127);
    private float disturbanceX;
    private float disturbanceY;

    public PanelRunner_ControlledTFlow(@JsonProperty("name") String name,
                                       @JsonProperty("game") IGameSerializable<CommandQWOP, S> game,
                                       @JsonProperty("stateNormalizer") ITransform<S> stateNormalizer,
                                       @JsonProperty("modelLocation") String modelLocation,
                                       @JsonProperty("checkpointLocation") String checkpointLocation) {
        super(name, game, null);

        this.modelLocation = modelLocation;
        this.checkpointLocation = checkpointLocation;
        this.stateNormalizer = stateNormalizer;

        // Selecting the TFlow model.
        JLabel modelLabel = new JLabel("Model");
        constraints.gridx = 0;
        constraints.gridy = layoutRows - 5;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.PAGE_END;
        add(modelLabel, constraints);

        modelSelection = new JComboBox<>(getAvailableModels());
        constraints.gridx = 0;
        constraints.gridy = layoutRows - 4;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.PAGE_START;
        modelSelection.addPopupMenuListener(new PopupMenuListener() { // Update the available files right before
            // the menu opens.
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                DefaultComboBoxModel model = (DefaultComboBoxModel) modelSelection.getModel();
                model.removeAllElements();
                Arrays.stream(getAvailableModels()).forEach(model::addElement);
                modelSelection.setModel(model);
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        modelSelection.setMaximumRowCount(50);
        add(modelSelection, constraints);
        modelSelection.setSelectedIndex(-1);
        modelSelection.addActionListener(this);

        badModelMsg = new JLabel("Model input/output size incorrect.");
        badModelMsg.setForeground(Color.RED);
        badModelMsg.setVisible(false);
        constraints.gridx = 1;
        constraints.gridy = layoutRows - 4;
        constraints.anchor = GridBagConstraints.LINE_END;
        add(badModelMsg, constraints);

        // Selecting the checkpoint.
        JLabel checkpointLabel = new JLabel("Controller checkpoint");
        constraints.gridx = 0;
        constraints.gridy = layoutRows - 3;
        constraints.anchor = GridBagConstraints.PAGE_END;
        add(checkpointLabel, constraints);

        checkpointSelection = new JComboBox<>(getAvailableCheckpoints());
        constraints.gridy = layoutRows - 2;
        constraints.anchor = GridBagConstraints.PAGE_START;

        checkpointSelection.addPopupMenuListener(new PopupMenuListener() { // Update the available files right before
            // the menu opens.
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                DefaultComboBoxModel model = (DefaultComboBoxModel) checkpointSelection.getModel();
                model.removeAllElements();
                Arrays.stream(getAvailableCheckpoints()).forEach(model::addElement);
                checkpointSelection.setModel(model);
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        checkpointSelection.setMaximumRowCount(50);
        add(checkpointSelection, constraints);
        checkpointSelection.setSelectedIndex(-1);
        checkpointSelection.addActionListener(this);

        badCheckpointMsg = new JLabel("Checkpoint does not match model.");
        badCheckpointMsg.setForeground(Color.RED);
        badCheckpointMsg.setVisible(false);
        constraints.gridx = 1;
        constraints.gridy = layoutRows - 2;
        constraints.anchor = GridBagConstraints.LINE_END;
        add(badCheckpointMsg, constraints);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        // Close TensorFlow stuff gracefully at the end.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (controller != null) {
                controller.close();
                logger.debug("Cleaned up TFlow controller.");
            }
        }));
    }

    /**
     * Check for new checkpoint files to put on the menu when it is opened.
     * @return All checkpoints in the directory.
     */
    private String[] getAvailableCheckpoints() {
        File[] files = new File(checkpointLocation).listFiles();
        Objects.requireNonNull(files);
        if (files.length > 0) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            return Arrays.stream(files).map(f -> f.getName().split("\\.")[0]).distinct().toArray(String[]::new);
        } else {
            return new String[0];
        }
    }

    /**
     * Check for model files to put on the menu when it is opened.
     * @return All model files in the directory.
     */
    private String[] getAvailableModels() {
        File[] files = new File(modelLocation).listFiles();
        Objects.requireNonNull(files);
        if (files.length > 0) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            return Arrays.stream(files).filter(f -> f.getPath().contains(".pb")).map(File::getName).distinct().toArray(String[]::new);
        } else {
            return new String[0];
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // When anything is selected on the drop-down menus.
        badModelMsg.setVisible(false);

        // Model selection.
        if (e.getSource().equals(modelSelection) && modelSelection.getSelectedItem() != null) { // Can be null
            // briefly while the entries are being updated.
            deactivateTab();
            // Switch controllers to the one
            try {
                String selectedModel = modelSelection.getSelectedItem().toString();
                if (controller != null) {
                    controller.close();
                    logger.debug("Cleaned up old TFlow controller before loading a new one.");
                }
                controller =
                        new Controller_ValueFunction<>(
                                new ValueFunction_TensorFlow_StateOnly<>(
                                        Paths.get(modelLocation, selectedModel).toFile(),
                                        game,
                                        stateNormalizer,
                                        null,
                                        1f, // No dropout for this evaluation.
                                        false
                                )
                        );
                logger.debug("Loaded a new controller: " + selectedModel);
                if (tryCheckpoint(checkpointSelection)) {
                    activateTab();
                } else {
                    activateDrawingButNotController();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (IllegalArgumentException modeNotMatchingGameException) { // Dimension of model input does not
                // match game state size
                activateDrawingButNotController();
                badModelMsg.setVisible(true);
            }
        } else if (e.getSource().equals(checkpointSelection)) {
            // Checkpoint selection.
            deactivateTab();
            if (tryCheckpoint(checkpointSelection)) {
                activateTab();
            } else {
                activateDrawingButNotController();
            }
        }
    }

    /**
     * Attempt to load a checkpoint specified in the menu.
     *
     * @param checkpointSelection Menu selection to attempt to load.
     * @return Whether or not the operation succeeded.
     */
    private boolean tryCheckpoint(JComboBox<String> checkpointSelection) {
        if (checkpointSelection == null || checkpointSelection.getSelectedItem() == null || controller == null)
            return false;
        badCheckpointMsg.setVisible(false);
        try {
            String checkpointName = (String) checkpointSelection.getSelectedItem();
            controller.getValueFunction().loadCheckpoint(Paths.get(checkpointLocation, checkpointName).toString());
            logger.debug("Loaded checkpoint: " + checkpointName);
            return true;
        } catch (IOException exception) {
            badCheckpointMsg.setVisible(true);
            logger.debug("Could not load checkpoint.");
            return false;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (arrowShape != null) {
            g.setColor(arrowColor);
            ((Graphics2D) g).fill(arrowShape);
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        mouseActive = true;
        mousePoint = e.getPoint();
        changeDisturbance();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseActive = false;
        changeDisturbance();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePoint = e.getPoint();
        changeDisturbance();
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    void applyDisturbance(IGameSerializable<CommandQWOP, S> game) {
        // TODO other game types.
        if (game instanceof GameQWOP) {
            ((GameQWOP) game).applyBodyImpulse(disturbanceX, disturbanceY);
        } else if (game instanceof GameQWOPCaching) {
            ((GameQWOPCaching) game).applyBodyImpulse(disturbanceX, disturbanceY);
        }
    }

    /**
     * Change the disturbance impulse to be applied at every timestep.
     */
    private void changeDisturbance() {
        if (mouseActive) {
            arrowShape = PanelRunner.createArrowShape(mousePoint, new Point(xOffsetPixels, yOffsetPixels), 80);
            float impulseGain = 0.02f;
            disturbanceX = impulseGain * (xOffsetPixels - mousePoint.x);
            disturbanceY = impulseGain * (yOffsetPixels - mousePoint.y);
        } else {
            disturbanceX = 0;
            disturbanceY = 0;
            arrowShape = null;
        }
    }
}
