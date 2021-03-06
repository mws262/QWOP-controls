package goals.perturbation_analysis;

import data.SavableFileIO;
import data.SavableSingleGame;
import game.IGameInternal;
import game.action.Action;
import game.action.ActionQueue;
import game.qwop.CommandQWOP;
import game.qwop.GameQWOP;
import game.qwop.StateQWOP;
import tree.node.NodeGameGraphicsBase;
import ui.runner.PanelRunner_MultiState;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.List;
import java.util.*;

/**
 * Takes a known running sequence, adds a disturbance impulse at one point along the trajectory in a number of
 * different directions. Visualizes when each of the disturbed runners falls. Displays arrow above each failed runner
 * indicating the direction of the impulse which lead to this fall.
 *
 * @author matt
 */
public class MAIN_PerturbationImpulse extends JFrame {

    /**
     * Number of disturbed runners simulated. Each will receive an impulse in a direction around the unit circle,
     * evenly divided by the number of runners.
     */
    private int numPerturbedRunners = 30;

    /**
     * Location of the perturbation, in terms of game.command along the known, good sequence.
     */
    private int perturbationLocation = 15;

    /**
     * Scaling of the applied disturbance impulse. Directions are along the unit circle, with the magnitude specified
     * here.
     */
    private float impulseScaling = 40f;

    /**
     * Display snapshots of the runners at this number of timestep intervals.
     */
    private int drawInterval = 30;

    public static void main(String[] args) {
        String fileName = "src/main/resources/saved_data/example_run.SavableSingleGame";

        MAIN_PerturbationImpulse viewPerturbations = new MAIN_PerturbationImpulse();
        viewPerturbations.run(fileName);
    }

    public void run(String fileName) {
        // Vis resetGame.
        PanelRunner_MultiStateWithArrows panelRunner = new PanelRunner_MultiStateWithArrows("Runner");
        panelRunner.activateTab();
        getContentPane().add(panelRunner);
        setPreferredSize(new Dimension(2000, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        // Load a saved game.
        List<SavableSingleGame<CommandQWOP, StateQWOP>> gameList = new ArrayList<>();
        SavableFileIO<SavableSingleGame<CommandQWOP, StateQWOP>> fileIO = new SavableFileIO<>();
        fileIO.loadObjectsToCollection(new File(fileName), gameList);

        assert !gameList.isEmpty();

        List<Action<CommandQWOP>> baseActions = gameList.get(0).actions;

        // Simulate the base game.command.
        IGameInternal<CommandQWOP, StateQWOP> game = new GameQWOP();

        // These are the runners which will be perturbed.
        List<GameQWOP> perturbedGames = new ArrayList<>();
        for (int i = 0; i < numPerturbedRunners; i++) {
            perturbedGames.add(new GameQWOP());
        }
        ActionQueue<CommandQWOP> actionQueue = new ActionQueue<>();
        actionQueue.addSequence(baseActions);

        // Get all runners to the perturbation location.
        while (actionQueue.getCurrentActionIdx() < perturbationLocation) {

            CommandQWOP command = actionQueue.pollCommand();
            game.step(command);

            for (IGameInternal<CommandQWOP, StateQWOP> perturbedGame : perturbedGames) {
                perturbedGame.step(command);
            }
        }

        // Apply impulse disturbances.
        Map<IGameInternal, float[]> gameToDisturbanceDir = new HashMap<>();

        for (int i = 0; i < numPerturbedRunners; i++) {
            float[] disturbance = new float[]{(float) Math.cos((double) i / (double) numPerturbedRunners * 2. *
                    Math.PI), (float) Math.sin((double) i / (double) numPerturbedRunners * 2. * Math.PI)};
            gameToDisturbanceDir.put(perturbedGames.get(i), disturbance);
            perturbedGames.get(i).applyBodyImpulse(impulseScaling * disturbance[0], impulseScaling * disturbance[1]);
        }

        panelRunner.setMainState(game.getCurrentState());

        int count = 0;
        while (!actionQueue.isEmpty()) {
            CommandQWOP command = actionQueue.pollCommand();
            game.step(command);
            if (count % drawInterval == 0)
                panelRunner.addSecondaryState(game.getCurrentState(), Color.BLACK);

            // Step perturbed runners.
            for (int i = 0; i < perturbedGames.size(); i++) {
                IGameInternal<CommandQWOP, StateQWOP> thisGame = perturbedGames.get(i);
                thisGame.step(command);
                if (count % drawInterval == 0)
                    panelRunner.addSecondaryState(perturbedGames.get(i).getCurrentState(), NodeGameGraphicsBase.getColorFromScaledValue(i
                            , numPerturbedRunners, 0.8f));

                // Remove and draw if failure.
                if (thisGame.isFailed()) {
                    float[] distDir = gameToDisturbanceDir.get(thisGame);
                    panelRunner.addSecondaryStateWithArrow(thisGame.getCurrentState(), Color.RED, distDir);
                    perturbedGames.remove(thisGame);
                }
            }

            panelRunner.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
    }

    /**
     * Same as the superclass, but with the capability of displaying directed arrows above each runner.
     *
     * @author matt
     */
    static class PanelRunner_MultiStateWithArrows extends PanelRunner_MultiState {

        private static final int ARR_SIZE = 4;

        private List<Integer[]> arrowCoords = new Vector<>();

        PanelRunner_MultiStateWithArrows(String name) {
            super(name);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (Integer[] coord : arrowCoords) {
                drawArrow(g, coord[0], coord[1], coord[2], coord[3]);
            }
        }

        /**
         * Add a secondary state to draw. Same as {@link PanelRunner_MultiState#addSecondaryState(StateQWOP, Color)}
         * except with an arrow drawn from the runner center.
         *
         * @param state          StateQWOP to add to drawing list.
         * @param color          Color to outline the runner in.
         * @param arrowDirection Direction of the arrow.
         */
        void addSecondaryStateWithArrow(StateQWOP state, @SuppressWarnings("SameParameterValue") Color color,
                                        float[] arrowDirection) {
            super.addSecondaryState(state, color);
            Integer[] arrowCoord = new Integer[4];
            arrowCoord[0] = getOffset()[0] + (int) (state.getCenterX() * runnerScaling);
            arrowCoord[1] = getOffset()[1] - 100;
            arrowCoord[2] = (int) (50 * arrowDirection[0]) + arrowCoord[0];
            arrowCoord[3] = (int) (50 * arrowDirection[1]) + arrowCoord[1];
            arrowCoords.add(arrowCoord);
        }

        /**
         * Draw an arrow on screen.
         *
         * @param g1 Graphics object.
         * @param x1 X screen coordinate of arrow origin.
         * @param y1 Y screen coordinate of arrow origin.
         * @param x2 X screen coordinate of arrow endpoint.
         * @param y2 Y screen coordinate of arrow endpoint.
         */
        private void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
            Graphics2D g = (Graphics2D) g1.create();

            double dx = x2 - x1, dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            int len = (int) Math.sqrt(dx * dx + dy * dy);
            AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
            at.concatenate(AffineTransform.getRotateInstance(angle));
            g.transform(at);

            // Draw horizontal arrow starting in (0, 0)
            g.drawLine(0, 0, len, 0);
            g.fillPolygon(new int[]{len, len - ARR_SIZE, len - ARR_SIZE, len},
                    new int[]{0, -ARR_SIZE, ARR_SIZE, 0}, 4);
        }
    }
}
