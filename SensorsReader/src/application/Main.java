package application;

import javafx.application.Application;
import javafx.stage.Stage;
import logger.ReadingsLogger;
import pendrive.PendriveKeeper;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import arduino.*;
import sensors.*;
import sensors.SensorFactory.Type;

import java.io.File;

import javax.xml.bind.*;
import initializator.*;

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
		Xml.retrieveXml();
		System.out.println(SensorFactory.sensorMap.get(Type.ENCODER).get(0).getName());
		launch(args);
		PendriveKeeper keeper = new PendriveKeeper();
		Thread thread1 = new Thread(keeper);
		thread1.start();
		while (!keeper.isMounted()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ReadingsLogger logger = new ReadingsLogger();
		Thread thread2 = new Thread(logger);
		thread2.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.stop();
		System.out.println("logging stopped");
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keeper.unmount();
		keeper.stop();

	}

}
