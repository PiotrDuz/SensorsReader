package userInterface.main;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.jfree.chart.fx.ChartCanvas;

import application.ProgramException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import operations.initializator.Xml;
import operations.logger.ReadingsLogger;
import operations.pendrive.PendriveKeeper;
import operations.pendrive.PendriveMount;
import operations.sensors.SensorFactory;
import operations.sensors.Sensorable;
import operations.sensors.TimeStamp;
import operations.sensors.combination.SensorCombinationFactory;
import userInterface.addSensorWindow.AddSensorWindow;
import userInterface.bigDataWindow.BigDataWindow;
import userInterface.combinationWindow.CombinationWindow;
import userInterface.dateSetting.DateWindow;
import userInterface.keyboard.Keyboard;
import userInterface.prompt.PromptWindow;
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
	private ChartData chartData;
	private BigDataWindow dataWindow = new BigDataWindow();
	private boolean saveToFile = false;
	private String fileName = null;
	BooleanProperty menuDisableProperty = new SimpleBooleanProperty(false);
	// ROOT
	@FXML
	AnchorPane root;
	@FXML
	GridPane gridPaneMaster;

	// Chart
	ChartCanvas chartTop;

	@FXML
	GridPane gridChartTop;
	// Time display
	@FXML
	Text textTimeValue;
	@FXML
	Text textTimeUnit;
	// scale max value for force display
	@FXML
	Label textMaxValue;
	@FXML
	Label textMaxValueUnit;
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
	MenuItem menuPendriveFileName;
	@FXML
	Menu menuAction;
	@FXML
	MenuItem menuActionTare;
	@FXML
	MenuItem menuActionClean;
	@FXML
	MenuItem menuActionCleanAll;
	@FXML
	CheckMenuItem menuActionSaveState;
	@FXML
	Menu menuSystem;
	@FXML
	MenuItem menuSystemDate;
	@FXML
	MenuItem menuSystemReboot;
	@FXML
	MenuItem menuSystemShutdown;
	@FXML
	MenuItem menuSystemUpdate;
	@FXML
	MenuItem menuChartClear;
	@FXML
	CheckMenuItem menuChartShowPane;
	@FXML
	CheckMenuItem menuChartShowWindow;
	@FXML
	Menu menuChartItems;

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
		DoubleProperty fontSize = new SimpleDoubleProperty(12); // font size in pt
		root.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpt;", fontSize));
		// set chart properties
		chartTop = ChartCreator.getChart(gridChartTop);
		// initialize chart data arrays database
		chartData = ChartData.getInstance(menuChartShowPane, dataWindow);
		// connect timeValue with PaneFactory holding data
		SensorPaneFactory.setTimeTextValue(textTimeValue);
		setGridColumnSize(gridPaneMaster, 0, 2);

		initializeElements();

		// disable stop button
		buttonStop.disableProperty().bind(menuDisableProperty.not());
		buttonStart.disableProperty().bind(menuDisableProperty);
		// menu items binding
		menuSettings.disableProperty().bind(menuDisableProperty);
		menuPendrive.disableProperty().bind(menuDisableProperty);
		menuSystem.disableProperty().bind(menuDisableProperty);
	}

	/**
	 * Initialize elements that hold measurement objects.<br>
	 * Method should be used whenever measurement's objects set changes.
	 */
	public void initializeElements() {

		// set chart data points
		chartData.setDataPoints(TimeStamp.getInstance().getChartPoints());
		// actualize chartData map
		chartData.actualizeSeries();

		// Set menu items list from which chart data can be chosen
		menuChartItems.getItems().clear();
		for (Sensorable sens : chartData.dataMap.keySet()) {
			CheckMenuItem menuItem = new CheckMenuItem(sens.getName());
			menuItem.setUserData(sens);
			menuItem.setOnAction(this::selectChartItem);
			menuChartItems.getItems().add(menuItem);
		}

		// reset time units and value
		textTimeValue.setText(".");
		textTimeUnit.setText(TimeStamp.getInstance().getUnit());
		// update max scale value
		textMaxValue.setText(SensorCombinationFactory.combinationMap.get(0).getChosenVar());
		textMaxValueUnit.setText(SensorCombinationFactory.combinationMap.get(0).getUnit());
		// clear old Pane map, fill with new Panes
		vboxSensors.getChildren().clear();
		SensorPaneFactory.mapPane.clear();
		for (Sensorable sensor : chartData.dataMap.keySet()) {
			vboxSensors.getChildren().add(SensorPaneFactory.createPane(sensor));
		}

	}

	public Sensorable getSelectedChartSens() {
		Sensorable item = null;
		for (MenuItem menuItem : menuChartItems.getItems()) {
			CheckMenuItem checkItem = (CheckMenuItem) menuItem;
			if (checkItem.isSelected()) {
				item = (Sensorable) checkItem.getUserData();
			}
		}

		return item;
	}

	public void selectChartItem(ActionEvent event) {

		for (MenuItem menuItem : menuChartItems.getItems()) {
			CheckMenuItem checkItem = (CheckMenuItem) menuItem;
			checkItem.setSelected(false);
		}

		CheckMenuItem menuItem = (CheckMenuItem) event.getSource();
		menuItem.setSelected(true);

		if (menuItem.getUserData() == null) {
			return;
		}
		Sensorable chartItem = (Sensorable) menuItem.getUserData();

		ChartCreator.changeSeries(chartTop, chartItem, chartData.dataMap.get(chartItem));
		ChartCreator.actualizeChart(chartTop);

		if (dataWindow.isOpen()) {
			dataWindow.refresh(chartItem);
		}
	}

	/**
	 * Button Start/Stop - launching and stopping reading data.
	 * 
	 * @param event
	 * @throws ProgramException
	 */
	@FXML
	public void buttonPress(ActionEvent event) throws ProgramException {
		if (event.getSource().equals(buttonStart)) {
			// clean all data series, for new measurements to come
			chartData.cleanSeries();
			// if last state should not be saved, clear it
			if (!menuActionSaveState.isSelected()) {
				SensorFactory.clearState();
			}

			menuDisableProperty.set(true);

			readingsLogger = new ReadingsLogger(saveToFile, fileName);
			Thread thread = new Thread(readingsLogger, "ReadingsLogger");
			thread.start();

		} else if (event.getSource().equals(buttonStop)) {
			menuDisableProperty.set(false);

			readingsLogger.stop();
		}
	}

	@FXML
	public void clickMenuItemSettings(ActionEvent event) {
		MenuItem menuItem = (MenuItem) event.getSource();

		if (menuItem == menuSettingsSave) {
			Xml.saveXml();
			PromptWindow.getPrompt(menuBar, "Zapisano!");
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
		} else if (menuItem == menuSettingsAdd) {
			AddSensorWindow addSensorWindow = new AddSensorWindow((Node) menuBar);
			addSensorWindow.openWindow();
			initializeElements();
		}
	}

	@FXML
	public void clickMenuItemPendrive(ActionEvent event) {
		MenuItem menuItem = (MenuItem) event.getSource();

		if (menuItem == menuPendriveSave) {
			if (saveToFile == false) {
				saveToFile = true;
				labelSaving.setTextFill(Color.GREEN);
				labelSaving.setText("DO ZAPISU");
			} else {
				saveToFile = false;
				labelSaving.setTextFill(Color.RED);
				labelSaving.setText("BRAK ZAPISU");
			}
		} else if (menuItem == menuPendriveUnmount) {
			PendriveKeeper.getInstance().orderUnmount();
		} else if (menuItem == menuPendriveFileName) {
			Keyboard keyboard;
			if (fileName == null) {
				keyboard = new Keyboard(
						"Pomiar_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
			} else {
				keyboard = new Keyboard(fileName);
			}
			keyboard.display((Node) menuBar);
			// check if user has not cancelled the input
			if (keyboard.getText() != null) {
				fileName = keyboard.getText();
			}
		}
	}

	@FXML
	public void clickMenuItemSystem(ActionEvent event) {
		MenuItem menuItem = (MenuItem) event.getSource();

		if (menuItem == menuSystemDate) {
			DateWindow dateWindow = new DateWindow((Node) menuBar);
			dateWindow.openWindow();
		} else if (menuItem == menuSystemReboot) {
			String text = "Uruchomic ponownie?";
			if (PromptWindow.getPrompt(menuBar, text)) {
				PendriveMount.executeCommand("sudo shutdown -r now");
			}
		} else if (menuItem == menuSystemShutdown) {
			String text = "Wylaczyc system?";
			if (PromptWindow.getPrompt(menuBar, text)) {
				PendriveMount.executeCommand("sudo shutdown -h now");
			}
		} else if (menuItem == menuSystemUpdate) {
			String text = "Aktualizowac? \n Wymagane polaczenie z internetem: \n "
					+ "Postawic siec o SSID: raspberry_wifi \n i hasle: raspberry1";
			if (PromptWindow.getPrompt(menuBar, text)) {
				PendriveMount.executeCommand("~/update.sh");
			}
		}
	}

	@FXML
	public void clickMenuItemAction(ActionEvent event) {
		MenuItem menuItem = (MenuItem) event.getSource();

		if (menuItem == menuActionTare) {

			TareWindow tareWindow = new TareWindow((Node) menuBar, getSelectedChartSens());
			tareWindow.openWindow();
		} else if (menuItem == menuActionClean) {
			if (getSelectedChartSens() != null) {
				getSelectedChartSens().setMax(null);
				getSelectedChartSens().setMin(null);
			}
		} else if (menuItem == menuActionCleanAll) {
			for (Sensorable component : ChartData.getInstance().dataMap.keySet()) {
				component.setMax(null);
				component.setMin(null);
			}
		}
	}

	@FXML
	public void clickMenuItemChart(ActionEvent event) {
		MenuItem menuItem = (MenuItem) event.getSource();

		if (menuItem == menuChartShowPane) {
			if (menuChartShowPane.isSelected()) {
				setGridColumnSize(gridPaneMaster, 0, 200);
			} else {
				setGridColumnSize(gridPaneMaster, 0, 2);
			}
		} else if (menuItem == menuChartShowWindow) {
			if (menuChartShowWindow.isSelected()) {
				dataWindow.openWindow(getSelectedChartSens(), (Stage) menuBar.getScene().getWindow());
			} else {
				dataWindow.closeWindow();
			}
		} else if (menuItem == menuChartClear) {
			this.chartData.cleanSeries();
		}
	}

	private void setGridColumnSize(GridPane grid, int column, int size) {
		grid.getColumnConstraints().get(column).setMaxWidth(size);
		grid.getColumnConstraints().get(column).setPrefWidth(size);
		grid.getColumnConstraints().get(column).setMinWidth(size);
	}

	/**
	 * Called by pendrive keeping thread to update on screen information
	 * 
	 * @param flag
	 */
	public void changePendriveMountStatus(boolean flag) {
		if (flag == true) {
			labelPendrive.setTextFill(Color.GREEN);
			labelPendrive.setText("OBECNY PENDRIVE");
		} else {
			labelPendrive.setTextFill(Color.RED);
			labelPendrive.setText("BRAK PENDRIVE");
		}
	}
}
