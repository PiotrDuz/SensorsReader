package userInterface.bigDataWindow;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import operations.sensors.Sensorable;

public class BigDataWindowController implements Initializable {
	// sensor for which data will be displayed
	Sensorable sensor;

	@FXML
	Label labelName;
	@FXML
	Label labelX;
	@FXML
	Label labelY;
	@FXML
	Label labelYUnit;
	@FXML
	Label labelXUnit;

	public BigDataWindowController(Sensorable sensor) {
		this.sensor = sensor;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		refresh(sensor);
	}

	public void setText(Double labelX, Double labelY) {
		this.labelX.setText(labelX.toString());
		this.labelY.setText(labelY.toString());
	}

	public void refresh(Sensorable sensor) {
		this.sensor = sensor;
		labelYUnit.setText(sensor.getUnit());
		labelXUnit.setText(sensor.getXAxis().getUnit());
		labelName.setText(sensor.getName());
	}

	public Sensorable getSensor() {
		return sensor;
	}

}
