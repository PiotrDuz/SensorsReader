package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import arduino.*;
import Sensors.*;
import pendrive.*;
import Sensors.SensorFactory.Type;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);

		System.out.println("test");

		Arduino serial = Arduino.getInstance();
		serial.open();

		SensorFactory.createSensor(Type.ENCODER);
		SensorFactory.createSensor(Type.ENCODER);
		SensorFactory.createSensor(Type.TENSOMETER);

		ReadingsLogger logger = new ReadingsLogger(serial);
		Thread thread = new Thread(logger);
		thread.start();
		serial.delay(10000);

		logger.stop();
		serial.delay(1000);
		serial.close();
	}
}
