package operations.arduino;

public enum Command {
	SEND_SENSORS_QUANTITY(2), START_MEASURING(48), STOP_MEASURING(0);

	private int number;

	private Command(int i) {
		number = i;
	}

	public int get() {
		return number;
	}
}
