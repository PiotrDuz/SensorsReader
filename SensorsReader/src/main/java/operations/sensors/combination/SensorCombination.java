package operations.sensors.combination;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

public class SensorCombination implements Sensorable {
	private String name = "Comb";
	private String unit;
	private int iD;

	private Double maxValue = null;
	private Double minValue = null;

	protected double zeroValue = 0;

	protected boolean isCharted = true;
	/**
	 * Map of variables used in calculations
	 */
	protected HashMap<String, Variable> variables = new HashMap<>();
	/**
	 * Variable currently set to be chosen from radio buttons
	 */
	protected String choosenVar = null;

	public SensorCombination() {
	}

	public SensorCombination(int iD) {
		this.iD = iD;
	}

	public void setCombinationData(CombinationData data) {
		this.name = data.getName();
		this.unit = data.getUnit();
		this.zeroValue = data.getZeroValue();
		this.isCharted = data.isCharted();
		this.choosenVar = data.getChoosenVar();

		Map<String, Variable> varDataMap = data.getVariableMap();
		for (String var : this.variables.keySet()) {
			if (varDataMap.get(var) != null) {
				this.variables.put(var, varDataMap.get(var));
			}
		}
	}

	public CombinationData getCombinationData() {
		CombinationData data = new CombinationData();
		data.setName(this.name);
		data.setUnit(this.unit);
		data.insertVariableMap(this.variables);
		data.setiD(this.iD);
		data.setZeroValue(this.zeroValue);
		data.setCharted(this.isCharted);
		data.setChoosenVar(this.choosenVar);
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

	public SensorCombination addVariable(Variable var) {
		variables.put(var.getName(), var);
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

	public void setChosenVar(String var) {
		this.choosenVar = var;
	}

	public String getChosenVar() {
		return this.choosenVar;
	}

	public int getId() {
		return iD;
	}

	public HashMap<String, Variable> getVariables() {
		return variables;
	}

	public void setVariables(HashMap<String, Variable> variables) {
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
	public synchronized void setZeroValueScaledRemembered(double number) {
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
