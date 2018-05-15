package arduino;

import com.fazecast.jSerialComm.*;
import java.io.IOException;

public class Arduino {
	SerialPort port;

	public Arduino() {
		port = getComm();
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1, 1);
		port.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
	}

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
		for (int i = 0; i < limit; i++) {
			result = result | ((0xFF & array[i]) << 8 * i);
		}
		return result;
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
		port.writeBytes(buffer, 1);
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
		while (port.bytesAvailable() < quantity && i < 1500) {
			try {
				Thread.sleep(1);
				i++;
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		}
		if (i > 1499) {
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
			if (port.getDescriptivePortName().contains(ConstArdu.DEVICE_ID.getValue())) {
				return port;
			}
		}
		return null;
	}

}
