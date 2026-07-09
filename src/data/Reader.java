package data;

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

	public abstract void processAirports(String line);

	public abstract void processFlights(String line);

	public abstract void process(BufferedReader br)
			throws IOException, FormatException, AirportExists, AirportNotExists;


	// ne obrađuje greške ovde - samo ih propušta nagore, da ih GUI prikaže korisniku
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
