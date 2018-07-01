package userInterface.keyboard;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Keyboard {
	private KeyboardController keyboardController;

	public Keyboard() {
		keyboardController = new KeyboardController();
	}

	public void display(Node node) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getClassLoader().getResource("./userInterface/keyboard/keyboard.fxml"));
			loader.setController(keyboardController);
			Parent root = loader.load();
			Scene scene1 = new Scene(root);
			Stage newWindow = new Stage();
			newWindow.initOwner(node.getScene().getWindow());
			newWindow.initModality(Modality.WINDOW_MODAL);
			newWindow.setTitle("Keyboard");
			newWindow.setScene(scene1);
			newWindow.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getText() {
		if (keyboardController.textHolder == null) {
			return null;
		} else {
			return keyboardController.textHolder.toString();
		}
	}
}
