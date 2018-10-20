package operations.arduino;

/**
 * Holds byte command that are send to Arduino, <br>
 * under human-readable enum variable.
 * 
 * @author Piotr Duzniak
 *
 */
public enum Command {
	SEND_SENSORS_QUANTITY(2), START_MEASURING(0), STOP_MEASURING(1), GET_DATE(3), SET_DATE(4);

	private int number;

	private Command(int i) {
		number = i;
	}

	public int get() {
		return number;
	}
}
