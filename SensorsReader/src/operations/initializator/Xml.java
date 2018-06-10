package operations.initializator;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import operations.arduino.Arduino;
import operations.sensors.Encoder;
import operations.sensors.Sensor;
import operations.sensors.SensorFactory;
import operations.sensors.Tensometer;
import operations.sensors.combination.CombinationData;
import operations.sensors.combination.SensorCombination;
import operations.sensors.combination.SensorCombinationFactory;
import operations.sensors.combination.Variable;
import userInterface.main.ChartData;
import operations.sensors.SensorFactory.Type;

/**
 * Class that saves user settings to XML file and retrieves them to objects
 * Currently, Arduino and Sensors classes are being saved.
 * <p>
 * Retrieving is a first invoked method inside Main()
 * 
 * @author piotr
 *
 */
public class Xml {
	public static final String CONFIG_NAME = "settings.xml";

	/**
	 * Retrieves Arduino and Sensor objects from XML
	 */
	public static void retrieveXml() {
		XmlHelper helperClass = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(XmlHelper.class);
			helperClass = unmarshal(jc);
		} catch (JAXBException exc) {
			System.out.println(exc);
		}

		for (Object object : helperClass.getList()) {
			if (object instanceof Encoder) {
				Encoder enc = (Encoder) object;
				insertToMap(enc, Type.ENCODER);
			} else if (object instanceof Tensometer) {
				Tensometer ten = (Tensometer) object;
				insertToMap(ten, Type.TENSOMETER);
			} else if (object instanceof Arduino) {
				Arduino ard = (Arduino) object;
				Arduino.setInstance(ard);
			} else if (object instanceof CombinationData) {
				CombinationData combData = (CombinationData) object;
				SensorCombinationFactory.addToDataList(combData);
			}
		}
	}

	/**
	 * Inserts Sensor object to Sensor's map
	 * 
	 * @param sensor
	 *            Object to insert
	 * @param type
	 *            Type
	 */
	private static void insertToMap(Sensor sensor, Type type) {
		ConcurrentHashMap<Integer, Sensor> temporaryMap = SensorFactory.sensorMap.get(type);
		if (temporaryMap == null) {
			temporaryMap = new ConcurrentHashMap<>();
			SensorFactory.sensorMap.put(type, temporaryMap);
		}
		temporaryMap.put(sensor.getId(), sensor);
	}

	/**
	 * Saves Sensor and Arduino objects to XML
	 */
	public static void saveXml() {

		XmlHelper helperClass = new XmlHelper();

		// add sensor objects to xml file.
		for (Type key : SensorFactory.sensorMap.keySet()) {
			for (Sensor sensor : SensorFactory.sensorMap.get(key).values()) {
				helperClass.addToList(sensor);
			}
		}
		// add Arduino object to xml file.
		helperClass.addToList(Arduino.getInstance());
		// add CombinationData objects
		for (SensorCombination combination : SensorCombinationFactory.combinationMap.values()) {
			helperClass.addToList(combination.getCombinationData());
		}

		// marshall list to XML. Let JAXB know which classes will be used
		try {
			JAXBContext jc = JAXBContext.newInstance(XmlHelper.class);
			marshal(jc, helperClass);
		} catch (JAXBException exc) {
			System.out.println(exc);
		}
	}

	/**
	 * Sends objects parameters to XML file
	 * 
	 * @param jc
	 *            JAXBContext object, specifying which Classes will be used
	 * @param object
	 *            Object which parameters to save
	 * @throws JAXBException
	 */
	private static void marshal(JAXBContext jc, Object object) throws JAXBException {
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		File file = new File(Xml.CONFIG_NAME);
		marshaller.marshal(object, file);
	}

	private static XmlHelper unmarshal(JAXBContext jc) throws JAXBException {
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		File file = new File(Xml.CONFIG_NAME);
		return (XmlHelper) unmarshaller.unmarshal(file);
	}
}
