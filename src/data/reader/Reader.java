package data.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import exceptions.AirportExists;
import exceptions.AirportNotExists;
import exceptions.FileNotExists;
import exceptions.FormatException;

public abstract class Reader {

	public abstract void process(BufferedReader br)
			throws IOException, FormatException, AirportExists, AirportNotExists;


	// errors are not handled here; they are passed up so the GUI can show them to the user
	public void read(String filename)
			throws FileNotExists, IOException, FormatException, AirportExists, AirportNotExists {
		Path path = Paths.get(filename);
		if(!Files.exists(path)) {
			throw new FileNotExists(filename);
		}
		try (BufferedReader br = Files.newBufferedReader(path)) {
			process(br);
		}
	}

}
