package operations.sensors.combination;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import operations.sensors.Encoder;

@XmlRootElement(name = "CombinationData")
@XmlAccessorType(XmlAccessType.FIELD)
public class CombinationData {
	private String name;
	private String unit;
	private int iD;

	// @XmlElements({ @XmlElement(name = "variable", type = Variable.class) })
	// @XmlElementWrapper(name = "variableList")
	private ArrayList<Variable> variableList = new ArrayList<>();

	public void insertVariableMap(HashMap<String, Double> map) {
		for (String varName : map.keySet()) {
			variableList.add(new Variable(varName, map.get(varName)));
		}
	}

	public HashMap<String, Double> getVariableMap() {
		HashMap<String, Double> map = new HashMap<>();
		for (Variable var : variableList) {
			map.put(var.getName(), var.getValue());
		}
		return map;
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
}
