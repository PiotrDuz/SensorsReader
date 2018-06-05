package application;

import javafx.application.Application;
import operations.initializator.Xml;
import operations.sensors.combination.SensorCombination;
import operations.sensors.combination.SensorCombinationFactory;
import userInterface.main.MainWindow;

public class App {
	public static void main(String[] args) {
		// initialize classes saved in xml
		Xml.retrieveXml();
		// initialize custom sensor combinations
		SensorCombinationFactory.initialize();

		Application.launch(MainWindow.class, args);

		for (SensorCombination sens : SensorCombinationFactory.combinationMap.values()) {
			System.out.println(sens.getName() + "-    -" + sens.getVariables().toString());
		}
	}
}
