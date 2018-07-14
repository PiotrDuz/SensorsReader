package main.java.userInterface.combinationWindow;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CombinationWindow {
	private CombinationWindowController controller = new CombinationWindowController();
	private Window ownerWindow;

	public CombinationWindow(Node node) {
		ownerWindow = node.getScene().getWindow();
	}

	public void openWindow() {
		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource("main/resources/gui/combinationWindow.fxml"));
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
