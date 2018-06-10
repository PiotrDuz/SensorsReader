package userInterface.main;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import operations.sensors.Sensor;

public class SensorPaneFactory {
	public static TitledPane getPane(Sensor sensor) {
		TitledPane tp = new TitledPane();
		tp.setText("test"); // sensor.getName());
		Text text1 = new Text("Email");

		GridPane grid = new GridPane();
		// Setting columns size in percent
		ColumnConstraints column = new ColumnConstraints();
		column.setPercentWidth(30);
		grid.getColumnConstraints().add(column);
		column = new ColumnConstraints();
		column.setPercentWidth(70);
		grid.getColumnConstraints().add(column);
		grid.add(text1, 0, 0);
		grid.setGridLinesVisible(true);
		tp.setContent(grid);
		return tp;
	}

}
