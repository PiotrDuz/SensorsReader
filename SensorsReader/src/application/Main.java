package application;

import javafx.application.Application;
import javafx.stage.Stage;
import logger.ReadingsLogger;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import arduino.*;
import sensors.*;
import sensors.SensorFactory.Type;

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

		SensorFactory.createSensor(Type.ENCODER).setName("Enc1");
		SensorFactory.createSensor(Type.ENCODER).setName("Enc2");
		SensorFactory.createSensor(Type.TENSOMETER).setName("Ten1");

		ReadingsLogger logger = new ReadingsLogger();
		Thread thread = new Thread(logger);
		thread.start();
		serial.delay(10000);

		logger.stop();
		serial.delay(1000);
		serial.close();
	}
}
