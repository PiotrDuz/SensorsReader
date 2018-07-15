package main.java.operations.pendrive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Mounting, unmounting, detecting pendrive.<br>
 * Holds list of devices being present at system bootup,<br>
 * compares it on demand with actual devices, finds difference. <br>
 * Asks system via bash terminal commands.
 * 
 * @author Piotr Duzniak
 *
 */
public class PendriveMount {
	public static final String MOUNT_POINT = System.getProperty("user.home") + "/mnt";

	private List<String> disks = new ArrayList<String>();
	private String deviceAddress = null;

	public PendriveMount() {
		disks = getDiskArray();
	}

	public synchronized String getDeviceAddress() {
		return deviceAddress;
	}

	/**
	 * Unmounts drive. It HAS TO BE UNMOUNTED always, even when is already
	 * unplugged!
	 * 
	 * @return True if no output in console, false if there are errors
	 */
	public synchronized Boolean unmountDrive() {
		String command = "sudo umount " + deviceAddress;
		Boolean status = false;

		BufferedReader output = this.executeCommand(command);
		try {
			if (output.readLine() == null) {
				status = true;
			}
		} catch (IOException exc) {
			System.out.println(exc);
		}

		deviceAddress = null;
		System.out.println("Unmounted");
		return status;
	}

	/**
	 * Mounts drive for provided address. <br>
	 * Mounting point specified by MOUNT_POINT in Const
	 * 
	 * @param address
	 *            Address of device to be mounted
	 * @return If true, mounting successful. False means some errors.
	 */
	public Boolean mountDrive(String address) {
		deviceAddress = address;
		String command = "sudo mount -t vfat " + deviceAddress + " " + MOUNT_POINT + " -o uid=1000,gid=1000,umask=022";
		Boolean status = false;

		BufferedReader output = this.executeCommand(command);
		try {
			if (output.readLine() == null) {
				status = true;
			}
		} catch (IOException exc) {
			System.out.println(exc);
		}
		System.out.println("Mounted");
		return status;
	}

	/**
	 * Detects if a new drive is present, returns its address
	 * 
	 * @return Returns address for new drive, null if nothing found
	 */
	public String detectDrive() {
		String detectedDevice = null;

		ArrayList<String> tempList = getDiskArray();
		tempList.removeAll(disks);

		if (!tempList.isEmpty()) {
			detectedDevice = tempList.get(0);
		}
		return detectedDevice;
	}

	/**
	 * Return List of all Devices located in system
	 * 
	 * @return List of devices
	 */
	public ArrayList<String> getDiskArray() {
		String line;
		BufferedReader reader;
		ArrayList<String> DiskList = new ArrayList<String>();

		reader = executeCommand("sudo fdisk -l");
		try {
			do {
				line = reader.readLine();
				if (line == null || line.length() < 7) {
					continue;
				}

				if (line.substring(0, 6).equals("Device")) {
					line = reader.readLine();
					do {
						DiskList.add(line.split(" ")[0]);
						line = reader.readLine();
					} while (line != null && !line.equals(""));
				}
			} while (line != null);
		} catch (IOException exc) {
			System.out.println(exc);
		}
		return DiskList;
	}

	/**
	 * Executes bash command
	 * 
	 * @param command
	 *            Bash command to be executed
	 * @return BufferedReader containing terminal's answer for a command
	 */
	private BufferedReader executeCommand(String command) {
		File workingFolder = new File("/home");
		BufferedReader readerOutput = null;

		try {
			Process bash = new ProcessBuilder("bash").directory(workingFolder).start();

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bash.getOutputStream());
			outputStreamWriter.write(command);
			outputStreamWriter.close();

			bash.waitFor();
			readerOutput = new BufferedReader(new InputStreamReader(bash.getInputStream()));
		} catch (Exception e) {
			System.out.println(e);
		}
		return readerOutput;
	}
}
