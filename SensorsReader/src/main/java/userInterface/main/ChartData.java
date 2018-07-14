package main.java.userInterface.main;

import java.util.LinkedHashMap;

import java.util.concurrent.ConcurrentHashMap;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
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
	public volatile Boolean isBusy = false;
	static private ChartData chartData;

	/**
	 * Holds XYChart.Series data series corresponding to every {@link Sensor},
	 * {@link SensorCombination} and {@link TimeStamp}
	 */
	public final ConcurrentHashMap<Sensorable, XYChart.Series<Double, Double>> dataMap = new ConcurrentHashMap<>();

	public static ChartData getInstance() {
		if (chartData == null) {
			chartData = new ChartData();
		}
		return chartData;
	}

	private ChartData() {

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
						ObservableList<XYChart.Data<Double, Double>> dataList = dataMap.get(measurement).getData();
						XYChart.Data<Double, Double> chartPoint = new XYChart.Data<>(map.get(TimeStamp.getInstance()),
								map.get((Measurable) measurement));
						dataList.add(chartPoint);
						if (dataList.size() > dataPointsNumber) {
							dataList.remove(0);
						}

						// actualize Panes values
						PaneValues paneValues = SensorPaneFactory.mapPane.get(measurement);
						paneValues.setValue(map.get((Measurable) measurement));
						paneValues.setMax(measurement.getMax());
						paneValues.setMin(measurement.getMin());
						if (dataList.size() >= speedPointsNumber) {
							double dTime = dataList.get(dataList.size() - 1).getXValue()
									- dataList.get(dataList.size() - speedPointsNumber).getXValue();
							double dMeasurement = dataList.get(dataList.size() - 1).getYValue()
									- dataList.get(dataList.size() - speedPointsNumber).getYValue();
							paneValues.setSpeed(dMeasurement / dTime);
						}
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
				dataMap.put(SensorFactory.sensorMap.get(type).get(i), new XYChart.Series<Double, Double>());
			}
		}
		// insert all available combinations
		for (int i = 0; i < SensorCombinationFactory.size(); i++) {
			dataMap.put(SensorCombinationFactory.combinationMap.get(i), new XYChart.Series<Double, Double>());
		}
	}

	/**
	 * Erase all data from all series. (but don't delete series)
	 */
	public synchronized void cleanSeries() {
		for (XYChart.Series<Double, Double> series : dataMap.values()) {
			series.getData().clear();
		}
	}
}
