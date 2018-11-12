package userInterface.prompt;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class PromptWindow {

	private PromptWindowController controller;
	Window ownerWindow;

	Stage stage = null;

	public static boolean getPrompt(Node node, String text) {
		PromptWindow window = new PromptWindow(node, text);
		window.openWindow();
		return window.getResult();
	}

	public PromptWindow(Node node, String text) {
		ownerWindow = node.getScene().getWindow();
		controller = new PromptWindowController(text);
	}

	public void openWindow() {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/PromptWindow.fxml"));
		loader.setController(controller);
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		stage = new Stage();
		stage.setTitle("Prompt");
		stage.setScene(new Scene(root));
		stage.initOwner(ownerWindow);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.showAndWait();

	}

	public boolean getResult() {
		return controller.getResult();
	}
}
