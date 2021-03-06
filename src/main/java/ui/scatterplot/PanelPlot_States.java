package ui.scatterplot;

import com.fasterxml.jackson.annotation.JsonProperty;
import game.action.Command;
import game.qwop.IStateQWOP.ObjectName;
import game.qwop.StateQWOP;
import game.state.IState;
import game.state.StateVariable6D.StateName;
import org.jfree.chart.plot.XYPlot;
import tree.Utility;
import tree.node.NodeGameExplorableBase;
import tree.node.NodeGameGraphicsBase;
import tree.node.filter.NodeFilter_Downsample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Pane for displaying plots of various state variables. Click each plot to pull up a menu for selecting data.
 * A tab.
 *
 * @author Matt
 */
public class PanelPlot_States<C extends Command<?>, S extends IState> extends PanelPlot<C, S> implements ItemListener {

    /**
     * Maximum allowed datapoints. Will downsample if above. Prevents extreme lag.
     */
    private final NodeFilter_Downsample<C, S> plotDownsampler = new NodeFilter_Downsample<>(5000);

    /**
     * Node from which states are referenced.
     */
    private NodeGameGraphicsBase<?, C, S> selectedNode;

    /**
     * Which plot index has an active menu.
     */
    private int activePlotIdx = 0;

    /**
     * Body parts associated with each plot and axis.
     */
    private final ObjectName[] plotObjectsX;
    private final ObjectName[] plotObjectsY;

    /**
     * StateQWOP variables associated with each plot and axis.
     */
    private final StateName[] plotStatesX;
    private final StateName[] plotStatesY;

    /**
     * Drop down menus for the things to plot.
     */
    private final JComboBox<String> objListX;
    private final JComboBox<String> stateListX;
    private final JComboBox<String> objListY;
    private final JComboBox<String> stateListY;

    /**
     * Menu for selecting which data is displayed.
     */
    private final JDialog menu;

    private int countDataCollect = 0;

    private final String name;

    public PanelPlot_States(@JsonProperty("name") String name, @JsonProperty("numberOfPlots") int numPlots) {
        super(numPlots);
        this.name = name;
        plotObjectsX = new ObjectName[numPlots];
        plotObjectsY = new ObjectName[numPlots];
        plotStatesX = new StateName[numPlots];
        plotStatesY = new StateName[numPlots];

        // Make string arrays of the body part and state variable names.
        int count = 0;
        // String names of the body parts.
        String[] objNames = new String[ObjectName.values().length];
        for (ObjectName obj : ObjectName.values()) {
            objNames[count] = obj.name();
            count++;
        }
        count = 0;
        // String names of the state variables.
        String[] stateNames = new String[StateName.values().length];
        for (StateName st : StateName.values()) {
            stateNames[count] = st.name();
            count++;
        }

        // Initial plots to display
        for (int i = 0; i < numPlots - 1; i++) {
            plotObjectsX[i] = ObjectName.values()[Utility.randInt(0, numPlots - 1)];
            plotStatesX[i] = StateName.values()[Utility.randInt(0, numPlots - 1)];
            plotObjectsY[i] = ObjectName.values()[Utility.randInt(0, numPlots - 1)];
            plotStatesY[i] = StateName.values()[Utility.randInt(0, numPlots - 1)];
        }

        // Drop down menus
        objListX = new JComboBox<>(objNames);
        stateListX = new JComboBox<>(stateNames);
        objListY = new JComboBox<>(objNames);
        stateListY = new JComboBox<>(stateNames);

        objListX.addItemListener(this);
        stateListX.addItemListener(this);
        objListY.addItemListener(this);
        stateListY.addItemListener(this);

        menu = new JDialog(); // Pop up box for the menus.
        menu.setLayout(new GridLayout(2, 4));
        menu.add(new JLabel("X-axis", SwingConstants.CENTER));
        menu.getContentPane().add(objListX);
        menu.getContentPane().add(stateListX);
        menu.add(new JLabel("Y-axis", SwingConstants.CENTER));
        menu.getContentPane().add(objListY);
        menu.getContentPane().add(stateListY);

        menu.setAlwaysOnTop(true);
        menu.pack();
        menu.setVisible(false); // Start with this panel hidden.
    }

