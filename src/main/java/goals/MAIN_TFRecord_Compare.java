package goals;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import controllers.Controller_NearestNeighborApprox;
import controllers.Controller_NearestNeighborApprox.RunHolder;
import game.GameLoader;
import tree.Node;
import tree.Utility;
import ui.PanelRunner;


/**
 * Playback runs or sections of runs saved in SavableSingleRun files.
 *
 * @author matt
 */

@SuppressWarnings("serial")
public class MAIN_TFRecord_Compare extends JFrame implements Runnable {

    private GameLoader game = new GameLoader();

    private Controller_NearestNeighborApprox justForLoading;

    private Color backgroundColor = Color.DARK_GRAY; //new Color(GLPanelGeneric.darkBackground[0],GLPanelGeneric

    private boolean doneInit = false;

    public static void main(String[] args) {
        MAIN_TFRecord_Compare mc = new MAIN_TFRecord_Compare();
        mc.setup();

    }

    public void setup() {

        File saveLoc = new File(Utility.getExcutionPath() + "saved_data/training_data");

        File[] allFiles = saveLoc.listFiles();
        if (allFiles == null) throw new RuntimeException("Bad directory given: " + saveLoc.getName());

        List<File> exampleDataFiles = new ArrayList<>();
        int count = 0;
        for (File f : allFiles) {
            if (f.getName().contains("TFRecord") && !f.getName().contains("recovery")) {
                System.out.println("Found save file: " + f.getName());
                if (count < 5 && count > 0) { // 10 is bad? 9 meh, 8 good
                    exampleDataFiles.add(f);
                }
//8 not match
// 9 not match
                count++;
            }
        }

        justForLoading = new Controller_NearestNeighborApprox(exampleDataFiles);


        game.mainRunnerStroke = new BasicStroke(5);
        // .darkBackground[1],GLPanelGeneric.darkBackground[2]);
        Panel mainViewPanel = new Panel();
        this.setLayout(new BorderLayout());
        add(mainViewPanel, BorderLayout.CENTER);


        game.mainRunnerColor = Color.ORANGE;
        mainViewPanel.setBackground(backgroundColor);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Window height
        int windowHeight = 1000;
        // Window width
        int windowWidth = 1920;
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setContentPane(this.getContentPane());
        revalidate();
        repaint();
        pack();


        doneInit = true;
        Thread graphicsThread = new Thread(this);
        graphicsThread.start(); // Makes it smoother by updating the graphics faster than the timestep updates.

        setVisible(true);
        mainViewPanel.setVisible(true);
    }

    private Iterator<RunHolder> iter;

    @Override
    public void run() {
        iter = justForLoading.runs.iterator();
        while (true) {
            repaint();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private List<RunHolder> toDraw = new ArrayList<>();

    private void draw(Graphics g) {
        if (!doneInit) return;
        if (game != null) {
            if (iter.hasNext()) {
                RunHolder drawTraj = iter.next();
                toDraw.add(drawTraj);
            }

            for (int j = toDraw.size() - 1; j >= 0; j--) {
                RunHolder rh = toDraw.get(j);
                /* Runner coordinates to pixels. */
                float runnerScaling = 20f;
                int yOffsetPixels = 450;
                /* Drawing offsets within the viewing panel (i.e. non-physical) */
                int xOffsetPixels = 250;
                if (rh != null) {
                    int killThis = rh.actionDurations.get(0) + rh.actionDurations.get(1);
                    float specificXOffset = rh.states.get(killThis).state.body.getX();
                    int count = 0;
                    for (int i = 5 + killThis; i < rh.states.size(); i += rh.actionDurations.get(count)) {
                        count++;

                        //if (count % 2 != 0) continue;
                        game.drawExtraRunner((Graphics2D) g, rh.states.get(i).state, "",
                                runnerScaling, xOffsetPixels - (int) (runnerScaling * specificXOffset), yOffsetPixels,
                                Node.getColorFromScaledValue(2 * i, rh.states.size(),
                                        0.8f), PanelRunner.normalStroke);
                        if (count >= rh.actionDurations.size() - 1) break;
                    }
                }
                for (RunHolder rh2 : toDraw) {
                    int killThis = rh2.actionDurations.get(0) + rh2.actionDurations.get(1);
                    float specificXOffset = rh2.states.get(killThis).state.body.getX();
                    game.drawExtraRunner((Graphics2D) g, rh2.states.get(killThis).state, "",
                            runnerScaling, xOffsetPixels - (int) (runnerScaling * specificXOffset), yOffsetPixels,
                            Color.BLACK, PanelRunner.boldStroke);
                }
            }
        }
    }

    private class Panel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            if (!game.isGameInitialized()) return;
            super.paintComponent(g);
            draw(g);
        }
    }
}