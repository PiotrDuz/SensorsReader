package operations.pendrive;

public class PendriveKeeper implements Runnable {
	private final PendriveMount pendrive = new PendriveMount();
	private Boolean stop = false;

	public void run() {
		while (stop == false) {
			// when new drive detected, check if it has not been already mounted
			if (pendrive.getDeviceAddress() == null && pendrive.detectDrive() != null) {
				pendrive.mountDrive(pendrive.detectDrive());
			} else if (pendrive.detectDrive() == null && pendrive.getDeviceAddress() != null) {
				pendrive.unmountDrive();
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

	public synchronized Boolean unmount() {

		return pendrive.unmountDrive();
	}

	/**
	 * Stop execution of a thread, no checking for pendrive
	 */
	public synchronized void stop() {
		stop = true;
	}
}
