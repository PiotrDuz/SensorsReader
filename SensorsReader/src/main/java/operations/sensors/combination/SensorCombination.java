package operations.sensors.combination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlTransient;

import operations.sensors.Measurable;
import operations.sensors.Sensor;
import operations.sensors.Sensorable;

/**
 * Class for defining non-linear transformation from raw measurement to final.
 * <br>
 * Add {@link Sensor} to sensors list via {@link SensorCombination#addSensor()}.
 * <br>
 * Add a coefficient (Variable) via
 * {@link SensorCombination#addVariable(String)}. <br>
 * Then, inside {@link SensorCombination#getMeasurement(LinkedHashMap)} , invoke
 * them with proper index. Remember to also initialize equation string
 * ({@link SensorCombination#equationText()}.
 * <p>
 * Use defined fields in object:
 * <p>
 * --{@link SensorCombination#sensors} : a list of added sensors, use for
 * retrieving measurements from LinkedHashMap
 * <p>
 * --{@link SensorCombination#variables} : an array, use for referring to
 * variables
 * 
 * @author Piotr Duzniak
 *
 */

public class SensorCombination implements Measurable, Sensorable {
	private String name = "Comb";
	private String unit;
	private int iD;

	private Double maxValue = null;
	private Double minValue = null;

	protected double zeroValue = 0;

	protected boolean isCharted = true;

	protected ArrayList<Sensor> sensors = new ArrayList<>();
	protected HashMap<String, Double> variables = new HashMap<>();

	public SensorCombination() {
	}

	public SensorCombination(int iD) {
		this.iD = iD;
	}

	public void setCombinationData(CombinationData data) {
		this.name = data.getName();
		this.unit = data.getUnit();
		this.variables = data.getVariableMap();
		this.zeroValue = data.getZeroValue();
		this.isCharted = data.isCharted();
	}

	public CombinationData getCombinationData() {
		CombinationData data = new CombinationData();
		data.setName(this.name);
		data.setUnit(this.unit);
		data.insertVariableMap(this.variables);
		data.setiD(this.iD);
		data.setZeroValue(this.zeroValue);
		data.setCharted(this.isCharted);
		return data;
	}

	public double getMeasurement(LinkedHashMap<Measurable, Double> map) {
		double result = customMeasurementMethod(map);
		if (maxValue == null || result > maxValue) {
			maxValue = result;
		} else if (minValue == null || result < minValue) {
			minValue = result;
		}

		return result;
	}

	public double customMeasurementMethod(LinkedHashMap<Measurable, Double> map) {
		return 0.0;
	}

	public void setMax(Double max) {
		this.maxValue = max;
	}

	public Double getMax() {
		return maxValue;
	}

	public void setMin(Double min) {
		this.minValue = min;
	}

	public Double getMin() {
		return minValue;
	}

	public SensorCombination addSensor(Sensor sensor) {
		sensors.add(sensor);
		return this;
	}

	public SensorCombination addVariable(String name) {
		variables.put(name, 1.0);
		return this;
	}

	@Override
	public String toString() {
		return name;
	}

	public String equationText() {
		return null;
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

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getId() {
		return iD;
	}

	public HashMap<String, Double> getVariables() {
		return variables;
	}

	public void setVariables(HashMap<String, Double> variables) {
		this.variables = variables;
	}

	public synchronized void setZeroValue(double number) {
		zeroValue = number;
	}

	public Double getZeroValue() {
		return zeroValue;
	}

	public boolean isCharted() {
		return isCharted;
	}

	public void isChartedSet(boolean flag) {
		this.isCharted = flag;
	}

	/**
	 * There is no scale, so new value is just added to old zeroValue.
	 */
	public synchronized void setZeroValueScaled(double number) {
		zeroValue = zeroValue + number;
	}

	/**
	 * There is no scale, so zeroValue is just returned.
	 */
	public Double getZeroValueScaled() {
		return zeroValue;
	}

	public Measurable getXAxis() {
		return null;
	}

	public Sensorable getYAxis() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iD;
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
		SensorCombination other = (SensorCombination) obj;
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
		return true;
	}
}
