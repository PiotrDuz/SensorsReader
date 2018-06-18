package operations.sensors;

/**
 * Class to convert raw time reading to defined one.<br>
 * BEWARE! It may influence PID or other time-based processes.<br>
 * Is a singleton.
 * 
 * @author Piotr Duzniak
 *
 */
public class TimeStamp implements Sensorable {

	private static TimeStamp stamp;

	private String name = "time";
	private String unit = "s";
	private double scale = 1000.0;

	public static TimeStamp getInstance() {
		if (stamp == null) {
			return new TimeStamp();
		} else {
			return stamp;
		}
	}

	/**
	 * Convert raw value to final. Raw is divided by scale.
	 * 
	 * @param variable
	 * @return
	 */
	public Double getMeasurement(int variable) {
		return variable / scale;
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

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 29;
		int result = 1;
		result = prime * result;
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
		TimeStamp other = (TimeStamp) obj;
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
