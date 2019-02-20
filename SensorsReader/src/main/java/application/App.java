package application;

import java.io.IOException;

import org.apache.log4j.Logger;

import javafx.application.Application;
import operations.arduino.Arduino;
import operations.arduino.Command;
import operations.initializator.Xml;
import operations.pendrive.PendriveKeeper;
import operations.pendrive.PendriveMount;
import operations.sensors.combination.CombinationData;
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
		byte[] array = null;

		try (Arduino serial = Arduino.getInstance()) {
			serial.open();

			serial.write(Command.GET_DATE.get(), 1);

			array = serial.read(6);

		} catch (IOException | ProgramException e) {
			logger.error(e);
			throw new ProgramException(e);
		}

		int year = array[0] + 2000;
		int month = array[1];
		int day = array[2];
		int hour = array[3];
		int minute = array[4];
		int second = array[5];

		String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
		String time = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":"
				+ String.format("%02d", second);
		// reuse execute command
		System.out.println("Before system call: " + date + " " + time);
		PendriveMount.executeCommand("sudo timedatectl set-time \"" + date + " " + time + "\"");
		System.out.println("sudo timedatectl set-time \"" + date + " " + time + "\"");
	}
}
