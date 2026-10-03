package data.reader;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import data.Airport;
import data.AirportJson;
import data.DataWrapper;
import data.Flight;
import data.FlightJson;
import exceptions.AirportExists;
import exceptions.AirportNotExists;
import exceptions.FileNotExists;
import exceptions.FormatException;

public class JSONReader extends Reader {

	@Override
	public void process(BufferedReader br)
			throws IOException, FormatException, AirportExists, AirportNotExists {
		Gson gson = new Gson();

		DataWrapper w;
		try {
			w = gson.fromJson(br, DataWrapper.class);
		} catch (JsonSyntaxException e) {
			throw new FormatException("Fajl nije u ispravnom JSON formatu.");
		}
		if (w == null || w.airports == null || w.flights == null)
			throw new FormatException("Fajl ne sadrži očekivane sekcije 'airports' i 'flights'.");

		// airports -> into the airport list
		for (AirportJson a : w.airports) {
			if(Math.abs(a.x)>180)
				throw new FormatException("Aerodrom " + a.code + ": X koordinata (" + a.x
					+ ") mora biti između -180 i 180");
			if(Math.abs(a.y)>90)
				throw new FormatException("Aerodrom " + a.code + ": Y koordinata (" + a.y
					+ ") mora biti između -90 i 90");
			Airport.addAirport(a.x, a.y, a.name, a.code);
		}

		// flights -> into the flight list
		for (FlightJson f : w.flights) {
			Airport start = Airport.findAirport(f.from);
			Airport end   = Airport.findAirport(f.to);
			String[] time = f.departure.split(":");
			if (time.length < 2)
				throw new FormatException("Neispravno vreme poletanja: " + f.departure);
			int h, min;
			try {
				h = Integer.parseInt(time[0].trim());
				min = Integer.parseInt(time[1].trim());
			} catch (NumberFormatException e) {
				throw new FormatException("Neispravno vreme poletanja: " + f.departure);
			}
			Flight.addFlights(start, end, h, min, f.duration);
		}
	}

	public static void main(String[] args) {
		JSONReader reader = new JSONReader();
		try {
			reader.read("files/data.json");
		} catch (FileNotExists | IOException | FormatException | AirportExists | AirportNotExists e) {
			System.out.println(e.getMessage());
		}
	}

}
