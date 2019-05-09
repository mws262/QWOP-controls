package goals.data_visualization;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JFrame;

import data.SavableFileIO;
import data.SavableSingleGame;
import tree.Node;
import ui.PanelRunner_Snapshot;

/**
 * Playback runs or sections of runs saved in SavableSingleRun files.
 *
 * @author matt
 */
public class MAIN_SnapshotAll extends JFrame {

    private PanelRunner_Snapshot snapshotPane;

    /**
     * Window width
     */
    public static int windowWidth = 1920;

    /**
     * Window height
     */
    public static int windowHeight = 1000;

    /**
     * Display up to this tree depth.
     */
    public static int playbackDepth = 5;

    /**
     * Location where the files to be played back are located. All SavableSingleGame files will be used.
     */
    private File saveLoc = new File("src/main/resources/saved_data/tmp_testing");

    public static void main(String[] args) {
        MAIN_SnapshotAll mc = new MAIN_SnapshotAll();
        mc.setup();
        mc.run();
    }

    /**
     * Set up the graphics for animating the runner.
     */
    public void setup() {
        snapshotPane = new PanelRunner_Snapshot();
        snapshotPane.activateTab();
        snapshotPane.yOffsetPixels = 600;
        add(snapshotPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        pack();
        setVisible(true);
        repaint();
    }

    public void run() {
        File[] allFiles = saveLoc.listFiles();

        List<File> playbackFiles = new ArrayList<>();
        for (File f : Objects.requireNonNull(allFiles)) {
            if (f.getName().contains("SavableSingleGame")) {
                playbackFiles.add(f);
            }
        }

        if (playbackFiles.isEmpty()) {
            System.out.println("No files found in specified directory. Quitting.");
            return;
        }

        Node rootNode = new Node();

        SavableFileIO<SavableSingleGame> fileIO = new SavableFileIO<>();
        List<SavableSingleGame> games = new ArrayList<>();

        for (File f : playbackFiles) {
            fileIO.loadObjectsToCollection(f, games);
        }

        Node.makeNodesFromRunInfo(games, rootNode, -1);
        Node currNode = rootNode;
        while (currNode.getTreeDepth() < playbackDepth && currNode.getChildCount() > 0) {
            currNode = currNode.getChildren()[0];
        }
        System.out.println(currNode.countDescendants());
        snapshotPane.update(currNode);
        repaint();
    }
}
