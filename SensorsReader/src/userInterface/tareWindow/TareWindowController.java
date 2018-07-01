package userInterface.tareWindow;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import operations.sensors.SensorFactory;
import operations.sensors.Sensorable;
import userInterface.main.ChartData;
import operations.sensors.SensorFactory.Type;

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
		ObservableList<XYChart.Data<Double, Double>> list = ChartData.getInstance().dataMap.get(measureComponent)
				.getData();

		if (list.size() == 0) {
			buttonYes.setDisable(true);
			labelName.setText("BRAK DANYCH W PAMIECI");
			return;
		}

		value = measureComponent.getZeroValue() + list.get(list.size() - 1).getYValue();
		labelValue.setText(value.toString());
	}

	@FXML
	public void clickButton(ActionEvent event) {
		Button button = (Button) event.getSource();

		if (button == buttonYes) {
			measureComponent.setZeroValue(value);
		} else if (button == buttonAll) {
			for (Sensorable measureCompLoop : ChartData.getInstance().dataMap.keySet()) {
				ObservableList<XYChart.Data<Double, Double>> list = ChartData.getInstance().dataMap.get(measureCompLoop)
						.getData();

				if (list.size() == 0) {
					buttonAll.setDisable(true);
					labelName.setText("BRAK DANYCH W PAMIECI");
					return;
				}

				double zeroValue = measureCompLoop.getZeroValue() + list.get(list.size() - 1).getYValue();
				measureCompLoop.setZeroValue(zeroValue);
			}
		}

		Stage stage1 = (Stage) button.getScene().getWindow();
		stage1.close();
	}
}
