package operations.arduino;

import com.fazecast.jSerialComm.*;
import java.io.IOException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "Arduino")
public class Arduino {

	private String DEVICE_ID;
	@XmlTransient
	public SerialPort port;
	@XmlTransient
	private static Arduino arduino;

	/**
	 * Return Instance of a singleton. There is only one Arduino object for program,
	 * only one COM port connected at all times
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

	public static void setInstance(Arduino instance) {
		if (arduino == null) {
			arduino = instance;
			arduino.initialize();
		}
	}

	private Arduino() {
	}

	/**
	 * Used to initialize SerialPort settings within class Sets timeout
	 * (read_blocking), speed (115200 baud) 8bit, 1 stop, noparity
	 */
	private void initialize() {
		port = getComm();
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1, 1);
		port.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
	}

	/**
	 * Opens USB COM port and waits 4seconds to initialize Port has to be closed
	 * after using !
	 */
	public void open() {
		port.openPort();
		try {
			Thread.sleep(4000);
		} catch (InterruptedException exc) {
			System.out.println(exc);
		}
	}

	public void close() {
		port.closePort();
	}

	/**
	 * Takes byte array and convert 4 consecutive bytes to int Starts point provided
	 * by second param
	 *
	 * @param array
	 *            Byte array
	 * @param start
	 *            Start point
	 * @return int number
	 */
	public static int byteToInt(byte[] array, int start) {
		int result = 0;
		int limit;

		if (array.length < start + 4) {
			limit = array.length;
		} else {
			limit = start + 4;
		}
		for (int i = start; i < limit; i++) {
			result = result | ((0xFF & array[i]) << 8 * i);
		}
		return result;
	}

	/**
	 * Delay calling thread by amount of ms
	 * 
	 * @param i
	 */
	public void delay(int i) {
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
	 * Write given number of bytes from int input
	 * 
	 * @param c
	 *            int input
	 * @param quantity
	 *            how many bytes from input write (starting from least significant)
	 */
	public void write(int c, int quantity) {

		byte[] buffer = new byte[quantity];

		for (int i = 0; i < quantity; i++) {
			buffer[i] = (byte) (c >> 8 * i);
		}
		port.writeBytes(buffer, quantity);
	}

	/**
	 * Read specified number of bytes from port Method will wait for bytes arrival
	 * 3ms
	 * 
	 * @param quantity
	 *            How many bytes to read
	 * @return Bytes array or null if port doesn't have specified number of bytes
	 */
	public byte[] read(int quantity) throws IOException {
		byte buffer[] = new byte[quantity];

		int i = 0;
		while (port.bytesAvailable() < quantity && i < 1000) {
			try {
				Thread.sleep(1);
				i++;
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		}
		if (i > 999) {
			throw new IOException("No bytes in port");
		}

		port.readBytes(buffer, quantity);
		return buffer;
	}

	/**
	 * Get SerialPort of connected Arduino
	 * 
	 * @return SerialPort object or null if not found
	 */
	public SerialPort getComm() {
		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort port : ports) {
			if (port.getDescriptivePortName().contains(DEVICE_ID)) {
				return port;
			}
		}
		return null;
	}

	/**
	 * Set deviceId as reported in linux system
	 *
	 * @param Id
	 */
	public void setDeviceId(String Id) {
		DEVICE_ID = Id;
	}

	@XmlElement(name = "deviceId")
	public String getDeviceId() {
		return DEVICE_ID;
	}

}
