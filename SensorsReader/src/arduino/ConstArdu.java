package arduino;

public enum ConstArdu {
	DEVICE_ID("ch341");

	private String value;

	private ConstArdu(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
