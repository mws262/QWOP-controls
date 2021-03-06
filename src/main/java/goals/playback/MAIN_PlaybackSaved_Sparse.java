package goals.playback;

import data.SavableFileIO;
import data.SavableSingleGame;
import game.qwop.GameQWOP;
import game.qwop.CommandQWOP;
import game.qwop.StateQWOP;
import tree.node.NodeGameGraphics;
import tree.node.NodeGameGraphicsBase;
import ui.runner.PanelRunner_Animated;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Playback runs or sections of runs saved in {@link SavableSingleGame} files.
 *
 * @author matt
 */

public class MAIN_PlaybackSaved_Sparse extends JFrame {

    private PanelRunner_Animated runnerPane;

    /**
     * Window width
     */
    public static int windowWidth = 1920;

    /**
     * Window height
     */
    public static int windowHeight = 1000;


    private File saveLoc = new File("src/main/resources/saved_data/11_2_18");

    private List<NodeGameGraphics<CommandQWOP, StateQWOP>> leafNodes = new ArrayList<>();

    /**
     * What point to start displaying from (to skip any prefix).
     */
    public int startPt = 0;

    public static void main(String[] args) {
        MAIN_PlaybackSaved_Sparse mc = new MAIN_PlaybackSaved_Sparse();
        mc.setup();
        mc.run();
    }

    public void setup() {
        /* Runner pane */
        runnerPane = new PanelRunner_Animated("Animated runner");
        runnerPane.activateTab();
        runnerPane.yOffsetPixels = 600;
        add(runnerPane);
        Thread runnerThread = new Thread(runnerPane);
        runnerThread.start();

        setTitle("Simulate saved game.command from file");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        pack();
        setVisible(true);
        repaint();
    }

    public void run() {
        File[] allFiles = saveLoc.listFiles();
        if (allFiles == null) throw new RuntimeException("Bad directory given: " + saveLoc.getName());

        List<File> playbackFiles = new ArrayList<>();
        for (File f : allFiles) {
            if (f.getName().contains("SavableSingleGame")) {
                playbackFiles.add(f);
            }
        }
        Collections.shuffle(playbackFiles);

        SavableFileIO<SavableSingleGame<CommandQWOP, StateQWOP>> fileIO = new SavableFileIO<>();
        for (File f : playbackFiles) {
            NodeGameGraphics<CommandQWOP, StateQWOP> rootNode = new NodeGameGraphics<>(GameQWOP.getInitialState());

            List<SavableSingleGame<CommandQWOP, StateQWOP>> loadedGames = new ArrayList<>();
            fileIO.loadObjectsToCollection(f, loadedGames);
            NodeGameGraphicsBase.makeNodesFromRunInfo(loadedGames, rootNode);
            leafNodes.clear();
            rootNode.getLeaves(leafNodes);
            NodeGameGraphics<CommandQWOP, StateQWOP> endNode = leafNodes.get(0);
            NodeGameGraphics<CommandQWOP, StateQWOP> startNode = endNode;
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
