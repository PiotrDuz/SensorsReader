package operations.arduino;

import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.fazecast.jSerialComm.SerialPort;

import application.ProgramException;

/**
 * Class that interacts with Arduino microcontroller. <br>
 * Uses JSerialComm library to handle serial ports (linux only). <br>
 * Class is a singleton.
 * 
 * @author Piotr Duzniak
 */
@XmlRootElement(name = "Arduino")
public class Arduino implements AutoCloseable {
	@XmlTransient
	final static Logger logger = Logger.getLogger(Arduino.class);

	private String DEVICE_ID = "Serial"; // ch341
	@XmlTransient
	protected SerialPort port;
	@XmlTransient
	private static Arduino arduino;

	/**
	 * Return Instance of a singleton.
	 * <p>
	 * There is only one Arduino object for program, only one COM port connected at
	 * all times
	 * 
	 * @return Arduino object
	 */
	public static Arduino getInstance() {
		if (arduino == null) {
			arduino = new Arduino();
			arduino.initialize();
		}
		return arduino;
	}

	/**
	 * Method that is used with XML data retrieval
	 * 
	 * @param instance
	 */
	public static void setInstance(Arduino instance) {
		if (arduino == null) {
			arduino = instance;
			arduino.initialize();
		}
	}

	private Arduino() {
	}

	/**
	 * Used to initialize SerialPort settings within class.
	 * <p>
	 * Sets timeout (read_blocking), speed (115200 baud) 8bit, 1 stop, noparity
	 */
	private void initialize() {
		port = getComm(DEVICE_ID);
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1, 1);
		port.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
	}

	/**
	 * Opens USB COM port and waits 3 seconds to initialize.
	 * <p>
	 * Port has to be closed with {@link Arduino#close()} after using !
	 * 
	 * @throws ProgramException
	 */
	public void open() throws ProgramException {
		if (!port.openPort()) {
			throw new ProgramException("Serial port could not be opened");
		}
		delay(3000);
	}

	/**
	 * Close serial port.
	 * 
	 * @throws ProgramException
	 */
	public void close() throws ProgramException {
		if (!port.closePort()) {
			throw new ProgramException("Port has not been closed properly");
		}
	}

	/**
	 * Takes byte array and converts 4 consecutive bytes to int.
	 * <p>
	 * Start point defines from which index start taking 4 bytes. <br>
	 * Bytes should be oriented MSB first.
	 *
	 * @param array Byte array
	 * @param start Array's index from which to start conversion
	 * @return int Number
	 */
	public static int byteToInt(byte[] array, int start) {
		int result = 0;
		int limit;

		if (array.length < start + 4) {
			limit = array.length;
		} else {
			limit = start + 4;
		}

		// MSB first
		int j = 4;
		for (int i = start; i < limit; i++) {
			// byte value as int
			int tempInt = (0xFF & array[i]);
			int shift = 8 * --j;
			result = result | (tempInt << shift);
		}

		return result;
	}

	/**
	 * Delay calling thread by amount of ms
	 * 
	 * @param i
	 */
	public static void delay(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException exc) {
			System.out.println(exc);
		}
	}

	/**
	 * Clears serial port from remaining bytes
	 * 
	 * @return returns amount of bytes that it cleared
	 */
	public int clean() {
		int num = port.bytesAvailable();
		byte[] array = new byte[1];
		while (port.bytesAvailable() > 0) {
			port.readBytes(array, 1);
		}
		return num;
	}

	/**
	 * Take int number and write its bytes to serial port.
	 * <p>
	 * Number of bytes written can be controlled.
	 * 
	 * @param c        int number
	 * @param quantity number of bytes to write (starting from least significant)
	 */
	public void write(int c, int quantity) {

		byte[] buffer = new byte[quantity];

		for (int i = 0; i < quantity; i++) {
			buffer[i] = (byte) (c >> 8 * i);
		}
		port.writeBytes(buffer, quantity);
	}

	/**
	 * Read specified number of bytes from port.
	 * <p>
	 * Method will wait for bytes arrival 1s.
	 * 
	 * @param quantity How many bytes to read
	 * @return Bytes array or throws IOException if port doesn't have specified
	 *         number of bytes
	 */
	public byte[] read(int quantity) throws IOException {
		byte buffer[] = new byte[quantity];

		int i = 0;
		// wait for whole packet to arrive. Throw exception on timeout
		while (port.bytesAvailable() < quantity && i < 1001) {

			// wait 2ms
			Arduino.delay(2);

			if (i > 999) {
				throw new IOException("No bytes in port");
			}
			i++;
		}

		port.readBytes(buffer, quantity);
		return buffer;
	}

	/**
	 * Get {@link SerialPort} handle of connected Arduino.
	 * <p>
	 * Searches for DEVICE_ID String in name of connected devices.
	 * 
	 * @return SerialPort object or null if not found
	 */
	public SerialPort getComm(String devId) {
		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort port : ports) {
			logger.info("Ports available: " + port.getDescriptivePortName());
			if (port.getDescriptivePortName().contains(devId)) {
				logger.info("Chosen port: " + port.getDescriptivePortName());
				return port;
			}
		}
		return null;
	}

	/**
	 * Set DEVICE_ID of connected microcontroller.
	 * <p>
	 * DEVICE_ID has to be part of device's name, as reported in linux system.
	 *
	 * @param Id
	 */
	public void setDeviceId(String Id) {
		DEVICE_ID = Id;
	}

	public String getDeviceId() {
		return DEVICE_ID;
	}

}
