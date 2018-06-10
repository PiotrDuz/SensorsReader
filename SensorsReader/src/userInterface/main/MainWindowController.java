package userInterface.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import operations.initializator.Xml;
import operations.logger.ReadingsLogger;
import operations.sensors.Sensor;
import operations.sensors.SensorFactory;
import operations.sensors.SensorFactory.Type;
import operations.sensors.Sensorable;

public class MainWindowController implements Initializable {
	// not FXML
	ReadingsLogger readingsLogger;
	final ChartData chartData = ChartData.getInstance();
	//
	// ComboBox
	@FXML
	ComboBox<Sensorable> comboChartTop;
	@FXML
	ComboBox<Sensorable> comboChartBottom;
	// Chart
	@FXML
	LineChart<Double, Double> chartTop;
	@FXML
	NumberAxis axisXTop;
	@FXML
	NumberAxis axisYTop;
	@FXML
	LineChart<Double, Double> chartBottom;
	@FXML
	NumberAxis axisXBottom;
	@FXML
	NumberAxis axisYBottom;
	// Buttons
	@FXML
	Button buttonStart;
	@FXML
	Button buttonStop;
	// menuBar
	@FXML
	MenuBar menuBar;
	@FXML
	MenuItem menuSettingsSave;

	@FXML
	VBox vboxSensors;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		initializeElements();

		vboxSensors.getChildren().add(SensorPaneFactory.getPane(null));

		buttonStop.setDisable(true);
		// set chart properties
		chartTop.setAnimated(false);
		chartTop.setLegendVisible(false);
		chartTop.setCreateSymbols(false);
		axisXTop.setForceZeroInRange(false);
		axisYTop.setForceZeroInRange(false);
		chartBottom.setAnimated(false);
		chartBottom.setCreateSymbols(false);
		chartBottom.setLegendVisible(false);
		axisXBottom.setForceZeroInRange(false);
		axisYBottom.setForceZeroInRange(false);
	}

	public void initializeElements() {
		// actualize chartData map
		chartData.actualizeSeries();

		// Set comboBox items list
		ArrayList<Sensorable> dataList = new ArrayList<>(chartData.dataMap.keySet());
		comboChartTop.setItems(FXCollections.observableList(dataList));
		comboChartBottom.setItems(FXCollections.observableList(dataList));

	}

	@FXML
	public synchronized void comboSelection(ActionEvent event) {
		if (event.getSource().equals(comboChartTop)) {
			Sensorable measuredData = comboChartTop.getValue();
			if (measuredData == comboChartBottom.getValue()) {
				return;
			}
			chartTop.setTitle(measuredData.getName() + "  f( [s] ) = [" + measuredData.getUnit() + "]");
			chartTop.getData().clear();
			chartTop.getData().add(chartData.dataMap.get(measuredData));
		} else if (event.getSource().equals(comboChartBottom)) {
			Sensorable measuredData = comboChartBottom.getValue();
			if (measuredData == comboChartTop.getValue()) {
				return;
			}
			chartBottom.setTitle(measuredData.getName() + "  f( [s] ) = [" + measuredData.getUnit() + "]");
			chartBottom.getData().clear();
			chartBottom.getData().add(chartData.dataMap.get(measuredData));
		}
	}

	@FXML
	public void buttonPress(ActionEvent event) {
		if (event.getSource().equals(buttonStart)) {
			// clean all data series, for new measurements to come
			chartData.cleanSeries();
			menuBar.setDisable(true);
			buttonStart.setDisable(true);
			// enable STOP button
			buttonStop.setDisable(false);
			readingsLogger = new ReadingsLogger();
			Thread thread = new Thread(readingsLogger, "ReadingsLogger");
			thread.start();
		} else if (event.getSource().equals(buttonStop)) {
			menuBar.setDisable(false);
			buttonStart.setDisable(false);
			// disable STOP button
			buttonStop.setDisable(true);
			readingsLogger.stop();
		}
	}

	@FXML
	public void clickMenuItem(ActionEvent event) {
		if (event.getSource().equals(menuSettingsSave)) {
			Xml.saveXml();
			System.out.println("saved");
		}
	}
}
