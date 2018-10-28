package userInterface.tareWindow;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import operations.sensors.Sensorable;

public class TareWindow {
	private TareWindowController controller;
	private Window ownerWindow;

	public TareWindow(Node node, Sensorable measureComponent) {
		ownerWindow = node.getScene().getWindow();
		controller = new TareWindowController(measureComponent);
	}

	public void openWindow() {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/TareWindow.fxml"));
		loader.setController(controller);
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stage stage = new Stage();
		stage.initOwner(ownerWindow);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setTitle("TareWindow");
		stage.setScene(new Scene(root));
		stage.showAndWait();
	}
}
