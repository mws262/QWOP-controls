package ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.jfree.chart.plot.XYPlot;

import game.GameLoader;
import game.State;
import actions.Action;
import actions.ActionQueue;
import transformations.ITransform;
import tree.Node;
import transformations.Transform_Autoencoder;

/**
 * This plotter makes plots from all the states along a single run, not
 * just at the nodes. This involves re-simulating the run before plotting
 * to re-obtain those states.
 *
 * @author matt
 */
public class PanelPlot_SingleRun extends PanelPlot implements KeyListener {

    /**
     * Copy of the game used to obtain all the states along a single run by re-simulating it.
     */
    private GameLoader game;

    /**
     * Transformer to use to transform normal states into reduced coordinates.
     */
    private ITransform transformer = new Transform_Autoencoder("src/main/resources/tflow_models" +
            "/AutoEnc_72to8_6layer.pb", 8);//new Transform_Identity();

    /**
     * Stores the qwop actions we're going to execute.
     */
    private ActionQueue actionQueue = new ActionQueue();

    /**
     * List of all the states that we got from simulating. Not just at nodes.
     */
    private List<State> stateList = new ArrayList<>();
    private List<float[]> transformedStates = new ArrayList<>();
    private List<boolean[]> commandList = new ArrayList<>();

    /**
     * How many plots to squeeze in one displayed row.
     */
    private int plotsPerView;

    /**
     * Which plot, in the grid of potential plots, is currently being plotted in the first spot on the left.
     */
    private int firstPlotRow = 0;

    /**
     * Total number of plots -- not necessarily all displayed at once.
     */
    private final int numPlots;

    /**
     * Node that we're plotting the actions/states up to.
     */
    private Node selectedNode;

    public PanelPlot_SingleRun(int numberOfPlots) {
        super(numberOfPlots);
        game = new GameLoader();

        numPlots = transformer.getOutputStateSize();
        this.plotsPerView = numberOfPlots;
        addKeyListener(this);
        setFocusable(true);
    }

    /**
     * Run the simulation to collect the state info we want to plot.
     */
    private void simRunToNode(Node node) {
        stateList.clear();
        transformedStates.clear();
        commandList.clear();
        actionQueue.clearAll();
        game.makeNewWorld();
        Action[] actionSequence = node.getSequence();
        actionQueue.addSequence(actionSequence);
        for (Action a : actionSequence) {
            System.out.println(a);
        }

        stateList.add(game.getCurrentState()); // Add initial state.
        while (!actionQueue.isEmpty()) {
            boolean[] nextCommand = actionQueue.pollCommand(); // Get and remove the next keypresses
            game.stepGame(nextCommand[0], nextCommand[1], nextCommand[2], nextCommand[3]); // Execute timestep.
            stateList.add(game.getCurrentState());
            commandList.add(nextCommand);
        }
    }

    @Override
    public void update(Node plotNode) { // Note that this is different from the other PlotPanes. It plots UP TO this
        // node rather than below this node.
        if (plotNode.getTreeDepth() == 0) return; // Nothing to see from root.
        selectedNode = plotNode;
        simRunToNode(plotNode);
        transformedStates = transformer.transform(stateList); // Dimensionally reduced states

        changePlots();
    }

    public void changePlots() {
        requestFocus();

        Iterator<Entry<XYPlot, PlotDataset>> it = plotsAndData.entrySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            if (count >= numPlots) break;
            Entry<XYPlot, PlotDataset> plotAndData = it.next();
            XYPlot pl = plotAndData.getKey();
            PlotDataset dat = plotAndData.getValue();

            int currCol = count + firstPlotRow * plotsPerView;
            Float[] xData = transformedStates.stream().map(ts -> ts[currCol]).toArray(Float[]::new);
            Float[] yData = commandList.stream().map(b -> (float) ((b[0] ? 1 : 0) + (b[1] ? 2 : 0) + (b[2] ? 4 :
                    0) + (b[3] ? 8 : 0))).toArray(Float[]::new);
            Color[] cData =
                    IntStream.range(0, yData.length).mapToObj(i -> Node.getColorFromTreeDepth((int) (i / (float) xData.length * (float) selectedNode.getTreeDepth()))).toArray(Color[]::new);

            pl.getRangeAxis().setLabel("Command combination");
            pl.getDomainAxis().setLabel(State.ObjectName.values()[firstPlotRow].toString() + " " +
                    State.StateName.values()[count].toString());

            dat.addSeries(0, Arrays.copyOf(xData, xData.length - 1), yData, cData); // Have more states than actions,
            // so will kill the last one.

            setPlotBoundsFromData(pl, xData, yData);
            count++;
        }
        applyUpdates();
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (selectedNode == null) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                break;
            case KeyEvent.VK_LEFT:
                break;
            case KeyEvent.VK_UP:
                if (firstPlotRow <= 0) return;
                firstPlotRow--;
                break;
            case KeyEvent.VK_DOWN:
                if (firstPlotRow >= (transformer.getOutputStateSize() - plotsPerView) / plotsPerView) return;
                firstPlotRow++;
                break;
        }
        changePlots();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void plotClicked(int plotIdx) {}
}
