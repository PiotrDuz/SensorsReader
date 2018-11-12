package operations.sensors;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Map;

/**
 * Class to create, and hold all {@link Sensor} objects.
 * 
 * @author Piotr Duzniak
 *
 */
public class SensorFactory {
	/**
	 * ConcurrentHashMap holding all sensors, having <br>
	 * key1={@link SensorType}, key2=ID, value={@link Sensor}
	 */
	public static final ConcurrentHashMap<SensorType, ConcurrentHashMap<Integer, Sensor>> sensorMap = new ConcurrentHashMap<>();
	/**
	 * Defines what is the order of types ({@link SensorType}, and which should be
	 * used.
	 */
	public final static SensorType[] typePrecedence = { SensorType.ENCODER };

	/**
	 * Returns array with int values, where each record holds quantity of sensors
	 * of<br>
	 * given {@link SensorType} as specified in
	 * {@link SensorFactory#typePrecedence}.<br>
	 * Values are properly ordered to send message to AVR.
	 * 
	 * @return
	 */
	public static int[] getOrderedQuantity() {
		int[] array = new int[typePrecedence.length];
		for (int j = 0; j < typePrecedence.length; j++) {
			array[j] = countSensors(typePrecedence[j]);
		}
		return array;
	}

	/**
	 * Return a {@link Sensor} array, with properly ordered sensors to read data
	 * from AVR
	 * 
	 * @return Sensor Array, ordered as in {@link SensorFactory#typePrecedence}
	 */
	public static Sensor[] getOrderedArray() {
		int num = 0;
		for (SensorType type : SensorType.values()) {
			num = num + countSensors(type);
		}

		Sensor[] array = new Sensor[num];
		int k = 0;
		for (int j = 0; j < typePrecedence.length; j++) {
			SensorType type = typePrecedence[j];
			if (j != 0) {
				k = k + countSensors(typePrecedence[j - 1]);
			}
			for (int i = 0; i < countSensors(type); i++) {
				array[k + i] = sensorMap.get(type).get(i);
			}
		}

		return array;
	}

	/**
	 * Return the number of sensors of given {@link SensorType}
	 * 
	 * @param classType
	 * @return
	 */
	public static int countSensors(SensorType classType) {
		Map<Integer, Sensor> tempMap = sensorMap.get(classType);
		int num = 0;

		if (tempMap == null) {
			num = 0;
		} else {
			num = tempMap.size();
		}
		return num;
	}

	/**
	 * Creates object basing on provided enum parameter. <br>
	 * Tensometer/Encoder objects supported.
	 * 
	 * @param classType
	 *            Provide type of class to create object (must extend Sensor)
	 * @return newly created object
	 */
	public static Sensor createSensor(SensorType classType) {
		Sensor object = null;
		int sizeMap = 0;

		ConcurrentHashMap<Integer, Sensor> temporaryMap = sensorMap.get(classType);
		if (temporaryMap == null) {
			temporaryMap = new ConcurrentHashMap<>();
			sensorMap.put(classType, temporaryMap);
		} else {
			sizeMap = temporaryMap.size();
		}

		if (classType == SensorType.ENCODER) {
			object = new Encoder();
			object.setType(classType);
			object.setId(sizeMap);
		} else if (classType == SensorType.TENSOMETER) {
			object = new Tensometer();
			object.setType(classType);
			object.setId(sizeMap);
		}

		temporaryMap.put(sizeMap, object);
		return object;
	}

	private SensorFactory() {
	}

	/**
	 * Enum for specifying type of sensor
	 * 
	 * @author piotr
	 *
	 */

	@XmlEnum
	public static enum SensorType {
	//@formatter:off
		@XmlEnumValue("Tensometer")
		TENSOMETER, 
		@XmlEnumValue("Encoder")
		ENCODER;
	//@formatter:on
	}
}
