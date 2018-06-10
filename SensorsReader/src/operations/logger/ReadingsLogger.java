package operations.logger;

import operations.arduino.Arduino;
import operations.arduino.Command;
import operations.sensors.Sensor;
import operations.sensors.SensorFactory;
import operations.sensors.SensorFactory.Type;
import operations.sensors.Sensorable;
import operations.sensors.TimeStamp;
import operations.sensors.combination.SensorCombinationFactory;
import userInterface.main.ChartData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

public class ReadingsLogger implements Runnable {
	private Boolean stop = false;
	private Boolean save = false;
	private final Arduino serial = Arduino.getInstance();
	private final float LOAD_FACTOR = (float) 0.7;

	public ReadingsLogger() {
	}

	/**
	 * Main thread for reading data from AVR and saving to chart's array or USD
	 * drive
	 */
	public void run() {
		// holds read arduino's byte stream
		byte[] array = null;

		// if save is selected, methods connected with csv printing activated
		CsvCreator csvCreator = null;
		if (save) {
			csvCreator = new CsvCreator();
		}

		// set array of sensor, in correct reading order. Get number of sensors of each
		// type
		Sensor[] sensorArray = SensorFactory.getOrderedArray();
		int[] orderedQuantity = SensorFactory.getOrderedQuantity();

		// calculate how many measurements will be read in one packet. Each is 4 bytes.
		int intsToRead = 0;
		for (int i = 0; i < orderedQuantity.length; i++) {
			intsToRead = intsToRead + orderedQuantity[i];
		}
		intsToRead++; // +1 because of time
		// total amount of buckets needed in hashmap
		int buckets = intsToRead + SensorCombinationFactory.combinationMap.size();
		// include load factor
		Float bucketsNeeded = buckets / LOAD_FACTOR;
		buckets = bucketsNeeded.intValue();

		// open serial and initialize number of sensors of each type to read
		serial.open();
		serial.write(Command.SEND_SENSORS_QUANTITY.get(), 1);
		// write in proper order, how many sensors of given type.
		for (int i = 0; i < orderedQuantity.length; i++) {
			serial.write(orderedQuantity[i], 1);
		}

		serial.write(Command.START_MEASURING.get(), 1);

		// loop for constant reading
		while (stop == false) {
			// create each time new map holding new measurements
			LinkedHashMap<Sensorable, Double> measureMap = new LinkedHashMap<>(buckets, LOAD_FACTOR, false);

			// each number is sent as arduino's long (4 bytes)
			try {
				array = serial.read(intsToRead * 4);
			} catch (IOException exc) {
				System.out.println(exc);
			}

			// Convert read byte array to values array
			for (int i = 0; i < sensorArray.length; i++) {
				measureMap.put(sensorArray[i], sensorArray[i].getMeasurement(Arduino.byteToInt(array, i * 4)));
			}
			// last value is a time
			measureMap.put(TimeStamp.getInstance(),
					TimeStamp.getInstance().getMeasurement(Arduino.byteToInt(array, (intsToRead - 1) * 4)));

			// Add any combination

			// add new values to corresponding chart's series
			ChartData.getInstance().appendSeries(measureMap);

			System.out.println(measureMap.get(TimeStamp.getInstance()));

			if (save) {
				csvCreator.saveCsv(measureMap);
			}
		}

		serial.write(Command.STOP_MEASURING.get(), 1);
		serial.delay(1000);
		serial.close();

		if (save) {
			csvCreator.close();
		}

	}

	public synchronized void stop() {
		stop = true;
	}

}
