package main.java.userInterface.timeWindow;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.java.operations.sensors.TimeStamp;
import main.java.userInterface.keyboard.Keyboard;

public class TimeWindowController implements Initializable {
	@FXML
	TextField textFieldName;
	@FXML
	TextField textFieldUnit;
	@FXML
	TextField textFieldScale;
	@FXML
	Button buttonExit;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		textFieldName.setText(TimeStamp.getInstance().getName());
		textFieldUnit.setText(TimeStamp.getInstance().getUnit());
		textFieldScale.setText(TimeStamp.getInstance().getScale().toString());
	}

	@FXML
	public void clickButton(ActionEvent event) {
		if (event.getSource().equals(buttonExit)) {
			Stage stage = (Stage) buttonExit.getScene().getWindow();
			stage.close();
		}
	}

	@FXML
	public void textFieldClick(MouseEvent event) {

		TextField field = (TextField) event.getSource();
		Keyboard keyboard = new Keyboard();
		keyboard.display((Node) field);
		// check if user has not cancelled the edit
		if (keyboard.getText() != null) {

			boolean parsable = true;
			Double number = null;
			try {
				number = Double.parseDouble(keyboard.getText());
			} catch (NumberFormatException exc) {
				parsable = false;
				System.out.println(exc);
			}

			// find out which box data is entered
			if (field == textFieldName) {
				System.out.println(TimeStamp.getInstance().getName());
				TimeStamp.getInstance().setName(keyboard.getText());
			} else if (field == textFieldUnit) {
				TimeStamp.getInstance().setUnit(keyboard.getText());
			} else if (field == textFieldScale) {
				if (parsable == false) {
					return;
				}
				TimeStamp.getInstance().setScale(number);
			}

			field.setText(keyboard.getText());
		}
	}
}
