package main.java.operations.sensors;

public interface Sensorable {

	public void setZeroValue(double number);

	public Double getZeroValueScaled();

	public void setZeroValueScaled(double number);

	public Double getMax();

	public void setMax(Double max);

	public Double getMin();

	public void setMin(Double min);

	public String getName();

	public String getUnit();

	public boolean isCharted();

	public void isChartedSet(boolean flag);

}
