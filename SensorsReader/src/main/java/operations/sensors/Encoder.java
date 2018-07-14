package main.java.operations.sensors;

import javax.xml.bind.annotation.*;

/**
 * Class extending {@link Sensor}. <br>
 * Hold settings exclusive for "Encoder", that are not common for all sensors.
 * 
 * @author Piotr Duzniak
 *
 */
@XmlRootElement(name = "Encoder")
@XmlAccessorType(XmlAccessType.FIELD)
public class Encoder extends Sensor {

	public Encoder() {
	}

}
