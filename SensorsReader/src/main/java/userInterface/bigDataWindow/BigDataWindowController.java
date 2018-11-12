package userInterface.bigDataWindow;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import operations.sensors.Sensorable;

public class BigDataWindowController implements Initializable {
	// sensor for which data will be displayed
	Sensorable sensor;

	@FXML
	AnchorPane anchorRoot;
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

	public BigDataWindowController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// set font
		DoubleProperty fontSize = new SimpleDoubleProperty(15); // font size in pt
		anchorRoot.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpt;", fontSize));
		this.labelX.setText(".");
		this.labelY.setText(".");
		labelYUnit.setText(".");
		labelXUnit.setText(".");
		labelName.setText(".");
	}

	/**
	 * X and Y values of given chart
	 * 
	 * @param labelX
	 * @param labelY
	 */
	public void setText(Double labelX, Double labelY) {
		this.labelX.setText(labelX.toString());
		this.labelY.setText(labelY.toString());
	}

	public void refresh(Sensorable sensor) {
		this.sensor = sensor;
		System.out.println(labelYUnit);
		labelYUnit.setText(sensor.getUnit());
		labelXUnit.setText(sensor.getXAxis().getUnit());
		labelName.setText(sensor.getName());
	}

	public Sensorable getSensor() {
		return sensor;
	}

}
