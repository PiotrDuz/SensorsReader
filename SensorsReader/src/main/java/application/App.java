package main.java.application;

import javafx.application.Application;
import main.java.operations.initializator.Xml;
import main.java.operations.pendrive.PendriveKeeper;
import main.java.operations.sensors.combination.SensorCombinationFactory;
import main.java.userInterface.main.MainWindow;

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

	public static void main(String[] args) {
		// initialize classes saved in xml
		System.out.println(Xml.CONFIG_PATH);
		Xml.retrieveXml();
		// initialize custom sensor combinations
		SensorCombinationFactory.initialize();
		// launch pendrive checking thread
		PendriveKeeper pendriveKeeper = PendriveKeeper.getInstance();
		Thread thread = new Thread(pendriveKeeper);
		thread.start();

		Application.launch(MainWindow.class, args);

		// stop checking for pendrive
		pendriveKeeper.stop();
	}
}
