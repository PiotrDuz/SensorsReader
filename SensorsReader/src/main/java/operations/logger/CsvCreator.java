package operations.logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import application.ProgramException;
import operations.pendrive.PendriveMount;
import operations.sensors.Measurable;
import operations.sensors.Sensor;
import operations.sensors.SensorFactory;
import operations.sensors.SensorFactory.SensorType;
import operations.sensors.TimeStamp;
import operations.sensors.combination.SensorCombination;
import operations.sensors.combination.SensorCombinationFactory;;

/**
 * .CSV file creator class.<br>
 * Uses Apache Commons CVS library.
 */
public class CsvCreator implements AutoCloseable {
	private static final String filePath = PendriveMount.MOUNT_POINT + "/";
	private CSVPrinter csvPrinter;
	private double period = 0;
	private double prevTime = 0;

	/**
	 * Constructor and file creator. It creates new file and assigns to it actual
	 * <br>
	 * time. If the fileName is not null at the time of creation, it will stay as
	 * <br>
	 * unchanged.Also, prints header as a first line (.name of all
	 * sensors/combinations) <br>
	 * Specified frequency of saved data shall be in ms.
	 * 
	 * @throws ProgramException
	 */
	public CsvCreator(String fileName, double period) throws ProgramException {
		// frequency in ms of saving
		this.period = period;
		// Create new file name if already not exist
		if (fileName == null) {
			fileName = "Pomiar_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		}

		// create file and CSVPrinter
		// write header
		Path totalPath = Paths.get(filePath + fileName + ".csv");
		try {
			Files.createFile(totalPath);
			BufferedWriter writer = Files.newBufferedWriter(totalPath, StandardCharsets.UTF_8);
			csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
			csvPrinter.printRecord((Object[]) createHeader());
			csvPrinter.flush();
		} catch (IOException exc) {
			throw new ProgramException(exc);
		}
	}

	private String[] createHeader() {
		ArrayList<String> header = new ArrayList<>();

		header.add(TimeStamp.getInstance().getName());
		for (SensorType type : SensorFactory.typePrecedence) {
			if (SensorFactory.sensorMap.get(type) == null) {
				continue;
			}
			for (int i = 0; i < SensorFactory.sensorMap.get(type).values().size(); i++) {
				Sensor sens = SensorFactory.sensorMap.get(type).get(i);
				header.add(sens.getName() + " [" + sens.getUnit() + "]");
			}
		}
		for (int i = 0; i < SensorCombinationFactory.combinationMap.size(); i++) {
			SensorCombination comb = SensorCombinationFactory.combinationMap.get(i);
			header.add(comb.getName() + " [" + comb.getUnit() + "]");
		}
		// add information about used max scale
		header.add("Zakres max: " + SensorCombinationFactory.combinationMap.get(0).getChosenVar());

		return header.toArray(new String[header.size()]);
	}

	/**
	 * Creates record and writes it to CSV. Flushes every line. Text format is max 4
	 * decimals.
	 * <p>
	 * Uses {@link LinkedHashMap} so iteration order is preserved.
	 * 
	 * @param valuesMap Map of sensor-value pairs
	 * @throws ProgramException
	 * 
	 */
	public void saveCsv(LinkedHashMap<Measurable, Double> valuesMap) throws ProgramException {
		int i = 0;
		String[] row = new String[valuesMap.size()];
		for (Measurable sensor : valuesMap.keySet()) {
			row[i] = String.format("%.4f", valuesMap.get(sensor));
			i++;
		}

		try {
			csvPrinter.printRecord((Object[]) row);
			csvPrinter.flush();
		} catch (IOException e) {
			throw new ProgramException(e);
		}
	}

	/**
	 * Closes open file for writing/reading.
	 */
	public void close() {
		try {
			csvPrinter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean readyToSave(double time) {
		boolean flag = false;
		// 0.9 factor to not skip any frame. Better to catch more.
		if ((time - this.prevTime) >= 0.9 * this.period) {
			this.prevTime = time;
			flag = true;
		}
		return flag;
	}

}
