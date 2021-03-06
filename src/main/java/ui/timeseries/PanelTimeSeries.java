package ui.timeseries;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import ui.IUserInterface.TabbedPaneActivator;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public abstract class PanelTimeSeries extends JPanel implements TabbedPaneActivator {

    /**
     * How many plots per row?
     */
    private static final int plotsPerRow = 6;

    /**
     * Array of the numberOfPlots number of plots we make.
     */
    private ChartPanel[] plotPanels;

    /**
     * Background color for the plots.
     */
    private Color plotBackgroundColor = new Color(230, 230, 230);

    /**
     * Max in time series before old begin to be removed.
     */
    public int maxPtsPerPlot = 200;

    protected Map<XYPlot, TimeSeriesCollection> plotsAndData = new LinkedHashMap<>(); // Retains order of insertion.

    public PanelTimeSeries(@JsonProperty("numberOfPlots") int numberOfPlots) {
        /**
         * How many plots total?
         */
        plotPanels = new ChartPanel[numberOfPlots];

        int numRows = (int) Math.ceil(numberOfPlots / (double) plotsPerRow);
        JPanel[] rowPanels = new JPanel[numRows];
        // Panel for each row. Makes it easier with BoxLayout.
        for (int j = 0; j < numRows; j++) {
            JPanel rowPanel = new JPanel();
            rowPanels[j] = rowPanel;
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
            this.add(rowPanel);
        }
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        for (int i = 0; i < numberOfPlots; i++) {
            TimeSeries series = new TimeSeries("");
            series.setMaximumItemCount(maxPtsPerPlot);
            TimeSeriesCollection plData = new TimeSeriesCollection(series);

            JFreeChart chart = createChart(plData, null); // Null means no title yet
            XYPlot pl = chart.getXYPlot();
            pl.getRangeAxis().setRange(0, 40000);
            plotsAndData.put(chart.getXYPlot(), plData);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPopupMenu(null);
            chartPanel.setDomainZoomable(false);
            chartPanel.setRangeZoomable(false);
            chartPanel.setVisible(true);
            plotPanels[i] = chartPanel;

            rowPanels[i / plotsPerRow].add(chartPanel);
        }
    }

    /**
     * Command all plots to apply updates.
     */
    public void applyUpdates() {
        Arrays.stream(plotPanels).forEach(panel -> panel.getChart().fireChartChanged());
    }

    public void addToSeries(float value, int plotNum, int seriesNum) {
        TimeSeriesCollection ts = plotsAndData.get(plotPanels[plotNum].getChart().getXYPlot());
        ts.getSeries(seriesNum).add(RegularTimePeriod.createInstance(Millisecond.class, new Date(),
				TimeZone.getDefault()), value);
        ts.getSeries(seriesNum).removeAgedItems(false);
    }

    /**
     * My default settings for each plot.
     */
    private JFreeChart createChart(XYDataset dataset, String name) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                name,
                "",
                "",
                dataset,
                false,
                false,
                false);

        chart.setBackgroundPaint(plotBackgroundColor);
        chart.setPadding(new RectangleInsets(-0, -8, -2, -5)); // Pack em in really tight.
        chart.setBorderVisible(false);

        return chart;
    }

    @JsonProperty("numberOfPlots")
    public int getNumberOfPlots() {
        return plotPanels.length;
    }
}
