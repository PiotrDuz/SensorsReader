package operations.arduino;

/**
 * Holds byte command that are send to Arduino, <br>
 * under human-readable enum variable.
 * 
 * @author Piotr Duzniak
 *
 */
public enum Command {
	SEND_SENSORS_QUANTITY(2), START_MEASURING(32), STOP_MEASURING(33), GET_DATE(3), SET_DATE(4), GET_STATE(5);

	private int number;

	private Command(int i) {
		number = i;
	}

	public int get() {
		return number;
	}
}
