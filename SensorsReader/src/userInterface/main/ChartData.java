package userInterface.main;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import operations.sensors.SensorFactory;
import operations.sensors.SensorFactory.Type;
import operations.sensors.Sensorable;
import operations.sensors.TimeStamp;
import operations.sensors.combination.SensorCombinationFactory;

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
	public volatile Boolean isBusy = false;
	static private ChartData chartData;

	/**
	 * Holds Chart Data series corresponding to every Sensor in
	 * {@link SensorFactory#sensorMap}
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
	public void appendSeries(LinkedHashMap<Sensorable, Double> map) {
		if (isBusy != true) {
			isBusy = true;

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					for (Sensorable sensor : dataMap.keySet()) {
						ObservableList<XYChart.Data<Double, Double>> dataList = dataMap.get(sensor).getData();
						XYChart.Data<Double, Double> chartPoint = new XYChart.Data<>(map.get(TimeStamp.getInstance()),
								map.get(sensor));
						dataList.add(chartPoint);
						if (dataList.size() > dataPointsNumber) {
							dataList.remove(0);
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
