package operations.pendrive;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import operations.sensors.Sensorable;
import operations.sensors.TimeStamp;
import userInterface.main.MainWindow;
import userInterface.main.SensorPaneFactory;
import userInterface.main.SensorPaneFactory.PaneValues;

/**
 * Class that runs as a seperate thread.<br>
 * Every few seconds it checks if a new storage device has appeared. <br>
 * When it detects it, it is mounted. For details, see {@link PendriveMount}
 * 
 * @author Piotr Duzniak
 *
 */
public class PendriveKeeper implements Runnable {
	private static PendriveKeeper pendriveKeeper;

	private final PendriveMount pendrive = new PendriveMount();
	private volatile boolean stop = false;
	private volatile boolean toUnmount = false;

	public static PendriveKeeper getInstance() {
		if (pendriveKeeper == null) {
			pendriveKeeper = new PendriveKeeper();
		}
		return pendriveKeeper;
	}

	private PendriveKeeper() {

	}

	/**
	 * Main method of a thread. Runs for all program's duration.<br>
	 * It checks if new device is present, and nothing is mounted -> action
	 * mount<br>
	 * Or if new device is NOT present, but there is still device's address holden
	 * -> action unmount
	 */
	public void run() {
		while (stop == false) {
			// when new drive detected, check if it has not been already mounted
			if (pendrive.getDeviceAddress() == null && pendrive.detectDrive() != null) {
				mount(pendrive.detectDrive());
			} else if (pendrive.detectDrive() == null && pendrive.getDeviceAddress() != null) {
				unmount();
			} else if (toUnmount) {
				unmount();
				toUnmount = false;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
		}
	}

	public synchronized Boolean isMounted() {
		return pendrive.getDeviceAddress() != null;
	}

	private void mount(String address) {
		if (pendrive.mountDrive(address)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					MainWindow.controller.changePendriveMountStatus(true);
				}
			});
		}
	}

	private synchronized void unmount() {
		if (pendrive.unmountDrive()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					MainWindow.controller.changePendriveMountStatus(false);
				}
			});
		}
		// wait 10 sec to give user chance to remove pendrive
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop execution of a thread, no checking for pendrive
	 */
	public synchronized void stop() {
		stop = true;
	}

	public void orderUnmount() {
		toUnmount = true;
	}
}
