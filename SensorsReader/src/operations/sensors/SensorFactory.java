package operations.sensors;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SensorFactory {

	public static final ConcurrentHashMap<Type, ConcurrentHashMap<Integer, Sensor>> sensorMap = new ConcurrentHashMap<>();
	/**
	 * Defines what is the order of types, and which should be used
	 */
	public final static Type[] typePrecedence = { Type.ENCODER, Type.TENSOMETER };

	/**
	 * Returns array with int values, where each record holds quantity of sensors of
	 * given {@link Type} as specified in {@link SensorFactory#typePrecedence}.
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
	 * @return
	 */
	public static Sensor[] getOrderedArray() {
		int num = 0;
		for (Type type : Type.values()) {
			num = num + countSensors(type);
		}

		Sensor[] array = new Sensor[num];
		int k = 0;
		for (int j = 0; j < typePrecedence.length; j++) {
			Type type = typePrecedence[j];
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
	 * Return the number of sensors of given Type
	 * 
	 * @param classType
	 * @return
	 */
	public static int countSensors(Type classType) {
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
	 * Creates object basing on provided enum parameter. Tensometer/Encoder objects
	 * supported
	 * 
	 * @param classType
	 *            Provide type of class to create object (must extend Sensor)
	 * @return newly created object
	 */
	public static Sensor createSensor(Type classType) {
		Sensor object = null;
		int sizeMap = 0;

		ConcurrentHashMap<Integer, Sensor> temporaryMap = sensorMap.get(classType);
		if (temporaryMap == null) {
			temporaryMap = new ConcurrentHashMap<>();
			sensorMap.put(classType, temporaryMap);
		} else {
			sizeMap = temporaryMap.size();
		}

		if (classType == Type.ENCODER) {
			object = new Encoder();
			object.setId(sizeMap);
		} else if (classType == Type.TENSOMETER) {
			object = new Tensometer();
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
	public static enum Type {
		TENSOMETER, ENCODER;
	}
}
