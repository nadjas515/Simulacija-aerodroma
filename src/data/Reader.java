package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import exceptions.FileNotExists;
import exceptions.FormatException;

public abstract class Reader {
	
	public abstract void processAirports(String line);
	
	public abstract void processFlights(String line);
	
	public abstract void process(BufferedReader br) throws IOException, FormatException ;
	
	
	protected void read(String filename) throws FileNotExists{
		Path path = Paths.get(filename);
		if(!Files.exists(path)) {
			throw new FileNotExists(filename);
		}
		try (BufferedReader br = Files.newBufferedReader(path)) {
			process(br);
		} catch (IOException e) {
			System.out.println("Ups!");
		} catch (FormatException e) {
			System.out.println(e.getMessage());
		}
	}

}
