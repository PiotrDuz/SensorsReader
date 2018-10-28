package application;

public class ProgramException extends Exception {

	static final long serialVersionUID = 1L;

	public ProgramException() {

	}

	public ProgramException(String message) {
		super(message);

	}

	public ProgramException(Throwable cause) {
		super(cause);

	}

	public ProgramException(String message, Throwable cause) {
		super(message, cause);

	}

}
