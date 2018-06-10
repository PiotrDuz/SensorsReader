package operations.sensors;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TimeStamp")
public class TimeStamp implements Sensorable {

	private static TimeStamp stamp;

	private String name;
	private String unit;
	private double scale;

	public static TimeStamp getInstance() {
		if (stamp == null) {
			return new TimeStamp();
		} else {
			return stamp;
		}
	}

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

}
