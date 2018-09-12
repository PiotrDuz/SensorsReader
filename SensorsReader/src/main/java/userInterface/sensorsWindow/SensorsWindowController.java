package  userInterface.sensorsWindow;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import  operations.sensors.Sensor;
import  operations.sensors.SensorFactory;
import  operations.sensors.SensorFactory.SensorType;
import  userInterface.keyboard.Keyboard;

public class SensorsWindowController implements Initializable {
	@FXML
	ComboBox<Sensor> comboBox;
	@FXML
	TextField textFieldName;
	@FXML
	TextField textFieldUnit;
	@FXML
	TextField textFieldScale;
	@FXML
	TextField textFieldZero;
	@FXML
	Label labelType;
	@FXML
	Label labelId;
	@FXML
	Button buttonExit;
	@FXML
	CheckBox checkIsCharted;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		for (SensorType sensorType : SensorFactory.typePrecedence) {
			for (int i = 0; i < SensorFactory.sensorMap.get(sensorType).size(); i++) {
				comboBox.getItems().add(SensorFactory.sensorMap.get(sensorType).get(i));
			}
		}
	}

	@FXML
	public void comboBoxChoose(ActionEvent event) {
		textFieldName.setText(comboBox.getValue().getName());
		textFieldUnit.setText(comboBox.getValue().getUnit());
		textFieldScale.setText(comboBox.getValue().getScale().toString());
		textFieldZero.setText(comboBox.getValue().getZeroValueScaled().toString());
		labelId.setText(comboBox.getValue().getId().toString());
		labelType.setText(comboBox.getValue().getType().toString());
		checkIsCharted.setSelected(comboBox.getValue().isCharted());
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
		// sensor must be chosen
		if (comboBox.getValue() == null) {
			return;
		}
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
				comboBox.getValue().setName(keyboard.getText());
			} else if (field == textFieldUnit) {
				comboBox.getValue().setUnit(keyboard.getText());
			} else if (field == textFieldScale) {
				if (parsable == false) {
					return;
				}
				comboBox.getValue().setScale(number);
			} else if (field == textFieldZero) {
				if (parsable == false) {
					return;
				}
				comboBox.getValue().setZeroValueScaled(number);
			}
			field.setText(keyboard.getText());
		}
	}

	@FXML
	public void checkBox(ActionEvent event) {
		if (comboBox.getValue() == null) {
			return;
		}

		comboBox.getValue().isChartedSet(checkIsCharted.isSelected());
	}
}
