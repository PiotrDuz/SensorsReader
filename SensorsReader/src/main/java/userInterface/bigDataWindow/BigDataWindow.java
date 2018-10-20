package userInterface.bigDataWindow;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import operations.sensors.Sensorable;

public class BigDataWindow {
	private BigDataWindowController controller;
	Stage stage;

	public BigDataWindow(Sensorable sensor) {
		controller = new BigDataWindowController(sensor);
	}

	public void openWindow() {
		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource("main/resources/gui/BigDataWindow.fxml"));
		loader.setController(controller);
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Stage stage = new Stage();
		stage.setTitle("combinationWindow");
		stage.setScene(new Scene(root));
		stage.showAndWait();
	}

	public void refresh(Sensorable sensor) {
		controller.refresh(sensor);
	}

	public void setText(double labelX, double labelY) {
		controller.setText(labelX, labelY);
	}

	public Sensorable getSensor() {
		return controller.getSensor();
	}

	public void closeWindow() {
		stage.close();
	}
}
