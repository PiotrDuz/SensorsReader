package operations.initializator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import operations.arduino.Arduino;
import operations.sensors.Encoder;
import operations.sensors.Tensometer;
import operations.sensors.combination.CombinationData;

/**
 * Helper class in creation of XML It holds all objects to write to XML in a
 * List<Object> Each class that will be written have to be preconfigured in
 * 
 * @XmlElements annotation!!
 * @author piotr
 *
 */
@XmlRootElement(name = "ObjectsList")
public class XmlHelper {

	@XmlElements({ @XmlElement(name = "Encoder", type = Encoder.class),
			@XmlElement(name = "Tensometer", type = Tensometer.class),
			@XmlElement(name = "Arduino", type = Arduino.class),
			@XmlElement(name = "CombinationData", type = CombinationData.class) })
	private List<Object> objects = new ArrayList<>();

	public List<Object> getList() {
		return objects;
	}

	public void addToList(Object object) {
		objects.add(object);
	}
}
