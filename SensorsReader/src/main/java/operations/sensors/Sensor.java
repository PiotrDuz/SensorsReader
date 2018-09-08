package main.java.operations.sensors;

import javax.xml.bind.annotation.*;

import main.java.operations.sensors.SensorFactory.Type;

/**
 * Class holding all settings for sensor.<br>
 * Use method {@link Sensor#getMeasurement(int)} to go from raw to final values.
 * 
 * @author Piotr Duzniak
 *
 */
@XmlSeeAlso({ Encoder.class, Tensometer.class })
@XmlAccessorType(XmlAccessType.FIELD)
public class Sensor implements Measurable, Sensorable {
	protected double scale = 1;
	protected Double zeroValue = 0.0;

	protected double maxValue = 0;
	protected double minValue = 0;

	protected String name;
	protected String unit = "N";

	protected boolean isCharted = true;

	protected int iD;

	@XmlTransient
	protected Type type;

	/**
	 * Assign new ID and add object to the List of all sensors
	 */
	public Sensor() {
	}

	/**
	 * Take raw reading and alter it to get final gauged value.
	 * 
	 * @param value
	 * @return
	 */
	public double getMeasurement(int value) {
		double measurement = (value - zeroValue) / scale;

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

	public Double getMax() {
		return maxValue;
	}

	public Double getMin() {
		return minValue;
	}

	public Double getScale() {
		return scale;
	}

	public void setScale(double number) {
		scale = number;
	}

	public synchronized void setZeroValue(double number) {
		zeroValue = number;
	}

	public Double getZeroValue() {
		return zeroValue;
	}

	public synchronized void setZeroValueScaled(double number) {
		zeroValue = zeroValue + number * scale;
	}

	public Double getZeroValueScaled() {
		return zeroValue / scale;
	}

	public Integer getId() {
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

	public boolean isCharted() {
		return isCharted;
	}

	public void isChartedSet(boolean flag) {
		this.isCharted = flag;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 17;
		int result = 1;
		result = prime * result + iD;
		result = prime * result + ((type == null) ? 0 : type.hashCode());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sensor other = (Sensor) obj;
		if (iD != other.iD)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		if (zeroValue != other.zeroValue)
			return false;
		return true;
	}

}