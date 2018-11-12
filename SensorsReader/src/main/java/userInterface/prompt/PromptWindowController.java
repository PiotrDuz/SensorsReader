package userInterface.prompt;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PromptWindowController implements Initializable {
	private String text;
	private boolean flag;
	@FXML
	Button buttonYes;
	@FXML
	Button buttonNo;
	@FXML
	Label labelText;

	public PromptWindowController(String text) {
		this.text = text;
	}

	public boolean getResult() {
		return this.flag;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelText.setText(text);
	}

	@FXML
	public void clickButton(ActionEvent event) {
		Button button = (Button) event.getSource();

		if (button == buttonYes) {
			flag = true;

		} else if (button == buttonNo) {
			flag = false;
		}

		Stage stage = (Stage) button.getScene().getWindow();
		stage.close();
	}

}
