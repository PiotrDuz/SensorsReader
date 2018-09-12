package  userInterface.addSensorWindow;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import  operations.sensors.Sensor;
import  operations.sensors.SensorFactory;
import  operations.sensors.SensorFactory.SensorType;

public class AddSensorWindowController implements Initializable {
	@FXML
	ComboBox<SensorType> comboBoxType;
	@FXML
	ComboBox<Sensor> comboBoxDelete;
	@FXML
	Label labelType;
	@FXML
	Label labelTypeCount;
	@FXML
	Button buttonExit;
	@FXML
	Button buttonAdd;
	@FXML
	Button buttonDelete;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<SensorType> dataList = new ArrayList<>();
		for (SensorType enumType : SensorFactory.typePrecedence) {
			dataList.add(enumType);
		}
		comboBoxType.setItems(FXCollections.observableList(dataList));

		List<Sensor> dataDeleteList = new ArrayList<>();
		for (SensorType enumType : SensorFactory.typePrecedence) {
			if (SensorFactory.sensorMap.get(enumType) == null) {
				continue;
			}
			for (Sensor sens : SensorFactory.sensorMap.get(enumType).values()) {
				dataDeleteList.add(sens);
			}
		}

		comboBoxDelete.setItems(FXCollections.observableList(dataDeleteList));
	}

	@FXML
	public void clickButton(ActionEvent event) {
		Button button = (Button) event.getSource();

		if (buttonAdd == button) {
			if (comboBoxType.getValue() != null) {
				SensorFactory.createSensor(comboBoxType.getValue());
				initialize(null, null);
			}
		} else if (buttonDelete == button) {
			SensorFactory.sensorMap.get(comboBoxDelete.getValue().getType()).remove(comboBoxDelete.getValue().getId());
			initialize(null, null);
		} else if (buttonExit == button) {
			Stage stage = (Stage) buttonExit.getScene().getWindow();
			stage.close();
		}
	}

	@FXML
	public void comboBoxChooseType(ActionEvent event) {
		if (comboBoxType.getValue() != null) {
			labelType.setText(comboBoxType.getValue().name());

			Integer itemCount = null;
			if (SensorFactory.sensorMap.get(comboBoxType.getValue()) != null) {
				itemCount = SensorFactory.sensorMap.get(comboBoxType.getValue()).keySet().size();
			}
			String text = "empty";
			if (itemCount != null) {
				text = itemCount.toString();
			}
			labelTypeCount.setText(text);
		}
	}

}
