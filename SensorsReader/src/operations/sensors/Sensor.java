package operations.sensors;

import javax.xml.bind.annotation.*;

@XmlSeeAlso({ Encoder.class, Tensometer.class })
public class Sensor implements Sensorable {
	protected double scale = 1;
	protected int maxReading;
	protected int zeroValue = 0;

	protected double maxValue = 0;
	protected double minValue = 0;

	protected String name;
	protected String unit = "N";

	protected int iD;

	/**
	 * Assign new ID and add object to the List of all sensors
	 */
	public Sensor() {
	}

	public double getMeasurement(int value) {
		int var = value - zeroValue;
		double measurement = var * scale;

		if (measurement > maxValue) {
			maxValue = measurement;
		}
		if (measurement < minValue) {
			minValue = measurement;
		}
		return measurement;
	}

	@Override
	public String toString() {
		return name;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double number) {
		scale = number;
	}

	public void setMaxReading(int number) {
		maxReading = number;
	}

	public int getMaxReading() {
		return maxReading;
	}

	public void setZeroValue(int number) {
		zeroValue = number;
	}

	public int getId() {
		return iD;
	}

	public void setId(int i) {
		iD = i;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String text) {
		unit = text;
	}
}