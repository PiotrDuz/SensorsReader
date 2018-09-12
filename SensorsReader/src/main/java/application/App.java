package application;

import java.io.IOException;

import org.apache.log4j.Logger;

import javafx.application.Application;
import operations.arduino.Arduino;
import operations.arduino.Command;
import operations.initializator.Xml;
import operations.pendrive.PendriveKeeper;
import operations.pendrive.PendriveMount;
import operations.sensors.combination.SensorCombinationFactory;
import userInterface.main.MainWindow;

/**
 * Class from which program starts. <br>
 * It retreives data from .xml file, and creates objects ({@link Sensor},
 * {@link Arduino}, {@link CombinationData}. <br>
 * {@link SensorCombinationFactory} is initialized. <br>
 * After that, program GUI starts.
 * 
 * @author Piotr Duzniak
 *
 */
public class App {
	final static Logger logger = Logger.getLogger(App.class);

	public static void main(String[] args) throws ProgramException {
		// initialize classes saved in xml
		System.out.println(Xml.CONFIG_PATH);
		Xml.retrieveXml();

		// initialize custom sensor combinations
		SensorCombinationFactory.initialize();

		// launch pendrive checking thread
		PendriveKeeper pendriveKeeper = PendriveKeeper.getInstance();
		Thread thread = new Thread(pendriveKeeper);
		thread.start();

		// set system date
		setSystemDate();

		// launch main aaplication
		Application.launch(MainWindow.class, args);

		// stop checking for pendrive
		pendriveKeeper.stop();
	}

	public static void setSystemDate() throws ProgramException {
		Arduino serial = Arduino.getInstance();
		serial.open();
		serial.delay(1000);
		serial.write(Command.GET_DATE.get(), 1);

		byte[] array = null;
		try {
			array = serial.read(4 * 5);
		} catch (IOException e) {
			logger.error(e);
			throw new ProgramException(e);
		}

		serial.close();

		int year = Arduino.byteToInt(array, 4 * 0);
		Integer month = Arduino.byteToInt(array, 4 * 1);
		Integer day = Arduino.byteToInt(array, 4 * 2);
		Integer hour = Arduino.byteToInt(array, 4 * 3);
		Integer minute = Arduino.byteToInt(array, 4 * 4);

		String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
		String time = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":00";
		// reuse execute command
		PendriveMount.executeCommand("timedatectl set-time \"" + date + " " + time + "\"");

	}
}
