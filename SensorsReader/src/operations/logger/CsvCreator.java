package operations.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import operations.pendrive.PendriveMount;
import operations.sensors.SensorFactory;
import operations.sensors.SensorFactory.Type;
import operations.sensors.Measurable;
import operations.sensors.TimeStamp;
import operations.sensors.combination.SensorCombinationFactory;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;;

/**
 * .CSV file creator class.<br>
 * Uses Apache Commons CVS library.
 */
public class CsvCreator {
	private static final String filePath = PendriveMount.MOUNT_POINT;
	public static String fileName;
	private CSVPrinter csvPrinter;

	/**
	 * Constructor and file creator. It creates new file and assigns to it actual
	 * <br>
	 * time. If the fileName is not null at the time of creation, it will stay as
	 * <br>
	 * unchanged.Also, prints header as a first line (.name of all
	 * sensors/combinations) <br>
	 */
	public CsvCreator() {
		// Create new file name if already not exist
		if (fileName == null) {
			fileName = "/Pomiar_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
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
		} catch (FileNotFoundException exc) {
			System.out.println(exc + "1");
		} catch (IOException exc) {
			System.out.println(exc + "2");
		}
	}

	private String[] createHeader() {
		ArrayList<String> header = new ArrayList<>();

		header.add(TimeStamp.getInstance().getName());
		for (Type type : SensorFactory.typePrecedence) {
			for (int i = 0; i < SensorFactory.sensorMap.get(type).values().size(); i++) {
				header.add(SensorFactory.sensorMap.get(type).get(i).getName());
			}
		}
		for (int i = 0; i < SensorCombinationFactory.combinationMap.size(); i++) {
			header.add(SensorCombinationFactory.combinationMap.get(i).getName());
		}

		return header.toArray(new String[header.size()]);
	}

	/**
	 * Creates record and writes it to CSV. Flushes every line. Text format is max 4
	 * decimals.
	 * <p>
	 * Uses {@link LinkedHashMap} so iteration order is preserved.
	 * 
	 * @param valuesMap
	 *            Map of sensor-value pairs
	 * 
	 */
	public void saveCsv(LinkedHashMap<Measurable, Double> valuesMap) {
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
			e.printStackTrace();
		}
	}

	/**
	 * Closes open file for writing/reading.
	 */
	public void close() {
		fileName = null;
		try {
			csvPrinter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
