package data.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import data.Airport;
import data.AirportJson;
import data.DataWrapper;
import data.Flight;
import data.FlightJson;
import data.reader.JSONReader;

public class JSONWriter extends Writer {

	@Override
	public void process(BufferedWriter bw) throws IOException {
		DataWrapper w = new DataWrapper();

		// domen -> DTO (iste pomoćne klase koje koristi JSONReader)
		List<Airport> airports = Airport.getAirports();
		w.airports = new AirportJson[airports.size()];
		for (int i = 0; i < airports.size(); i++) {
			Airport a = airports.get(i);
			AirportJson aj = new AirportJson();
			aj.code = a.getCode();
			aj.name = a.getName();
			aj.x = (int) a.getX();
			aj.y = (int) a.getY();
			w.airports[i] = aj;
		}

		List<Flight> flights = Flight.getFlights();
		w.flights = new FlightJson[flights.size()];
		for (int i = 0; i < flights.size(); i++) {
			Flight f = flights.get(i);
			FlightJson fj = new FlightJson();
			fj.from = f.getStart().getCode();
			fj.to = f.getEnd().getCode();
			fj.departure = String.format("%02d:%02d", f.getPlannedH(), f.getPlannedMin());
			fj.duration = f.getDuration();
			w.flights[i] = fj;
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		bw.write(gson.toJson(w));
	}

	public static void main(String[] args) {
		try {
			new JSONReader().read("files/data.json");   // prvo učitaj podatke
			new JSONWriter().write("files/out.json");   // pa ih snimi nazad
			System.out.println("Snimljeno u files/out.json");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