    @Override
    public void update(NodeGameGraphicsBase<?, C, S> selectedNode) {
        this.selectedNode = selectedNode;
        // Fetching new data.
        List<NodeGameExplorableBase<?, C, S>> nodesBelow = new ArrayList<>();
        if (selectedNode != null) {
            selectedNode.recurseDownTreeInclusive(nodesBelow::add);

            // Reduce the list size to something which renders quickly.
            plotDownsampler.filter(nodesBelow);

            Iterator<Entry<XYPlot, PlotDataset>> it = plotsAndData.entrySet().iterator();
            countDataCollect = 0;
            while (it.hasNext()) {
                Entry<XYPlot, PlotDataset> plotAndData = it.next();
                XYPlot pl = plotAndData.getKey();
                PlotDataset dat = plotAndData.getValue();

                Float[] xData =
						nodesBelow.stream().map(n -> ((StateQWOP) n.getState()).getStateVariableFromName(plotObjectsX[countDataCollect]).getStateByName(plotStatesX[countDataCollect])).toArray(Float[]::new); // Crazy new Java 8!
                Float[] yData =
						nodesBelow.stream().map(n -> ((StateQWOP) n.getState()).getStateVariableFromName(plotObjectsY[countDataCollect]).getStateByName(plotStatesY[countDataCollect])).toArray(Float[]::new); // Crazy new Java 8!
                Color[] cData =
						nodesBelow.stream().map(n -> NodeGameGraphicsBase.getColorFromTreeDepth(n.getTreeDepth(),
                                NodeGameGraphicsBase.lineBrightnessDefault)).toArray(Color[]::new);

                setPlotBoundsFromData(pl, xData, yData);

                dat.addSeries(0, xData, yData, cData);
                pl.getRangeAxis().setLabel(plotObjectsX[countDataCollect].toString() + " " + plotStatesX[countDataCollect].toString());
                pl.getDomainAxis().setLabel(plotObjectsY[countDataCollect].toString() + " " + plotStatesY[countDataCollect].toString());
                countDataCollect++;
            }
            //addCommandLegend(firstPlot);
            applyUpdates();
        }
    }

    @Override
    public void deactivateTab() {
        super.deactivateTab();
        menu.setVisible(false);
    }

    @Override
    public void plotClicked(int plotIdx) {
        activePlotIdx = plotIdx;
        menu.setTitle("Select plot " + (plotIdx + 1) + " data.");
        int menuYOffset = -30; // Offsets to put the data selection menu right above the correct panel.
        int menuXOffset = 30;
        menu.setLocation(plotPanels[plotIdx].getX() + menuXOffset, menuYOffset);
        objListX.setSelectedIndex(plotObjectsX[plotIdx].ordinal()); // Make the drop down menus match the current plots.
        objListY.setSelectedIndex(plotObjectsY[plotIdx].ordinal());
        stateListX.setSelectedIndex(plotStatesX[plotIdx].ordinal());
        stateListY.setSelectedIndex(plotStatesY[plotIdx].ordinal());
        menu.setVisible(true);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        int state = e.getStateChange();
        if (state == ItemEvent.SELECTED) {
            if (e.getSource() == objListX) {
                plotObjectsX[activePlotIdx] = ObjectName.valueOf((String) e.getItem());
            } else if (e.getSource() == objListY) {
                plotObjectsY[activePlotIdx] = ObjectName.valueOf((String) e.getItem());
            } else if (e.getSource() == stateListX) {
                plotStatesX[activePlotIdx] = StateName.valueOf((String) e.getItem());
            } else if ((e.getSource() == stateListY)) {
                plotStatesY[activePlotIdx] = StateName.valueOf((String) e.getItem());
            } else {
                throw new RuntimeException("Unknown item status in plots from: " + e.getSource().toString());
            }
            update(selectedNode);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
