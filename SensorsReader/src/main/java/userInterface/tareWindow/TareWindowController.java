package userInterface.tareWindow;

import java.net.URL;
import java.util.ResourceBundle;

import org.jfree.data.xy.XYSeries;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import operations.sensors.Sensorable;
import userInterface.main.ChartData;

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
		// check if there is component chosen
		if (measureComponent == null) {
			labelName.setText("Nie wybrano komponentu");
			buttonYes.setDisable(true);
			return;
		}

		labelName.setText(measureComponent.getName());
		XYSeries list = ChartData.getInstance().dataMap.get(measureComponent);

		// check if there is data for component
		if (list.getItemCount() == 0) {
			buttonYes.setDisable(true);
			labelName.setText("BRAK DANYCH W PAMIECI");
			return;
		}

		value = getAverage(list, 10);
		labelValue.setText(value.toString());
	}

	@FXML
	public void clickButton(ActionEvent event) {
		Button button = (Button) event.getSource();

		if (button == buttonYes) {
			measureComponent.setZeroValueScaledRemembered(value);
		} else if (button == buttonAll) {
			for (Sensorable measureCompLoop : ChartData.getInstance().dataMap.keySet()) {
				XYSeries list = ChartData.getInstance().dataMap.get(measureCompLoop);

				if (list.getItemCount() == 0) {
					buttonAll.setDisable(true);
					labelName.setText("BRAK DANYCH W PAMIECI");
					return;
				}

				double zeroValue = getAverage(list, 10);
				measureCompLoop.setZeroValueScaledRemembered(zeroValue);
			}
		}

		Stage stage1 = (Stage) button.getScene().getWindow();
		stage1.close();
	}

	private double getAverage(XYSeries list, int avgPeriod) {
		int totalItems = list.getItemCount();
		double avgValue = 0.0;
		for (int i = 1; i <= avgPeriod; i++) {
			avgValue = avgValue + list.getDataItem(totalItems - i).getYValue();
		}
		avgValue = avgValue / avgPeriod;
		return avgValue;
	}
}
