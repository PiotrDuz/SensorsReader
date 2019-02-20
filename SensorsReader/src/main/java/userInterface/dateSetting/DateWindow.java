package userInterface.dateSetting;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DateWindow {
	private DateWindowController controller = new DateWindowController();
	private Window ownerWindow;

	public DateWindow(Node node) {
		ownerWindow = node.getScene().getWindow();
	}

	public void openWindow() {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/DateWindow.fxml"));
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
		stage.setTitle("combinationWindow");
		stage.setScene(new Scene(root));
		stage.showAndWait();
	}
}
