package operations.arduino;

import java.io.IOException;

import org.junit.Test;

import com.fazecast.jSerialComm.SerialPort;

import application.ProgramException;

public class ArduinoTest {

	public void testByteToInt() {
		byte array[] = new byte[8];
		array[0] = (byte) 0xFF;
		array[1] = 0;
		array[2] = 0;
		array[3] = 0;
		array[4] = 0;
		array[5] = 0;
		array[6] = (byte) 1;
		array[7] = 0;

		int val1 = Arduino.byteToInt(array, 0);
		int val2 = Arduino.byteToInt(array, 4);
		System.out.println(val1 + "   " + val2);
		assert val1 == -16777216;
		assert val2 == 256;

	}

	@Test
	public void getComDevices() {
		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort port : ports) {
			System.out.println(port.getDescriptivePortName());
		}
	}

	@Test
	public void testDate() throws ProgramException {

		byte[] array = null;
		try (Arduino serial = Arduino.getInstance()) {
			serial.open();

			serial.write(Command.GET_DATE.get(), 1);

			array = serial.read(6);

		} catch (IOException | ProgramException e) {
			throw new ProgramException(e);
		}
		for (byte i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}
		int year = array[0] + 2000;
		int month = array[1];
		int day = array[2];
		int hour = array[3];
		int minute = array[4];
		int second = array[5];

		String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
		String time = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":"
				+ String.format("%02d", second);
		System.out.println(date + " " + time);

	}

	public void setDate() throws ProgramException {
		try (Arduino serial = Arduino.getInstance()) {
			serial.open();
			serial.write(Command.SET_DATE.get(), 1);

			serial.write(12, 1);
			serial.write(12, 1);
			serial.write(12, 1);
			serial.write(1, 1);
			serial.write(1, 1);
			serial.write(1, 1);

			serial.close();
		}

	}

	public void previousState() throws ProgramException {
		// save state to sensors
		try (Arduino serial = Arduino.getInstance()) {
			serial.open();
			serial.write(Command.SEND_SENSORS_QUANTITY.get(), 1);
			serial.write(2, 1);
			serial.write(Command.START_MEASURING.get(), 1);
			Arduino.delay(1000);
			serial.write(Command.STOP_MEASURING.get(), 1);

			serial.clean();

			serial.write(Command.GET_STATE.get(), 1);

			// each number is sent as arduino's long (4 bytes)
			byte[] array = null;
			try {
				array = serial.read(2 * 4);
			} catch (IOException exc) {
				throw new ProgramException(exc);
			}

			// Set raw lastState to sensor
			for (int i = 0; i < 2; i++) {
				System.out.println(Arduino.byteToInt(array, i * 4));
			}
		}
	}
}
