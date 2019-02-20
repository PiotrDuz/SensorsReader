package operations.sensors.combination;

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
	private boolean activable = false;
	/**
	 * Should be unique
	 */
	private String name;
	private Double value;

	public Variable() {

	}

	/**
	 * 
	 * @param name
	 * @param value
	 * @param activable should this variable has an activate possibility
	 */
	public Variable(String name, Double value, boolean activable) {
		this.name = name;
		this.value = value;
		this.activable = activable;
	}

	public String getName() {
		return name;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public boolean isActivable() {
		return this.activable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variable other = (Variable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
