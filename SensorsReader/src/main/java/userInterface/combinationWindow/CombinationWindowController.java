package userInterface.combinationWindow;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import operations.sensors.combination.SensorCombination;
import operations.sensors.combination.SensorCombinationFactory;
import operations.sensors.combination.Variable;
import userInterface.keyboard.Keyboard;

public class CombinationWindowController implements Initializable {
	@FXML
	ComboBox<SensorCombination> comboBox;
	@FXML
	TextField textFieldName;
	@FXML
	TextField textFieldUnit;
	@FXML
	TextField textFieldTare;
	@FXML
	GridPane gridPaneValues;
	@FXML
	Button buttonExit;
	@FXML
	Label labelEquation;
	@FXML
	CheckBox checkIsCharted;

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
		textFieldTare.setText(comboBox.getValue().getZeroValueScaled().toString());
		checkIsCharted.setSelected(comboBox.getValue().isCharted());

		// clear grid
		gridPaneValues.getChildren().clear();
		SensorCombination combination = comboBox.getValue();

		// set equation
		labelEquation.setText(combination.equationText());

		// fill grid with new variables
		int rowCount = gridPaneValues.getRowCount();
		for (String varName : combination.getVariables().keySet()) {
			Variable var = combination.getVariables().get(varName);
			// label - variable name
			Label label = new Label(varName);
			TextField textField = new TextField();
			// text field initial value is currently set variable value
			textField.setText(var.getValue().toString());
			// attach event to text field
			textField.setOnMouseClicked(this::textFieldGridClick);
			// add label and text field to grid
			gridPaneValues.add(label, 1, rowCount - 1);
			gridPaneValues.add(textField, 2, rowCount - 1);
			if (var.isActivable()) {
				CheckBox checkBox = new CheckBox();
				checkBox.setOnMouseClicked(this::clickCheckBox);
				if (var.getName().equals(comboBox.getValue().getChosenVar())) {
					checkBox.setSelected(true);
				}
				gridPaneValues.add(checkBox, 0, rowCount - 1);
			}

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

	public void clickCheckBox(MouseEvent event) {
		CheckBox currBox = (CheckBox) event.getSource();
		// uncheck any check box
		ObservableList<Node> children = gridPaneValues.getChildren();
		for (Node node : children) {
			// check boxes are column 0
			if (GridPane.getColumnIndex(node) == 0) {
				CheckBox gridBox = (CheckBox) node;
				gridBox.setSelected(false);
			}
		}
		// set current box as selected
		currBox.setSelected(true);
		// find which variable this checkbox references
		Label labelVar = (Label) getGridObject(currBox, 1);
		// set combination current variable
		SensorCombination comb = comboBox.getValue();
		comb.setChosenVar(labelVar.getText());
	}

	/**
	 * 
	 * @param rowObject
	 * @param columnToRetrieve
	 * @return
	 */
	public <T> Node getGridObject(T rowObject, int columnToRetrieve) {
		int rowNumber = GridPane.getRowIndex((Node) rowObject);
		Node object = null;
		ObservableList<Node> children = gridPaneValues.getChildren();
		for (Node node : children) {

			if (GridPane.getRowIndex(node) == rowNumber && GridPane.getColumnIndex(node) == columnToRetrieve) {
				object = node;
			}
		}
		if (object != null) {
			return object;
		} else {
			return null;
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
				// if not number then return
				return;
			}
			// find the row number in grid and return label of the same row
			Label varLabel = (Label) getGridObject(field, 1);
			String varName = varLabel.getText();
			// get CombinationData object and set its variable value
			Variable var = comboBox.getValue().getVariables().get(varName);
			var.setValue(number);
			// set provided value in textField
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
			// check if number
			boolean parsable = true;
			Double number = null;
			try {
				number = Double.parseDouble(keyboard.getText());
			} catch (NumberFormatException exc) {
				parsable = false;
				System.out.println(exc);
			}
			// find out which box data is entered
			if (field == textFieldName) {
				comboBox.getValue().setName(keyboard.getText());
			} else if (field == textFieldUnit) {
				comboBox.getValue().setUnit(keyboard.getText());
			} else if (field == textFieldTare) {
				if (parsable == false) {
					return;
				}
				comboBox.getValue().setZeroValueScaledRemembered(number);
			}
			field.setText(keyboard.getText());
		}
	}

	@FXML
	public void checkBox(ActionEvent event) {
		if (comboBox.getValue() == null) {
			return;
		}

		comboBox.getValue().isChartedSet(checkIsCharted.isSelected());
	}
}
