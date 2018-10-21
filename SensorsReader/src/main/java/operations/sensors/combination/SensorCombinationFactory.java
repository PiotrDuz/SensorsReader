package operations.sensors.combination;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import operations.sensors.Measurable;
import operations.sensors.SensorFactory;
import operations.sensors.SensorFactory.SensorType;

/**
 * Class for holding {@link SensorCombination} objects, and managing them.
 * 
 * @author Piotr Duzniak
 *
 */
public class SensorCombinationFactory {
	/**
	 * List holding all SensorCombination objects
	 */
	public static ConcurrentHashMap<Integer, SensorCombination> combinationMap = new ConcurrentHashMap<>();
	private static ArrayList<CombinationData> combinationDataList = new ArrayList<>();

	public static Integer size() {
		return combinationMap.size();
	}

	/**
	 * Get {@link CombinationData} object holding all parameters.
	 * 
	 * @param data
	 */
	public static void addToDataList(CombinationData data) {
		combinationDataList.add(data);
	}

	/**
	 * Retrieve data from {@link CombinationData} and save to object with proper ID.
	 */
	public static void setData() {
		for (CombinationData data : combinationDataList) {
			combinationMap.get(data.getiD()).setCombinationData(data);
		}
		combinationDataList.clear();
	}

	public static void initialize() {
		// initialize objects with overridden, customized methods
		defineCombinations();

		// Initialize internal data, retrieved from XML
		setData();
	}

	/**
	 * Create custom combinations of sensors, and use it as new "measurement".
	 * <p>
	 * Each object has an ID, that is assigned automatically in constructor.
	 * <p>
	 * Order of defined objects matter! Do not change the order, to not mix objects
	 * with saved values in xml
	 */
	private static void defineCombinations() {
		//@formatter:off
		//!!-!! initialize 1 object
		
		/*
		 combinationMap.put(size(), new SensorCombination(size()) {
		
		 @Override
		 public double customMeasurementMethod(LinkedHashMap<Measurable, Double> map)
		 {
			 return variables.get("var1") / map.get(sensors.get(1)) *
					 map.get(sensors.get(0)) * Math.pow(variables.get("var2"), 2);
		 }
		
		 @Override
		 public String equationText() {
		 return "var1 " + " * " + sensors.get(0).getName() + " / " +
		 sensors.get(1).getName() + " *" + "var2 ^2";
		 }
		 
		 @Override
		 public Measurable getXAxis() {
			 return SensorFactory.sensorMap.get(SensorType.ENCODER).get(1);
		 }
		 
		 }// variables setting section:
		 .addVariable("var1") //
		 .addVariable("var2") //
		 .addSensor(SensorFactory.sensorMap.get(SensorType.ENCODER).get(0)) // sensor0
		 .addSensor(SensorFactory.sensorMap.get(SensorType.TENSOMETER).get(0)) // sensor1		
		 );
		
		 // !!-!! Initialize 2 object
		
		 combinationMap.put(size(), new SensorCombination(size()) {
		
		 @Override
		 public double customMeasurementMethod(LinkedHashMap<Measurable, Double> map)
		 {
		 return variables.get("naprezenie") / map.get(sensors.get(1)) *
		 map.get(sensors.get(0))
		 * Math.pow(variables.get("nacisk"), 2);
		 }
		
		 @Override
		 public String equationText() {
		 return "naprezenie " + " * " + sensors.get(0).getName() + " / " +
		 sensors.get(1).getName() + " * " + "nacisk ^2";
		 }
		 }// variables setting section:
		 .addVariable("naprezenie") //
		 .addVariable("nacisk") //
		 .addSensor(SensorFactory.sensorMap.get(SensorType.ENCODER).get(1)) // sensor0= encoder1
		 .addSensor(SensorFactory.sensorMap.get(SensorType.TENSOMETER).get(0)) // sensor1 = tenso0
		
		 );
		 */
		 
		 //@formatter:on
	}
}
