package arduino;

import com.fazecast.jSerialComm.*;

public class Arduino {
	SerialPort port;

	public Arduino() {
		port = getComm();
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
