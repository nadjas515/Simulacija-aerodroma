package data.writer;

import java.io.BufferedWriter;
import java.io.IOException;

import data.Airport;
import data.Flight;
import data.reader.CSVReader;

public class CSVWriter extends Writer {

	@Override
	public void process(BufferedWriter bw) throws IOException {
		bw.write("# AIRPORTS");
		bw.newLine();
		bw.write("CODE,NAME,X,Y");
		bw.newLine();
		for (Airport a : Airport.getAirports()) {
			bw.write(a.getCode() + "," + a.getName() + "," + a.getX() + "," + a.getY());
			bw.newLine();
		}

		bw.newLine();   // prazan red između sekcija

		bw.write("# FLIGHTS");
		bw.newLine();
		bw.write("FROM,TO,DEPARTURE,DURATION");
		bw.newLine();
		for (Flight f : Flight.getFlights()) {
			bw.write(f.getStart().getCode() + "," + f.getEnd().getCode() + ","
				+ String.format("%02d:%02d", f.getH(), f.getMin()) + "," + f.getDuration());
			bw.newLine();
		}
	}

	public static void main(String[] args) {
		try {
			new CSVReader().read("files/data.csv");   // prvo učitaj podatke
			new CSVWriter().write("files/out.csv");   // pa ih snimi nazad
			System.out.println("Snimljeno u files/out.csv");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
