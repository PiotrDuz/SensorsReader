package main.java.userInterface.main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import main.java.operations.sensors.Sensorable;
import main.java.operations.sensors.TimeStamp;

public class ChartCreator {

	public static ChartCanvas getChart(GridPane grid) {
		XYSeriesCollection collection = new XYSeriesCollection();
		JFreeChart chart = ChartFactory.createXYLineChart("Chart", "X", "Y", collection);
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
			collection.removeSeries(0);
		}
		collection.addSeries(series);

		plot.getDomainAxis().setLabel(null); // ("Czas: " + TimeStamp.getInstance().getUnit());
		plot.getRangeAxis().setLabel(null); // dataObject.getUnit());
	}

	public static void actualizeChart(ChartCanvas chart) {
		chart.getChart().setNotify(true);
		chart.getChart().setNotify(false);
	}

	private static void decorCanvas(ChartCanvas canvas) {
		canvas.getChart().removeLegend();

	}

}
