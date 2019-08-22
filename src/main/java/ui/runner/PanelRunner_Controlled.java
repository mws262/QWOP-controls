package ui.runner;

import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.IController;
import game.GameConstants;
import game.GameUnified;
import game.IGameSerializable;
import game.action.*;
import game.action.Action;
import tree.node.NodeQWOPExplorable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Basic panel for displaying the runner under controls. Inheriting from this class is the best way to get anything
 * besides basic behavior.
 *
 * @param <C> Controller type being used. Must implement the IController interface.
 * @param <G> Game implementation used. Must implement the IGameInternal interface.
 */
public class PanelRunner_Controlled<C extends Command<?>, CON extends IController<C>, G extends IGameSerializable<C>> extends PanelRunner {

    /**
     * Controller being visualized.
     */
    CON controller;

    /**
     * Game instance being used. Its state size should match what the controller needs.
     */
    G game;

    /**
     * Actions are queued as the controller provides them.
     */
    private ActionQueue<C> actionQueue = new ActionQueue<>();

    /**
     * Action most recently returned by the controller.
     */
    private Action<C> mostRecentAction;

    /**
     * Is this an active window? If its in a hidden tab or something, then we want to deactivate all the internal
     * behavior.
     */
    private boolean active = false;

    /**
     * Button for resetting the runner to the initial configuration.
     */
    private JButton resetButton;

    /**
     * Name of this panel. Used when inserted as a tab.
     */
    public final String name;

    /**
     * If the controller needs an assigned action generator for nodes, then this can be externally set. Not the best
     * solution, but not many controllers need this anyway.
     */
    public IActionGenerator actionGenerator;

    /**
     * Number of layout row slots for the layout manager.
     */
    final int layoutRows = 25;

    /**
     * Number of layout column slots for the layout manager.
     */
    final int layoutColumns = 15;

    /**
     * Constraints for the layout manager.
     */
    GridBagConstraints constraints = new GridBagConstraints();

    /**
     * Handles advancing the game and querying the controller.
     */
    private ControllerExecutor controllerExecutor;

    private static final int normalSimRate = 35;

    private JCheckBox pauseToggle;

    private JCheckBox fastToggle;

    private JCheckBox serializeToggle;

    private JLabel gameDistance;

    private volatile float currentGameX = 0f;

    private Thread gameThread;

    public PanelRunner_Controlled(@JsonProperty("name") String name, G game, CON controller) {
        this.name = name;
        this.controller = controller;
        this.game = game;
        this.setName(name);

        // Reset button setup.
        resetButton = new JButton("Restart");
        resetButton.addActionListener(e -> {
            deactivateTab();
            activateTab();
        });
        resetButton.setPreferredSize(new Dimension(50, 25));

        // Setup the panel layout.
        GridBagLayout layout = new GridBagLayout();
        layout.columnWeights = new double[layoutColumns];
        layout.rowWeights = new double[layoutRows];
        Arrays.fill(layout.columnWeights, 1);
        Arrays.fill(layout.rowWeights, 1);
        setLayout(layout);

        constraints.gridx = layoutColumns - 1;
        constraints.gridy = layoutRows - 1;
        constraints.ipady = 0;
        constraints.ipadx = 100;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0,0,0,0);
        constraints.anchor = GridBagConstraints.PAGE_END;
        JLabel gameDescription = new JLabel("Provided game state dimension: " + game.getStateDimension());
        add(gameDescription, constraints);

        constraints.gridx = 0;
        constraints.gridy = layoutRows - 1;
        add(resetButton, constraints);

        // Options checkboxes.
        JPanel checkboxes = new JPanel();
        pauseToggle = new JCheckBox("Pause");
        pauseToggle.setToolTipText("Pause the controlled game simulation.");
        pauseToggle.setOpaque(false);
        checkboxes.add(pauseToggle);

        fastToggle = new JCheckBox("FAST!");
        fastToggle.setToolTipText("Simulate really fast.");
        fastToggle.setOpaque(false);
        fastToggle.addActionListener(e -> {
            if (fastToggle.isSelected()) {
                controllerExecutor.tsDelay = 0;
            } else {
                controllerExecutor.tsDelay = normalSimRate;
            }
        });
        checkboxes.add(fastToggle);

