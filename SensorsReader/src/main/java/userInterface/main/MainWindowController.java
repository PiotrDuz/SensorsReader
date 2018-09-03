package main.java.userInterface.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.jfree.chart.fx.ChartCanvas;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.java.operations.initializator.Xml;
import main.java.operations.logger.ReadingsLogger;
import main.java.operations.pendrive.PendriveKeeper;
import main.java.operations.sensors.Sensorable;
import main.java.userInterface.combinationWindow.CombinationWindow;
import main.java.userInterface.sensorsWindow.SensorsWindow;
import main.java.userInterface.tareWindow.TareWindow;
import main.java.userInterface.timeWindow.TimeWindow;

/**
 * Controller class for {@link MainWindow}
 * 
 * @author Piotr Duzniak
 *
 */
public class MainWindowController implements Initializable {
	// not FXML
	private ReadingsLogger readingsLogger;
	private ChartData chartData;
	private boolean saveToFile = false;
	BooleanProperty menuDisable = new SimpleBooleanProperty(false);
	// ROOT
	@FXML
	AnchorPane root;
	// ComboBox
	@FXML
	ComboBox<Sensorable> comboChartTop;
	@FXML
	ComboBox<Sensorable> comboChartBottom;
	// Chart
	ChartCanvas chartTop;

	ChartCanvas chartBottom;

	@FXML
	GridPane gridChartTop;
	@FXML
	GridPane gridChartBottom;
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
	MenuItem menuSettingsAdd;
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
		// set font
		DoubleProperty fontSize = new SimpleDoubleProperty(8); // font size in pt
		root.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpt;", fontSize));
		// set chart properties
		chartTop = ChartCreator.getChart(gridChartTop);
		chartBottom = ChartCreator.getChart(gridChartBottom);
		// charts
		chartData = ChartData.getInstance(chartTop, chartBottom);

		initializeElements();

		buttonStop.setDisable(true);

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
			ChartCreator.changeSeries(chartTop, measuredData, chartData.dataMap.get(measuredData));
			ChartCreator.actualizeChart(chartTop);

		} else if (event.getSource().equals(comboChartBottom)) {
			Sensorable measuredData = comboChartBottom.getValue();
			if (measuredData == comboChartTop.getValue() || measuredData == null) {
				return;
			}

			ChartCreator.changeSeries(chartBottom, measuredData, chartData.dataMap.get(measuredData));
			ChartCreator.actualizeChart(chartBottom);
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
		} else if (menuItem == menuSettingsAdd) {
			// IS EMPTY RIGHT NOW
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
