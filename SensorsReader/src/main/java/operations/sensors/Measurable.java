package main.java.operations.sensors;

/**
 * Common methods to plot measurement objects on GUI. <br>
 * Also used as common base to hold objects in measurement map, <br>
 * within {@link ReadingsLogger#run()}
 * 
 * @author piotr
 *
 */
public interface Measurable {
	public String getName();

	public String getUnit();

}