        constraints.gridx = 1;
        add(checkboxes, constraints);

        serializeToggle = new JCheckBox("Serialized state");
        serializeToggle.setToolTipText("Use the full game state, by serializing and deserializing the entire game instance" +
                ".");
        serializeToggle.setOpaque(false);
        checkboxes.add(serializeToggle);
        
        // Distance and time indicator.
        gameDistance = new JLabel("");
        gameDistance.setOpaque(false);
        gameDistance.setFont(gameDistance.getFont().deriveFont(24f));
        constraints.gridx = layoutColumns/2;
        constraints.gridy = 0;
        add(gameDistance, constraints);
    }

    /**
     * Can be useful to keep drawing the runner in a frozen position when an invalid controller is selected.
     */
    void activateDrawingButNotController() {
        active = true;
    }

    /**
     * Will be called before each game timestep. Override for anything specific.
     * @param game Game to apply a disturbance to.
     */
    private void applyDisturbance(G game) {}

    @Override
    public void paintComponent(Graphics g) {
        if (!active) return;
        super.paintComponent(g);
        if (game != null) {
            game.draw(g, runnerScaling, xOffsetPixels, yOffsetPixels);

            if (actionQueue.peekThisAction().peek() instanceof CommandQWOP) {
                boolean[] mostRecentKeys = actionQueue.isEmpty() ? new boolean[]{false, false, false, false} :
                        (boolean[]) actionQueue.peekThisAction().peek().get();
                keyDrawer(g, mostRecentKeys[0], mostRecentKeys[1], mostRecentKeys[2], mostRecentKeys[3]);
            }
        }
    }

    @Override
    public void activateTab() {
        active = true;
        if (controller != null) {
            controllerExecutor = new ControllerExecutor();
            controllerExecutor.reset();
            gameThread = new Thread(controllerExecutor);
            gameThread.start();
        }
    }

    @Override
    public void deactivateTab() {
        actionQueue.clearAll();
        active = false;
        if (gameThread != null && gameThread.isAlive()) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public G getGame() {
        return game;
    }

    /**
     * Handles advancing the game and querying the controller. Should be run on a timer.
     */
    private class ControllerExecutor implements Runnable {

        private NodeQWOPExplorable node;

        int tsDelay = normalSimRate;

        /**
         * Reset the game.
         */
        public void reset() {
            game.makeNewWorld();
            actionQueue.clearAll();
            node = null;
            currentGameX = 0f;
            if (fastToggle.isSelected()) {
                tsDelay = 0;
            }
        }
        @Override
        public void run() {
            while (active) {
                if (!pauseToggle.isSelected() && currentGameX < 100f) {
                    long timeInitial = System.currentTimeMillis();
                    // If the queue is out of actions, then ask the controller for a new one.
                    if (actionQueue.isEmpty()) {
                        // Either make the first node since the game began, or add a child to the previous node.
                        if (node == null) {
                            if (actionGenerator != null) {
                                node = new NodeQWOPExplorable(game.getCurrentState(), actionGenerator);
                            } else {
                                node = new NodeQWOPExplorable(game.getCurrentState());
                            }
                        } else {
                            node = node.addBackwardsLinkedChild(mostRecentAction, game.getCurrentState());
                        }
                        if (serializeToggle.isSelected()) {
                            mostRecentAction = controller.policy(node, game);
                        } else {
                            mostRecentAction = controller.policy(node);
                        }

                        actionQueue.addAction(mostRecentAction);
                    }
                    applyDisturbance(game);
                    game.step(actionQueue.pollCommand());
                    currentGameX = (game.getCurrentState().getCenterX() - GameUnified.getInitialState().getCenterX()) / GameConstants.worldScale;
                    gameDistance.setText(String.format("%.1fm  %.1fs",
                            currentGameX, game.getTimestepsThisGame() * GameConstants.timestep));

                    if (tsDelay > 0) {
                        try {
                            Thread.sleep(Math.max(0, tsDelay - (System.currentTimeMillis() - timeInitial)));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
