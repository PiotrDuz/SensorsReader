package operations.sensors.combination;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import operations.sensors.Measurable;
import operations.sensors.SensorFactory;
import operations.sensors.SensorFactory.SensorType;
import operations.sensors.TimeStamp;

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
		//---------------------- initialize 1 object
	
		 combinationMap.put(size(), new SensorCombination(size()) {
		
		 @Override
		 public double customMeasurementMethod(LinkedHashMap<Measurable, Double> map)
		 {	
			 //little performance hit, can improve it later
			 Measurable sensor = SensorFactory.sensorMap.get(SensorType.ENCODER).get(0);
			 
			 return		 map.get(sensor) * variables.get(choosenVar).getValue();
		 }
		
		 @Override
		 public String equationText() {
			 Measurable sensor = SensorFactory.sensorMap.get(SensorType.ENCODER).get(0);
			 return sensor.getName() + " *" +" skala";
		 }
		 
		 @Override
		 public Measurable getXAxis() {
			 return TimeStamp.getInstance();
		 }
		 
		 }// variables setting section:
		 .addVariable(new Variable(" 10kN",1.0,true)) 
		 .addVariable(new Variable(" 20kN",1.0,true)) 
		 .addVariable(new Variable(" 40kN",1.0,true))
		 .addVariable(new Variable("100kN",1.0,true))
		 .addVariable(new Variable("150kN",1.0,true))
		 );
 
		//------------------------- initialize 2 object
			
		 combinationMap.put(size(), new SensorCombination(size()) {
		
		 @Override
		 public double customMeasurementMethod(LinkedHashMap<Measurable, Double> map)
		 {	
			 //combination defined above is calculating force
			 Measurable combForce = SensorCombinationFactory.combinationMap.get(0);
			 
			 return map.get(combForce);
		 }
		
		 @Override
		 public String equationText() {
			 
			 return "F( przemieszczenie ) = sila";
		 }
		 
		 @Override
		 public Measurable getXAxis() {
			 //Sensor with number 2 (1 in array) is for measuring displacement
			 Measurable sensorDistance = SensorFactory.sensorMap.get(SensorType.ENCODER).get(1);
			 return sensorDistance;
		 }
		 
		 }// variables setting section:
		 	// no  variables
		 );
		 //@formatter:on
	}
}
