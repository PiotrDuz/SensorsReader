package pendrive;

public enum ConstPen {
	MOUNT_POINT("/home/piotr/mnt");

	private String value;

	private ConstPen(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
