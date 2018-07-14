package main.java.operations.sensors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class extending {@link Sensor}. <br>
 * Hold settings exclusive for "Tensometer", that are not common for all
 * sensors.
 * 
 * @author Piotr Duzniak
 *
 */
@XmlRootElement(name = "Tensometer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tensometer extends Sensor {

	public Tensometer() {
	}
}
