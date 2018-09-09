package main.java.operations.initializator;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import main.java.application.App;
import main.java.operations.arduino.Arduino;
import main.java.operations.sensors.Encoder;
import main.java.operations.sensors.Sensor;
import main.java.operations.sensors.SensorFactory;
import main.java.operations.sensors.Tensometer;
import main.java.operations.sensors.TimeStamp;
import main.java.operations.sensors.SensorFactory.SensorType;
import main.java.operations.sensors.combination.CombinationData;
import main.java.operations.sensors.combination.SensorCombination;
import main.java.operations.sensors.combination.SensorCombinationFactory;

/**
 * Class that saves user settings to XML file and retrieves them to objects.<br>
 * Currently, Arduino and Sensors classes are being saved. <br>
 * Retrieving should be invoked before classes are used by program.
 * 
 * @author piotr
 *
 */
public class Xml {
	public static final String CONFIG_NAME = "/settings.xml";
	public static String CONFIG_PATH = getRootPath();

	private static String getRootPath() {
		URI someURI = null;
		try {
			someURI = App.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		} catch (URISyntaxException exc) {
			System.out.println(exc);
		}

		String pathWithName = new File(someURI).getPath();
		String path = pathWithName.substring(0, pathWithName.lastIndexOf("/"));

		return path;
	}

	/**
	 * Retrieves objects from .XML file. {@link JAXBContext} class-input has to be
	 * only the class of top object,<br>
	 * which hold others. In this example, it is {@link XmlHelper} so for further
	 * edits<br>
	 * there is no need to change it.<br>
	 * Whenever new class is added to XML, its retrieval has to be managed here.
	 * 
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
				insertToMap(enc, enc.getType());
			} else if (object instanceof Tensometer) {
				Tensometer ten = (Tensometer) object;
				insertToMap(ten, ten.getType());
			} else if (object instanceof Arduino) {
				Arduino ard = (Arduino) object;
				Arduino.setInstance(ard);
			} else if (object instanceof CombinationData) {
				CombinationData combData = (CombinationData) object;
				SensorCombinationFactory.addToDataList(combData);
			} else if (object instanceof TimeStamp) {
				TimeStamp.setInstance((TimeStamp) object);
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
	private static void insertToMap(Sensor sensor, SensorType type) {

		sensor.setType(type);

		ConcurrentHashMap<Integer, Sensor> temporaryMap = SensorFactory.sensorMap.get(type);
		if (temporaryMap == null) {
			temporaryMap = new ConcurrentHashMap<>();
			SensorFactory.sensorMap.put(type, temporaryMap);
		}
		temporaryMap.put(sensor.getId(), sensor);
	}

	/**
	 * Saves objects to XML.<br>
	 * {@link JAXBContext} doesn't have to be changed, the {@link XmlHelper} class
	 * is a <br>
	 * parent class that holds other objects. The only thing to add <br>
	 * is a code that adds new objects to {@link XmlHelper#addToList(Object)}.
	 */
	public static void saveXml() {

		XmlHelper helperClass = new XmlHelper();

		// add Sensor objects to xml file.
		for (SensorType key : SensorFactory.sensorMap.keySet()) {
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

		// add TimeStamp object
		helperClass.addToList(TimeStamp.getInstance());

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
		File file = new File(CONFIG_PATH + CONFIG_NAME);
		marshaller.marshal(object, file);
	}

	private static XmlHelper unmarshal(JAXBContext jc) throws JAXBException {
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		File file = new File(CONFIG_PATH + CONFIG_NAME);
		return (XmlHelper) unmarshaller.unmarshal(file);
	}
}
