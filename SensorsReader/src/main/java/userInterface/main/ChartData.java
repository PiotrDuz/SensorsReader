package main.java.userInterface.main;

import java.util.LinkedHashMap;

import java.util.concurrent.ConcurrentHashMap;

import org.jfree.chart.fx.ChartCanvas;
import org.jfree.data.xy.XYSeries;

import javafx.application.Platform;
import main.java.operations.sensors.Measurable;
import main.java.operations.sensors.SensorFactory;
import main.java.operations.sensors.Sensorable;
import main.java.operations.sensors.TimeStamp;
import main.java.operations.sensors.SensorFactory.Type;
import main.java.operations.sensors.combination.SensorCombinationFactory;
import main.java.userInterface.main.SensorPaneFactory.PaneValues;

/**
 * Class responsible for holding and handling data series that are displayed on
 * charts.<br>
 * Sets additional information displayed in MainWindow, in real-time.
 * 
 * @author Piotr Duzniak
 *
 */
public class ChartData {
	private int dataPointsNumber = 800;
	private int speedPointsNumber = 5;
	static private ChartData chartData;
	// multithreaded data
	private volatile Boolean isBusy = false;
	private volatile Boolean chartTickTock = false;

	ChartCanvas chartTop;
	ChartCanvas chartBottom;

	/**
	 * Holds XYChart.Series data series corresponding to every {@link Sensor},
	 * {@link SensorCombination} and {@link TimeStamp}
	 */
	public final ConcurrentHashMap<Sensorable, XYSeries> dataMap = new ConcurrentHashMap<>();

	public static ChartData getInstance(ChartCanvas combo1, ChartCanvas combo2) {
		if (chartData == null) {
			chartData = new ChartData(combo1, combo2);
		}
		return chartData;
	}

	public static ChartData getInstance() {
		return ChartData.getInstance(null, null);
	}

	private ChartData(ChartCanvas combo1, ChartCanvas combo2) {
		this.chartTop = combo1;
		this.chartBottom = combo2;
	}

	/**
	 * Adds new data point to series, and deletes last if number of points <br>
	 * exceeds threshold.<br>
	 * Creates new thread, that in unspecified time will update GUI.
	 * /Platform.runLater/
	 * 
	 * @param map
	 *            LinkedHashMap holding measurements mapped to sensors/combinations.
	 */
	public void appendSeries(LinkedHashMap<Measurable, Double> map) {
		if (isBusy != true) {
			isBusy = true;

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					for (Sensorable measurement : dataMap.keySet()) {

						XYSeries dataList = dataMap.get(measurement);
						dataList.add(map.get(TimeStamp.getInstance()), map.get((Measurable) measurement));

						// actualize Panes values
						PaneValues paneValues = SensorPaneFactory.mapPane.get(measurement);

						paneValues.setValue(map.get((Measurable) measurement));
						paneValues.setMax(measurement.getMax());
						paneValues.setMin(measurement.getMin());
						if (dataList.getItemCount() >= speedPointsNumber) {
							double dTime = dataList.getDataItem(dataList.getItemCount() - 1).getXValue()
									- dataList.getDataItem(dataList.getItemCount() - speedPointsNumber).getXValue();
							double dMeasurement = dataList.getDataItem(dataList.getItemCount() - 1).getYValue()
									- dataList.getDataItem(dataList.getItemCount() - speedPointsNumber).getYValue();
							paneValues.setSpeed(dMeasurement / dTime);
						}
					}
					if (chartTickTock == false) {
						ChartCreator.actualizeChart(chartTop);
						chartTickTock = true;
					} else {
						ChartCreator.actualizeChart(chartBottom);
						chartTickTock = false;
					}
					isBusy = false;
				}
			});

		}
	}

	/**
	 * Deletes all series from {@link ChartData#dataMap} and sets them from
	 * beginning. <br>
	 * Should be called whenever new series has to be added.
	 */
	public void actualizeSeries() {
		// clear map holding chart serieses
		dataMap.clear();
		// insert all available sensors that are meant to work
		for (Type type : SensorFactory.typePrecedence) {
			for (int i = 0; i < SensorFactory.sensorMap.get(type).values().size(); i++) {
				Sensorable sensor = SensorFactory.sensorMap.get(type).get(i);
				XYSeries newSeries = new XYSeries(sensor.getName(), false, true);
				newSeries.setMaximumItemCount(dataPointsNumber);
				dataMap.put(sensor, newSeries);
			}
		}
		// insert all available combinations
		for (int i = 0; i < SensorCombinationFactory.size(); i++) {
			Sensorable sensor = SensorCombinationFactory.combinationMap.get(i);
			XYSeries newSeries = new XYSeries(sensor.getName(), false, true);
			newSeries.setMaximumItemCount(dataPointsNumber);
			dataMap.put(sensor, newSeries);
		}
	}

	/**
	 * Erase all data from all series. (but don't delete series)
	 */
	public synchronized void cleanSeries() {
		for (XYSeries series : dataMap.values()) {
			series.clear();
		}
	}
}
