package userInterface.main;

import java.awt.BasicStroke;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import operations.sensors.Sensorable;
import operations.sensors.TimeStamp;

public class ChartCreator {

	public static ChartCanvas getChart(GridPane grid) {
		XYSeriesCollection collection = new XYSeriesCollection();
		JFreeChart chart = ChartFactory.createXYLineChart("Chart", "X", "Y", collection);

		// chart settings
		XYPlot plot = chart.getXYPlot();
		// set font
		Font font = new Font("Dialog", Font.PLAIN, 14);
		ValueAxis domainAx = plot.getDomainAxis();
		domainAx.setTickLabelFont(font);
		ValueAxis rangeAx = plot.getRangeAxis();
		rangeAx.setTickLabelFont(font);
		// do not include zero point when chart is far away
		((NumberAxis) rangeAx).setAutoRangeIncludesZero(false);
		// change line thickness
		plot.getRenderer().setSeriesStroke(0, new BasicStroke(3));

		// fire chart repaint on demand
		chart.setNotify(false);

		// create JavaFX object and remove all handlers
		ChartCanvas canvas = new ChartCanvas(chart);
		canvas.removeMouseHandler(canvas.getMouseHandler("tooltip"));
		canvas.removeMouseHandler(canvas.getMouseHandler("scroll"));
		canvas.removeMouseHandler(canvas.getMouseHandler("anchor"));
		canvas.removeMouseHandler(canvas.getMouseHandler("pan"));
		canvas.removeMouseHandler(canvas.getMouseHandler("dispatch"));

		// add to display

		Platform.runLater(() -> {
			grid.add(canvas, 0, 0);
			canvas.heightProperty().bind(grid.heightProperty());
			canvas.widthProperty().bind(grid.widthProperty());
		});

		decorCanvas(canvas);

		return canvas;
	}

	public static void changeSeries(ChartCanvas chartDisplay, Sensorable dataObject, XYSeries series) {
		String title = dataObject.getName() + "  f( [" + TimeStamp.getInstance().getUnit() + "] ) = ["
				+ dataObject.getUnit() + "]";

		JFreeChart chart = chartDisplay.getChart();
		chart.setTitle(title);

		XYPlot plot = chart.getXYPlot();
		XYSeriesCollection collection = (XYSeriesCollection) plot.getDataset();
		if (collection.getSeriesCount() != 0) {
			collection.removeAllSeries();
		}
		collection.addSeries(series);

		ValueAxis domainAx = plot.getDomainAxis();
		domainAx.setLabel(dataObject.getXAxis().getUnit());
		ValueAxis rangeAx = plot.getRangeAxis();
		rangeAx.setLabel(dataObject.getUnit());

	}

	/**
	 * Has to be called by GUI thread!
	 * 
	 * @param chart
	 */
	public static void actualizeChart(ChartCanvas chart) {
		chart.getChart().setNotify(false);
		chart.getChart().setNotify(true);

		chart.requestFocus();
	}

	private static void decorCanvas(ChartCanvas canvas) {
		canvas.getChart().removeLegend();

	}

}
