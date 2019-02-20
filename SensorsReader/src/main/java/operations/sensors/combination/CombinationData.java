package operations.sensors.combination;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for holding {@link CombinationData} parameters and save them in XML.
 * 
 * @author Piotr Duzniak
 *
 */
@XmlRootElement(name = "CombinationData")
@XmlAccessorType(XmlAccessType.FIELD)
public class CombinationData {
	private String name;
	private String unit;
	private int iD;
	private double zeroValue;
	private boolean isCharted;
	private String choosenVar;

	private ArrayList<Variable> variableList = new ArrayList<>();

	public void insertVariableMap(HashMap<String, Variable> map) {
		for (String varName : map.keySet()) {
			variableList.add(map.get(varName));
		}
	}

	public HashMap<String, Variable> getVariableMap() {
		HashMap<String, Variable> map = new HashMap<>();
		for (Variable var : variableList) {
			map.put(var.getName(), var);
		}
		return map;
	}

	public String getChoosenVar() {
		return choosenVar;
	}

	public void setChoosenVar(String choosenVar) {
		this.choosenVar = choosenVar;
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

	public int getiD() {
		return iD;
	}

	public void setiD(int iD) {
		this.iD = iD;
	}

	/**
	 * @return the zeroValue
	 */
	public double getZeroValue() {
		return zeroValue;
	}

	/**
	 * @param zeroValue the zeroValue to set
	 */
	public void setZeroValue(double zeroValue) {
		this.zeroValue = zeroValue;
	}

	/**
	 * @return the isCharted
	 */
	public boolean isCharted() {
		return isCharted;
	}

	/**
	 * @param isCharted the isCharted to set
	 */
	public void setCharted(boolean isCharted) {
		this.isCharted = isCharted;
	}
}
