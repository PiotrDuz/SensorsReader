package userInterface.timeWindow;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class TimeWindow {
	private TimeWindowController controller = new TimeWindowController();
	private Window ownerWindow;

	public TimeWindow(Node node) {
		ownerWindow = node.getScene().getWindow();
	}

	public void openWindow() {
		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource("./userInterface/timeWindow/timeWindow.fxml"));
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
		stage.setTitle("timeWindow");
		stage.setScene(new Scene(root));
		stage.showAndWait();
	}
}
