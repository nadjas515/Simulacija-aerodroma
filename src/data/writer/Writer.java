package data.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Writer {

	public abstract void process(BufferedWriter bw) throws IOException;


	// simetrično sa Reader.read - otvori fajl, a pisanje prepusti konkretnom writeru
	public void write(String filename) throws IOException {
		Path path = Paths.get(filename);
		try (BufferedWriter bw = Files.newBufferedWriter(path)) {
			process(bw);
		}
	}

}
