package operations.logger;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import application.ProgramException;
import operations.arduino.Arduino;
import operations.arduino.Command;
import operations.sensors.Measurable;
import operations.sensors.Sensor;
import operations.sensors.SensorFactory;
import operations.sensors.TimeStamp;
import operations.sensors.combination.SensorCombination;
import operations.sensors.combination.SensorCombinationFactory;
import userInterface.main.ChartData;

/**
 * Class that is created as seperate thread. <br>
 * It's main method {@link ReadingsLogger#run()} is responsible of setting
 * variables <br>
 * and continuous pulling the port, reading it if a packet arrives. <br>
 * The process of constructing the main method:<br>
 * -array of all sensors is returned, which order is the same as order of
 * variables in Arduino's packet<br>
 * (this is controlled by program's list {@link SensorFactory#typePrecedence}.
 * <br>
 * -the hashmap for storing sensor and corresponding measurement parameter's are
 * initialized <br>
 * -the quantity of each type's sensors is written to Arduino, reading begins.
 * 
 * @author Piotr Duzniak
 *
 */
public class ReadingsLogger implements Runnable {
	final static Logger logger = Logger.getLogger(ReadingsLogger.class);
	private Boolean stop = false;

	// if save is selected, methods connected with csv printing activated
	CsvCreator csvCreator;
	// load factor of linkedHashmap
	private final float LOAD_FACTOR = (float) 0.7;

	public ReadingsLogger(boolean save, String fileName) throws ProgramException {

		if (save) {
			csvCreator = new CsvCreator(fileName, TimeStamp.getInstance().getSavePeriod());
		} else {
			csvCreator = null;
		}

	}

	/**
	 * Main thread for reading data from AVR and saving to chart's array or USD
	 * drive
	 */
	public void run() {
		// set array of sensor, in correct reading order. Get number of sensors of each
		Sensor[] sensorArray = SensorFactory.getOrderedArray();
		int[] orderedQuantity = SensorFactory.getOrderedQuantity();

		// get number of buckets for map holding measurements
		int buckets = getBuckets(sensorArray);

		try (Arduino serial = Arduino.getInstance(); CsvCreator csvCreator = this.csvCreator;) {
			// open serial and initialize number of sensors of each type to read
			serial.open();
			serial.write(Command.SEND_SENSORS_QUANTITY.get(), 1);
			// write in proper order, how many sensors of given type.
			for (int i = 0; i < orderedQuantity.length; i++) {
				serial.write(orderedQuantity[i], 1);
			}

			serial.write(Command.START_MEASURING.get(), 1);
			// clean serial of any possible bits residing
			serial.clean();

			// loop for constant reading
			while (stop == false) {
				// create each time new map holding new measurements
				LinkedHashMap<Measurable, Double> measureMap = new LinkedHashMap<>(buckets, LOAD_FACTOR, false);

				getReadings(serial, measureMap, sensorArray);

				// add new values to corresponding chart's series
				ChartData.getInstance().appendSeries(measureMap);

				// save readings
				if (csvCreator != null && csvCreator.readyToSave(measureMap.get(TimeStamp.getInstance()))) {
					csvCreator.saveCsv(measureMap);
				}
			}

			serial.write(Command.STOP_MEASURING.get(), 1);
			serial.clean();

			// save state to sensors
			saveStateToSensors(serial, sensorArray);

			Arduino.delay(1000);
		} catch (ProgramException e) {
			logger.error(e);
		}

	}

	private int getBuckets(Sensor[] sensorArray) {
		// calculate how many measurements will be read in one packet. Each is 4 bytes.
		int intsToRead = sensorArray.length + 1;
		// total amount of buckets needed in hashmap (+2 for safety)
		int buckets = intsToRead + SensorCombinationFactory.combinationMap.size() + 2;
		Float bucketsNeeded = buckets / LOAD_FACTOR;
		buckets = bucketsNeeded.intValue();

		return buckets;
	}

	private void saveStateToSensors(Arduino serial, Sensor[] sensorArray) throws ProgramException {
		serial.clean();
		// save state to sensors
		serial.write(Command.GET_STATE.get(), 1);

		// each number is sent as arduino's long (4 bytes)
		byte[] array = null;
		try {
			array = serial.read(sensorArray.length * 4);
		} catch (IOException exc) {
			logger.error(exc);
			throw new ProgramException(exc);
		}

		// Set raw lastState to sensor
		for (int i = 0; i < sensorArray.length; i++) {
			int currState = Arduino.byteToInt(array, i * 4);
			int lastState = sensorArray[i].getLastState();
			sensorArray[i].setLastState(currState + lastState);
		}
	}

	/**
	 * Fill previously prepared measureMap with readings from sensors. <br>
	 * LinkedHahsMap shoul dbe prepared for heavy load, eg. utilise buckets.
	 * 
	 * @param serial
	 * @param measureMap
	 * @param intsToRead
	 * @param sensorArray
	 * @return
	 * @throws ProgramException
	 */
	private void getReadings(Arduino serial, LinkedHashMap<Measurable, Double> measureMap, Sensor[] sensorArray)
			throws ProgramException {
		int intsToRead = sensorArray.length + 1; // add one because of time
		byte[] array = null;
		// each number is sent as arduino's long (4 bytes)
		try {
			array = serial.read(intsToRead * 4);
		} catch (IOException exc) {
			logger.error(exc);
			throw new ProgramException(exc);
		}

		// last value is a time
		measureMap.put(TimeStamp.getInstance(),
				TimeStamp.getInstance().getMeasurement(Arduino.byteToInt(array, (intsToRead - 1) * 4)));
		// Convert read byte array to values array
		for (int i = 0; i < sensorArray.length; i++) {
			measureMap.put(sensorArray[i], sensorArray[i].getMeasurement(Arduino.byteToInt(array, i * 4)));
		}
		// Add any combination
		for (SensorCombination sensComb : SensorCombinationFactory.combinationMap.values()) {
			measureMap.put(sensComb, sensComb.getMeasurement(measureMap));
		}

	}

	/**
	 * Call this method to stop reading loop, close the port, and exit thread.
	 */
	public synchronized void stop() {
		stop = true;
	}

}
