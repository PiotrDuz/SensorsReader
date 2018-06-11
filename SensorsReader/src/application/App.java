package application;

import javafx.application.Application;
import operations.initializator.Xml;
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
	public static void main(String[] args) {
		// initialize classes saved in xml
		Xml.retrieveXml();
		// initialize custom sensor combinations
		SensorCombinationFactory.initialize();

		Application.launch(MainWindow.class, args);

	}
}
