package main.java.userInterface.main;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import main.java.operations.sensors.Sensorable;
import main.java.operations.sensors.TimeStamp;

/**
 * Creating and managing TitledPanes that show real-time measurement parameters.
 * <br>
 * Incorporates a hashmap that stores generated pane's.
 * 
 * @author piotr
 *
 */
public class SensorPaneFactory {
	private static final int PANE_WIDTH = 200;
	public final static ConcurrentHashMap<Sensorable, PaneValues> mapPane = new ConcurrentHashMap<>();
	private volatile static Text timeTextValue = null;

	public static void setTimeTextValue(Text text) {
		timeTextValue = text;
	}

	public static Text getTimeTextValue() {
		return timeTextValue;
	}

	/**
	 * 
	 * @param sensor
	 * @return
	 */
	public static TitledPane createPane(Sensorable sensor) {
		TitledPane tp = new TitledPane();
		tp.setText(sensor.getName()); // sensor name;
		Text textValue = new Text(".");
		Text textValueName = new Text("Wart:");
		Text textValueUnit = new Text(sensor.getUnit());
		Text textMax = new Text(".");
		Text textMaxName = new Text("Maks:");
		Text textMaxUnit = new Text(sensor.getUnit());
		Text textMin = new Text(".");
		Text textMinName = new Text("Min:");
		Text textMinUnit = new Text(sensor.getUnit());
		Text textSpeed = new Text(".");
		Text textSpeedName = new Text("Predk:");
		Text textSpeedUnit = new Text(sensor.getUnit() + "/" + TimeStamp.getInstance().getUnit());

		GridPane grid = new GridPane();
		grid.setMinWidth(PANE_WIDTH);
		// Setting columns size in percent
		ColumnConstraints column = new ColumnConstraints();
		column.setPercentWidth(25);
		grid.getColumnConstraints().add(column);
		column = new ColumnConstraints();
		column.setPercentWidth(45);
		grid.getColumnConstraints().add(column);
		column = new ColumnConstraints();
		column.setPercentWidth(30);
		grid.getColumnConstraints().add(column);

		grid.add(textValueName, 0, 0);
		grid.add(textValue, 1, 0);
		grid.add(textValueUnit, 2, 0);
		grid.add(textMaxName, 0, 1);
		grid.add(textMax, 1, 1);
		grid.add(textMaxUnit, 2, 1);
		grid.add(textMinName, 0, 2);
		grid.add(textMin, 1, 2);
		grid.add(textMinUnit, 2, 2);
		grid.add(textSpeedName, 0, 3);
		grid.add(textSpeed, 1, 3);
		grid.add(textSpeedUnit, 2, 3);
		grid.setGridLinesVisible(false);

		tp.setContent(grid);

		PaneValues values = new PaneValues(textValue, textMax, textMin, textSpeed);
		mapPane.put(sensor, values);

		return tp;
	}

	public static class PaneValues {
		protected Text value;
		protected Text max;
		protected Text min;
		protected Text speed;
		public static DecimalFormat df = new DecimalFormat("#.000");

		public PaneValues(Text value, Text max, Text min, Text speed) {
			this.value = value;
			this.max = max;
			this.min = min;
			this.speed = speed;
		}

		public void setValue(Double numb) {
			value.setText(df.format(numb));
		}

		public void setMax(Double numb) {
			if (numb != null) {
				max.setText(df.format(numb));
			} else {
				max.setText(".");
			}
		}

		public void setMin(Double numb) {
			if (numb != null) {
				min.setText(df.format(numb));
			} else {
				min.setText(".");
			}
		}

		public void setSpeed(Double numb) {
			speed.setText(df.format(numb));
		}
	}

}
