package userInterface.dateSetting;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import application.ProgramException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import operations.arduino.Arduino;
import operations.arduino.Command;
import userInterface.keyboard.Keyboard;

public class DateWindowController implements Initializable {
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	private LocalDateTime dateTime = null;
	@FXML
	TextField textInput;
	@FXML
	Label labelDate;
	@FXML
	Button buttonDecline;
	@FXML
	Button buttonAccept;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelDate.setText(LocalDateTime.now().format(formatter));
	}

	@FXML
	public void clickButton(ActionEvent event) throws ProgramException {
		Button button = (Button) event.getSource();
		if (button == buttonAccept) {
			if (dateTime == null) {
				return;
			}

			try (Arduino serial = Arduino.getInstance()) {
				serial.open();
				serial.write(Command.SET_DATE.get(), 1);

				serial.write(dateTime.getYear() - 2000, 1);
				serial.write(dateTime.getMonthValue(), 1);
				serial.write(dateTime.getDayOfMonth(), 1);
				serial.write(dateTime.getHour(), 1);
				serial.write(dateTime.getMinute(), 1);
				serial.write(dateTime.getSecond(), 1);

			}

			Stage stage = (Stage) button.getScene().getWindow();
			stage.close();
		} else if (button == buttonDecline) {
			Stage stage = (Stage) button.getScene().getWindow();
			stage.close();
		}
	}

	@FXML
	public void clickMouse(MouseEvent event) {
		Keyboard keyboard = new Keyboard();
		keyboard.display((Node) event.getSource());
		if (keyboard.getText() != null) {
			String date = keyboard.getText();
			try {
				dateTime = LocalDateTime.parse(date, formatter);
			} catch (DateTimeParseException e) {
				dateTime = null;
				textInput.setText("Wrong Date!");
				return;
			}
			textInput.setText(dateTime.format(formatter));
		}
	}

}
