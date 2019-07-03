package ui.scatterplot;

import com.fasterxml.jackson.annotation.JsonProperty;
import game.action.Action;
import game.action.ActionQueue;
import game.*;
import game.state.IState;
import game.state.State;
import game.state.StateVariable;
import org.jfree.chart.plot.XYPlot;
import game.state.transform.ITransform;
import game.state.transform.Transform_Autoencoder;
import tree.node.NodeQWOPExplorableBase;
import tree.node.NodeQWOPGraphicsBase;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.IntStream;

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
    private IGameInternal game;

    /**
     * Transformer to use to transform normal states into reduced coordinates.
     */
    private ITransform transformer = new Transform_Autoencoder("src/main/resources/tflow_models" +
            "/AutoEnc_72to8_6layer.pb", 8);//new Transform_Identity();

    /**
     * Stores the qwop game.action we're going to execute.
     */
    private ActionQueue actionQueue = new ActionQueue();

    /**
     * List of all the states that we got from simulating. Not just at nodes.
     */
    private List<IState> stateList = new ArrayList<>();
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
     * Node that we're plotting the game.action/states up to.
     */
    private NodeQWOPExplorableBase<?> selectedNode;

    private final String name;

    public PanelPlot_SingleRun(@JsonProperty("name") String name, @JsonProperty("numberOfPlots") int numberOfPlots) {
        super(numberOfPlots);
        this.name = name;
        game = new GameUnified();

        numPlots = transformer.getOutputSize();
        this.plotsPerView = numberOfPlots;
        addKeyListener(this);
        setFocusable(true);
    }

    /**
     * Run the simulation to collect the state info we want to plot.
     */
    private void simRunToNode(NodeQWOPExplorableBase<?> node) {
        stateList.clear();
        transformedStates.clear();
        commandList.clear();
        actionQueue.clearAll();
        game.makeNewWorld();

        ArrayList<Action> actionList = new ArrayList<>();
        node.getSequence(actionList);
        actionQueue.addSequence(actionList);
        for (Action a : actionList) {
            System.out.println(a);
        }

        stateList.add(game.getCurrentState()); // Add initial state.
        while (!actionQueue.isEmpty()) {
            boolean[] nextCommand = actionQueue.pollCommand(); // Get and remove the next keypresses
            game.step(nextCommand[0], nextCommand[1], nextCommand[2], nextCommand[3]); // Execute timestep.
            stateList.add(game.getCurrentState());
            commandList.add(nextCommand);
        }
    }

    @Override
    public void update(NodeQWOPGraphicsBase<?> plotNode) { // Note that this is different from the other PlotPanes.
        // It plots UP TO this
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
                    IntStream.range(0, yData.length).mapToObj(i -> NodeQWOPGraphicsBase.getColorFromTreeDepth((int) (i / (float) xData.length * (float) selectedNode.getTreeDepth()), NodeQWOPGraphicsBase.lineBrightnessDefault)).toArray(Color[]::new);

            pl.getRangeAxis().setLabel("Command combination");
            pl.getDomainAxis().setLabel(State.ObjectName.values()[firstPlotRow].toString() + " " +
                    StateVariable.StateName.values()[count].toString());

            dat.addSeries(0, Arrays.copyOf(xData, xData.length - 1), yData, cData); // Have more states than game.action,
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
                if (firstPlotRow >= (transformer.getOutputSize() - plotsPerView) / plotsPerView) return;
                firstPlotRow++;
                break;
            default:
                break;
        }
        changePlots();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void plotClicked(int plotIdx) {}

    @Override
    public String getName() {
        return name;
    }
}