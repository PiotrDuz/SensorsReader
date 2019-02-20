package userInterface.bigDataWindow;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import operations.sensors.Sensorable;

public class BigDataWindow {
	private BigDataWindowController controller;
	Stage stage = null;

	public BigDataWindow() {
		controller = new BigDataWindowController();
	}

	public void openWindow(Sensorable sens, Stage parent) {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/BigDataWindow.fxml"));
		loader.setController(controller);
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		stage = new Stage();
		stage.setTitle("BigDataWindow");
		stage.setScene(new Scene(root));
		stage.initOwner(parent);
		stage.setAlwaysOnTop(true);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setX(0.0); // parent.getWidth()
		stage.setY(0.0);
		stage.show();

		refresh(sens);

	}

	public void refresh(Sensorable sensor) {
		if (sensor == null) {
			return;
		} else {
			controller.refresh(sensor);
		}
	}

	public void setText(double labelX, double labelY) {
		controller.setText(labelX, labelY);
	}

	public Sensorable getSensor() {
		return controller.getSensor();
	}

	public void closeWindow() {
		stage.close();
		stage = null;
	}

	public boolean isOpen() {
		if (stage == null) {
			return false;
		} else {
			return true;
		}
	}

}
