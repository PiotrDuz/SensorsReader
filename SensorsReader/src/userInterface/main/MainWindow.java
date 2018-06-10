package userInterface.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import operations.initializator.Xml;

public class MainWindow extends Application {
	public static MainWindowController controller;

	@Override
	public void init() {
		controller = new MainWindowController();
	}

	@Override
	public void start(Stage primaryStage) {
		// run main window

		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource("./userInterface/main/MainWindow.fxml"));
		loader.setController(controller);
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
}
