package userInterface.keyboard;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class KeyboardController implements Initializable {
	int coursorPos = 0;
	private StringBuilder textHolder = new StringBuilder();
	private String initialText;
	@FXML
	private TextField textInput;

	public KeyboardController(String initialText) {
		this.initialText = initialText;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (initialText != null) {
			textInput.setText(initialText);
			textHolder.append(initialText);
		}
	}

	@FXML
	public void clickedMouse(MouseEvent event) {
		coursorPos = textInput.getCaretPosition();
	}

	@FXML
	public void pressButton(ActionEvent event) {
		Button button1 = (Button) event.getSource();
		String text1 = button1.getText();
		textHolder.insert(coursorPos, text1);
		coursorPos++;
		textInput.setText(textHolder.toString());
	}

	@FXML
	public void pressDelete(ActionEvent event) {
		if (textHolder != null && textHolder.length() > 0) {
			if (coursorPos != 0) {
				textHolder.delete(coursorPos - 1, coursorPos);
				coursorPos--;
			}
		}
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

	public StringBuilder getTextHolder() {
		return textHolder;
	}
}
