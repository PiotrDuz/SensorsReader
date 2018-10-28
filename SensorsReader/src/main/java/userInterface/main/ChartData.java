package userInterface.main;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.jfree.data.xy.XYSeries;

import javafx.application.Platform;
import javafx.scene.control.CheckMenuItem;
import operations.sensors.Measurable;
import operations.sensors.SensorFactory;
import operations.sensors.SensorFactory.SensorType;
import operations.sensors.Sensorable;
import operations.sensors.TimeStamp;
import operations.sensors.combination.SensorCombination;
import operations.sensors.combination.SensorCombinationFactory;
import userInterface.main.SensorPaneFactory.PaneValues;

/**
 * Class responsible for holding and handling data series that are displayed on
 * charts.<br>
 * Sets additional information displayed in MainWindow, in real-time.
 * 
 * @author Piotr Duzniak
 *
 */
public class ChartData {
	static private ChartData chartData;

	private int dataPointsNumber = 800;
	private int speedPointsNumber = 5;

	private CheckMenuItem paneVisibility;
	// is chart refreshing thread still working?
	private volatile Boolean isBusy = false;

	/**
	 * Holds XYChart.Series data series corresponding to every {@link Sensor},
	 * {@link SensorCombination} and {@link TimeStamp}
	 */
	public final ConcurrentHashMap<Sensorable, XYSeries> dataMap = new ConcurrentHashMap<>();

	public static ChartData getInstance(CheckMenuItem paneVisibility) {
		if (chartData == null) {
			chartData = new ChartData(paneVisibility);
		}
		return chartData;
	}

	public static ChartData getInstance() {
		return ChartData.getInstance(null);
	}

	private ChartData(CheckMenuItem paneVisibility) {
		this.paneVisibility = paneVisibility;
	}

	/**
	 * Adds new data point to series, and deletes last if number of points <br>
	 * exceeds threshold.<br>
	 * Creates new thread, that in unspecified time will update GUI.
	 * /Platform.runLater/
	 * 
	 * @param map LinkedHashMap holding measurements mapped to sensors/combinations.
	 */
	public void appendSeries(LinkedHashMap<Measurable, Double> map) {
		if (isBusy != true) {
			isBusy = true;

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					for (Sensorable measurement : dataMap.keySet()) {
						// add new point to chart data array
						XYSeries dataList = dataMap.get(measurement);
						dataList.add(map.get(measurement.getXAxis()), map.get(measurement));

						if (paneVisibility.isSelected()) {
							// actualize time
							SensorPaneFactory.getTimeTextValue().setText(map.get(TimeStamp.getInstance()).toString());
							// actualize Panes values
							PaneValues paneValues = SensorPaneFactory.mapPane.get(measurement);

							paneValues.setValue(map.get(measurement));
							paneValues.setMax(measurement.getMax());
							paneValues.setMin(measurement.getMin());
							// speed calculations
							if (dataList.getItemCount() >= speedPointsNumber) {
								double dTime = dataList.getDataItem(dataList.getItemCount() - 1).getXValue()
										- dataList.getDataItem(dataList.getItemCount() - speedPointsNumber).getXValue();
								double dMeasurement = dataList.getDataItem(dataList.getItemCount() - 1).getYValue()
										- dataList.getDataItem(dataList.getItemCount() - speedPointsNumber).getYValue();
								paneValues.setSpeed(dMeasurement / dTime);
							}
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
		for (SensorType type : SensorFactory.typePrecedence) {
			if (SensorFactory.sensorMap.get(type) == null) {
				continue;
			}
			for (int i = 0; i < SensorFactory.sensorMap.get(type).values().size(); i++) {
				Sensorable sensor = SensorFactory.sensorMap.get(type).get(i);

				if (!sensor.isCharted()) {
					continue;
				}
				XYSeries newSeries = new XYSeries(sensor.getName(), false, true);
				newSeries.setMaximumItemCount(dataPointsNumber);
				dataMap.put(sensor, newSeries);
			}
		}
		// insert all available combinations
		for (int i = 0; i < SensorCombinationFactory.size(); i++) {
			Sensorable sensor = SensorCombinationFactory.combinationMap.get(i);
			if (!sensor.isCharted()) {
				continue;
			}
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
