package userInterface.combinationWindow;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import operations.sensors.combination.SensorCombination;
import operations.sensors.combination.SensorCombinationFactory;
import userInterface.keyboard.Keyboard;

public class CombinationWindowController implements Initializable {
	@FXML
	ComboBox<SensorCombination> comboBox;
	@FXML
	TextField textFieldName;
	@FXML
	TextField textFieldUnit;
	@FXML
	GridPane gridPaneValues;
	@FXML
	Button buttonExit;
	@FXML
	Label labelEquation;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		for (int i = 0; i < SensorCombinationFactory.combinationMap.size(); i++) {
			comboBox.getItems().add(SensorCombinationFactory.combinationMap.get(i));
		}

	}

	@FXML
	public void comboBoxChoose(ActionEvent event) {
		textFieldName.setText(comboBox.getValue().getName());
		textFieldUnit.setText(comboBox.getValue().getUnit());
		// clear grid
		gridPaneValues.getChildren().clear();
		SensorCombination combination = comboBox.getValue();
		// set equation
		labelEquation.setText(combination.equationText());
		// fill grid with new variables
		int rowCount = gridPaneValues.getRowCount();
		for (String varName : combination.getVariables().keySet()) {
			Label label = new Label(varName);
			TextField textField = new TextField();
			textField.setText(combination.getVariables().get(varName).toString());
			textField.setOnMouseClicked(this::textFieldGridClick);
			gridPaneValues.add(label, 0, rowCount - 1);
			gridPaneValues.add(textField, 1, rowCount - 1);
			rowCount++;
		}
	}

	@FXML
	public void clickButton(ActionEvent event) {
		if (event.getSource().equals(buttonExit)) {
			Stage stage = (Stage) buttonExit.getScene().getWindow();
			stage.close();
		}
	}

	public void textFieldGridClick(MouseEvent event) {
		// sensor must be chosen, if not, exit
		if (comboBox.getValue() == null) {
			return;
		}
		// call keyboard
		TextField field = (TextField) event.getSource();
		Keyboard keyboard = new Keyboard();
		keyboard.display((Node) field);
		// check if user has not cancelled the input
		if (keyboard.getText() != null) {
			// check if it is a number
			Double number = null;
			try {
				number = Double.parseDouble(keyboard.getText());
			} catch (NumberFormatException exc) {
				System.out.println(exc);
				return;
			}
			// find the row number in grid and return label of the same row
			int rowNumber = GridPane.getRowIndex((Node) field);
			Node label = null;
			ObservableList<Node> children = gridPaneValues.getChildren();
			for (Node node : children) {
				if (GridPane.getRowIndex(node) == rowNumber && GridPane.getColumnIndex(node) == 0) {
					label = node;
				}
			}
			String varName = ((Label) label).getText();
			// get CombinationData object and set its variable value
			comboBox.getValue().getVariables().put(varName, number);

			field.setText(keyboard.getText());
		}
	}

	@FXML
	public void textFieldClick(MouseEvent event) {
		// sensor must be chosen
		if (comboBox.getValue() == null) {
			return;
		}
		TextField field = (TextField) event.getSource();
		Keyboard keyboard = new Keyboard();
		keyboard.display((Node) field);
		// check if user has not cancelled the edit
		if (keyboard.getText() != null) {

			// find out which box data is entered
			if (field == textFieldName) {
				comboBox.getValue().setName(keyboard.getText());
			} else if (field == textFieldUnit) {
				comboBox.getValue().setUnit(keyboard.getText());
			}
			field.setText(keyboard.getText());
		}

	}
}
