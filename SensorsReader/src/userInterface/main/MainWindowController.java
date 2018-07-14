package userInterface.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import operations.initializator.Xml;
import operations.logger.ReadingsLogger;
import operations.pendrive.PendriveKeeper;
import operations.sensors.Sensorable;
import operations.sensors.TimeStamp;
import userInterface.combinationWindow.CombinationWindow;
import userInterface.sensorsWindow.SensorsWindow;
import userInterface.tareWindow.TareWindow;
import userInterface.timeWindow.TimeWindow;

/**
 * Controller class for {@link MainWindow}
 * 
 * @author Piotr Duzniak
 *
 */
public class MainWindowController implements Initializable {
	// not FXML
	private ReadingsLogger readingsLogger;
	final ChartData chartData = ChartData.getInstance();
	private boolean saveToFile = false;
	BooleanProperty menuDisable = new SimpleBooleanProperty(false);
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
	Menu menuSettings;
	@FXML
	MenuItem menuSettingsSave;
	@FXML
	MenuItem menuSettingsSensors;
	@FXML
	MenuItem menuSettingsCombinations;
	@FXML
	MenuItem menuSettingsTime;
	@FXML
	Menu menuPendrive;
	@FXML
	MenuItem menuPendriveSave;
	@FXML
	MenuItem menuPendriveUnmount;
	@FXML
	Menu menuAction;
	@FXML
	MenuItem menuActionTare;
	@FXML
	MenuItem menuActionClean;
	@FXML
	MenuItem menuActionCleanAll;

	// Sensors real-time info
	@FXML
	VBox vboxSensors;
	// info
	@FXML
	Label labelSaving;
	@FXML
	Label labelPendrive;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		initializeElements();

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
		// menu items binding
		menuSettings.disableProperty().bind(menuDisable);
		menuPendrive.disableProperty().bind(menuDisable);
		menuAction.disableProperty().bind(menuDisable.not());
	}

	/**
	 * Initialize elements that hold measurement objects.<br>
	 * Method should be used whenever measurement's objects set changes.
	 */
	public void initializeElements() {
		// actualize chartData map
		chartData.actualizeSeries();

		// Set comboBox items list
		ArrayList<Sensorable> dataList = new ArrayList<>(chartData.dataMap.keySet());
		comboChartTop.setItems(FXCollections.observableList(dataList));
		comboChartBottom.setItems(FXCollections.observableList(dataList));

		// clear old Pane map, fill with new Panes
		vboxSensors.getChildren().clear();
		SensorPaneFactory.mapPane.clear();
		for (Sensorable sensor : chartData.dataMap.keySet()) {
			vboxSensors.getChildren().add(SensorPaneFactory.createPane(sensor));
		}
	}

	/**
	 * Changes chart info and data on selection from ComboBox
	 * 
	 * @param event
	 */
	@FXML
	public synchronized void comboSelection(ActionEvent event) {
		if (event.getSource().equals(comboChartTop)) {
			Sensorable measuredData = comboChartTop.getValue();
			if (measuredData == comboChartBottom.getValue() || measuredData == null) {
				return;
			}
			chartTop.setTitle(measuredData.getName() + "  f( [" + TimeStamp.getInstance().getUnit() + "] ) = ["
					+ measuredData.getUnit() + "]");
			chartTop.getData().clear();
			chartTop.getData().add(chartData.dataMap.get(measuredData));
			chartTop.layout();
		} else if (event.getSource().equals(comboChartBottom)) {
			Sensorable measuredData = comboChartBottom.getValue();
			if (measuredData == comboChartTop.getValue() || measuredData == null) {
				return;
			}
			chartBottom.setTitle(measuredData.getName() + "  f( [" + TimeStamp.getInstance().getUnit() + "] ) = ["
					+ measuredData.getUnit() + "]");
			chartBottom.getData().clear();
			chartBottom.getData().add(chartData.dataMap.get(measuredData));
			chartBottom.layout();
		}
	}

	/**
	 * Button Start/Stop - launching and stopping reading data.
	 * 
	 * @param event
	 */
	@FXML
	public void buttonPress(ActionEvent event) {
		if (event.getSource().equals(buttonStart)) {
			// clean all data series, for new measurements to come
			chartData.cleanSeries();

			menuDisable.set(true);

			buttonStart.setDisable(true);
			// enable STOP button
			buttonStop.setDisable(false);

			readingsLogger = new ReadingsLogger(saveToFile);
			Thread thread = new Thread(readingsLogger, "ReadingsLogger");
			thread.start();

		} else if (event.getSource().equals(buttonStop)) {
			menuDisable.set(false);

			buttonStart.setDisable(false);
			// disable STOP button
			buttonStop.setDisable(true);
			readingsLogger.stop();
		}
	}

	/**
	 * Reacts on actions done in MenuBar.
	 * 
	 * @param event
	 */
	@FXML
	public void clickMenuItem(ActionEvent event) {
		MenuItem menuItem = (MenuItem) event.getSource();

		if (menuItem == menuSettingsSave) {
			Xml.saveXml();
			System.out.println("saved");
		} else if (menuItem == menuPendriveSave) {
			if (saveToFile == false) {
				saveToFile = true;
				labelSaving.setTextFill(Color.GREEN);
				labelSaving.setText("TAK");
			} else {
				saveToFile = false;
				labelSaving.setTextFill(Color.RED);
				labelSaving.setText("BRAK");
			}
		} else if (menuItem == menuPendriveUnmount) {
			PendriveKeeper.getInstance().orderUnmount();
		} else if (menuItem == menuSettingsSensors) {
			SensorsWindow sensorsWindow = new SensorsWindow((Node) menuBar);
			sensorsWindow.openWindow();
			initializeElements();
		} else if (menuItem == menuSettingsCombinations) {
			CombinationWindow combinationWindow = new CombinationWindow((Node) menuBar);
			combinationWindow.openWindow();
			initializeElements();
		} else if (menuItem == menuSettingsTime) {
			TimeWindow timeWindow = new TimeWindow((Node) menuBar);
			timeWindow.openWindow();
			initializeElements();
		} else if (menuItem == menuActionTare) {
			TareWindow tareWindow = new TareWindow((Node) menuBar, comboChartTop.getValue());
			tareWindow.openWindow();
		}
	}

	public void changePendriveMountStatus(boolean flag) {
		if (flag == true) {
			labelPendrive.setTextFill(Color.GREEN);
			labelPendrive.setText("OBECNY");
		} else {
			labelPendrive.setTextFill(Color.RED);
			labelPendrive.setText("BRAK");
		}
	}
}
