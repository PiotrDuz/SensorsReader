package operations.initializator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import operations.arduino.Arduino;
import operations.sensors.Encoder;
import operations.sensors.Tensometer;
import operations.sensors.combination.CombinationData;

/**
 * Helper class in creation of XML. <br>
 * It holds all objects that will be written to XML in a List<Object>.<br>
 * Each class that will be saved to the list should be properly annotated (above
 * the list, <br>
 * and at each class definition)
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
