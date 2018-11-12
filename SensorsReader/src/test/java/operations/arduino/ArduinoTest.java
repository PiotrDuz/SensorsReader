package operations.arduino;

import org.junit.Test;

import com.fazecast.jSerialComm.SerialPort;

public class ArduinoTest {

	@Test
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
}
