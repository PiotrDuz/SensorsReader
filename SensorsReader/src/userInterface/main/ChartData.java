package userInterface.main;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import operations.sensors.Sensor;
import operations.sensors.SensorFactory;
import operations.sensors.SensorFactory.Type;
import operations.sensors.Sensorable;
import operations.sensors.TimeStamp;
import operations.sensors.combination.SensorCombinationFactory;

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
	 * Checks if {@link #dataMap} holds the same amount of sensors as
	 * {@link SensorFactory#sensorMap}. If less, adds missing sensor from
	 * {@link SensorFactory#getClass()} If more, deletes sensor that is not in
	 * {@link SensorFactory#sensorMap} Doing that in a loop, until number of sensors
	 * is equal
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
			dataMap.put(SensorCombinationFactory.combinationMap.get(i), new XYChart.Series<>());
		}
	}

	/**
	 * Erase all data from all series
	 */
	public synchronized void cleanSeries() {
		for (XYChart.Series<Double, Double> series : dataMap.values()) {

			series.getData().clear();
		}
	}
}
