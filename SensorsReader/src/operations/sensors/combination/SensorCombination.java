package operations.sensors.combination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import operations.sensors.Sensor;
import operations.sensors.Sensorable;

/**
 * Class for defining non-linear transformation from raw measurement to Add
 * {@link Sensor} to sensors list via {@link SensorCombination#addSensor()}.
 * Then, inside corresponding method of new SensorCombination object, invoke
 * them with proper index. Remember to initialize equation string.
 * <p>
 * Use defined fields in object:
 * <p>
 * --{@link SensorCombination#sensors} : a list, use for retrieving measurement
 * from LinkedHashMap
 * <p>
 * --{@link SensorCombination#variables} : an array, use for using variables
 * 
 * @author Piotr Duzniak
 *
 */
public class SensorCombination implements Sensorable {
	private String name;
	private String unit;
	private String equation;
	private int iD;
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
	}

	public CombinationData getCombinationData() {
		CombinationData data = new CombinationData();
		data.setName(this.name);
		data.setUnit(this.unit);
		data.insertVariableMap(this.variables);
		data.setiD(this.iD);
		return data;
	}

	public double getMeasurement(LinkedHashMap<Sensorable, Double> map) {
		return 0.0;
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

	public SensorCombination setName(String name) {
		this.name = name;
		return this;
	}

	public String getUnit() {
		return unit;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
