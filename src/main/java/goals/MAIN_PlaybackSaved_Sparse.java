package goals;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import data.SavableFileIO;
import data.SavableSingleGame;
import game.GameLoader;
import tree.Node;
import ui.PanelRunner_Animated;

/**
 * Playback runs or sections of runs saved in SavableSingleRun files.
 *
 * @author matt
 */

public class MAIN_PlaybackSaved_Sparse extends JFrame {

    public GameLoader game;
    private PanelRunner_Animated runnerPane;

    /**
     * Window width
     */
    public static int windowWidth = 1920;

    /**
     * Window height
     */
    public static int windowHeight = 1000;


    private File saveLoc = new File("src/main/resources/saved_data/");

    private List<Node> leafNodes = new ArrayList<>();

    /**
     * What point to start displaying from (to skip any prefix).
     */
    public int startPt = 4;

    public static void main(String[] args) {
        MAIN_PlaybackSaved_Sparse mc = new MAIN_PlaybackSaved_Sparse();
        mc.setup();
        mc.run();
    }

    public void setup() {
        /* Runner pane */
        runnerPane = new PanelRunner_Animated();
        runnerPane.activateTab();
        runnerPane.yOffsetPixels = 600;
        this.add(runnerPane);
        Thread runnerThread = new Thread(runnerPane);
        runnerThread.start();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(windowWidth, windowHeight));
        this.setContentPane(this.getContentPane());
        this.pack();
        this.setVisible(true);
        repaint();
    }

    public void run() {
        File[] allFiles = saveLoc.listFiles();
        if (allFiles == null) throw new RuntimeException("Bad directory given: " + saveLoc.getName());

        List<File> playbackFiles = new ArrayList<>();
        for (File f : allFiles) {
            if (f.getName().contains("recoveries") || f.getName().contains("Prefix") && !f.getName().contains(
                    "unsuccessful")) {
                playbackFiles.add(f);
            }
        }
        Collections.shuffle(playbackFiles);

        SavableFileIO<SavableSingleGame> fileIO = new SavableFileIO<>();
        for (File f : playbackFiles) {
            Node rootNode = new Node();

            List<SavableSingleGame> loadedGames = new ArrayList<>();
            fileIO.loadObjectsToCollection(f, loadedGames);
            Node.makeNodesFromRunInfo(loadedGames, rootNode, -1);
            leafNodes.clear();
            rootNode.getLeaves(leafNodes);
            Node endNode = leafNodes.get(0);
            Node startNode = endNode;
            while (startNode.getTreeDepth() > startPt) {
                startNode = startNode.getParent();
            }
            runnerPane.simRunToNode(startNode, endNode.getParent().getParent()); // Leaving off the last two because they
            // usually are stupid.

            repaint();

            while (!runnerPane.isFinishedWithRun()) {
                runnerPane.repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
