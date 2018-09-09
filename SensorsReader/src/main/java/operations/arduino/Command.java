package main.java.operations.arduino;

/**
 * Holds byte command that are send to Arduino, <br>
 * under human-readable enum variable.
 * 
 * @author Piotr Duzniak
 *
 */
public enum Command {
	SEND_SENSORS_QUANTITY(2), START_MEASURING(48), STOP_MEASURING(0), GET_DATE(38);

	private int number;

	private Command(int i) {
		number = i;
	}

	public int get() {
		return number;
	}
}
