package main.java.userInterface.tareWindow;

import java.net.URL;
import java.util.ResourceBundle;

import org.jfree.data.xy.XYSeries;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.java.operations.sensors.Sensorable;
import main.java.userInterface.main.ChartData;

public class TareWindowController implements Initializable {
	Sensorable measureComponent;
	Double value;

	@FXML
	Label labelValue;
	@FXML
	Label labelName;
	@FXML
	Button buttonYes;
	@FXML
	Button buttonAll;
	@FXML
	Button buttonDiscard;

	public TareWindowController(Sensorable measureComponent) {
		this.measureComponent = measureComponent;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (measureComponent == null) {
			labelName.setText("Nie wybrano komponentu");
			buttonYes.setDisable(true);
			return;
		}

		labelName.setText(measureComponent.getName());
		XYSeries list = ChartData.getInstance(null, null).dataMap.get(measureComponent);

		if (list.getItemCount() == 0) {
			buttonYes.setDisable(true);
			labelName.setText("BRAK DANYCH W PAMIECI");
			return;
		}

		value = list.getDataItem(list.getItemCount() - 1).getYValue();
		labelValue.setText(value.toString());
	}

	@FXML
	public void clickButton(ActionEvent event) {
		Button button = (Button) event.getSource();

		if (button == buttonYes) {
			measureComponent.setZeroValueScaled(value);
		} else if (button == buttonAll) {
			for (Sensorable measureCompLoop : ChartData.getInstance(null, null).dataMap.keySet()) {
				XYSeries list = ChartData.getInstance().dataMap.get(measureCompLoop);

				if (list.getItemCount() == 0) {
					buttonAll.setDisable(true);
					labelName.setText("BRAK DANYCH W PAMIECI");
					return;
				}

				double zeroValue = list.getDataItem(list.getItemCount() - 1).getYValue();
				measureCompLoop.setZeroValueScaled(zeroValue);
			}
		}

		Stage stage1 = (Stage) button.getScene().getWindow();
		stage1.close();
	}
}
