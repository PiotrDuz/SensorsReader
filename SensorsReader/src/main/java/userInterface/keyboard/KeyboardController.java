package main.java.userInterface.keyboard;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class KeyboardController implements Initializable {
	public StringBuilder textHolder = new StringBuilder();
	@FXML
	private TextField textInput;

	public KeyboardController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TO DO (don't need at this example)
	}

	@FXML
	public void pressButton(ActionEvent event) {
		Button button1 = (Button) event.getSource();
		String text1 = button1.getText();
		textHolder.append(text1);
		textInput.setText(textHolder.toString());
	}

	@FXML
	public void pressDelete(ActionEvent event) {
		if (textHolder != null && textHolder.length() > 0)
			textHolder.setLength(textHolder.length() - 1);
		textInput.setText(textHolder.toString());
	}

	@FXML
	public void pressDecline(ActionEvent event) {
		textHolder = null;
		Button button1 = (Button) event.getSource();
		Stage stage1 = (Stage) button1.getScene().getWindow();
		stage1.close();
	}

	@FXML
	public void pressAccept(ActionEvent event) {
		Button button1 = (Button) event.getSource();
		Stage stage1 = (Stage) button1.getScene().getWindow();
		stage1.close();
	}
}
