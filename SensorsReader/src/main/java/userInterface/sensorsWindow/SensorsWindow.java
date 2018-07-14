package main.java.userInterface.sensorsWindow;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/*
 * Displays sensor settings window.
 */
public class SensorsWindow {
	private SensorsWindowController controller = new SensorsWindowController();
	private Window ownerWindow;

	public SensorsWindow(Node node) {
		ownerWindow = node.getScene().getWindow();
	}

	public void openWindow() {
		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource("main/resources/gui/sensorsWindow.fxml"));
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
		stage.setTitle("sensorsWindow");
		stage.setScene(new Scene(root));
		stage.showAndWait();
	}
}
