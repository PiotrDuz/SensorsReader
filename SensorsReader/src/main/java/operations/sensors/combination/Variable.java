package main.java.operations.sensors.combination;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Helper class, to store HashMap values.
 * 
 * @author Piotr Duzniak
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Variable {
	private String name;
	private Double value;

	public Variable() {

	}

	public Variable(String name, Double value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Double getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
