package data;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.Gson;

import exceptions.AirportNotExists;
import exceptions.FileNotExists;
import exceptions.FormatException;

//odgovara jednom aerodromu u JSON-u: {"code":..,"name":..,"x":..,"y":..}
class AirportJson {
 String code;
 String name;
 int x;
 int y;
}

//odgovara jednom letu: {"from":..,"to":..,"departure":"08:30","duration":..}
class FlightJson {
 String from;
 String to;
 String departure;   // ostaje String jer je "08:30"
 int duration;
}

//omotač oko cele datoteke
class DataWrapper {
 AirportJson[] airports;
 FlightJson[] flights;
}

public class JSONReader extends Reader {

	@Override
	public void processAirports(String line) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processFlights(String line) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(BufferedReader br) throws IOException, FormatException {
	    Gson gson = new Gson();
	    DataWrapper w = gson.fromJson(br, DataWrapper.class);   // umesto Student.class

	    // aerodromi -> u tvoju listu
	    for (AirportJson a : w.airports) {
	        if(Math.abs(a.x)>180)
	            System.out.println("Aerodrom " + a.code + ": X koordinata (" + a.x
	                + ") mora biti između -180 i 180. Aerodrom je preskočen.");
	        else if(Math.abs(a.y)>90)
	            System.out.println("Aerodrom " + a.code + ": Y koordinata (" + a.y
	                + ") mora biti između -90 i 90. Aerodrom je preskočen.");
	        else
	            Airport.addAirport(a.x, a.y, a.name, a.code);
	    }

	    // letovi -> u tvoju listu
	    for (FlightJson f : w.flights) {
	        try {
	            Airport start = Airport.findAirport(f.from);
	            Airport end   = Airport.findAirport(f.to);
	            String[] time = f.departure.split(":");
	            Flight.addFlights(start, end,
	                Integer.parseInt(time[0]), Integer.parseInt(time[1]), f.duration);
	        } catch (AirportNotExists e) {
	            System.out.println(e.getMessage());
	        }
	    }

	    Airport.printAirports();
	    Flight.printFlights();
	}

	public static void main(String[] args) {
		JSONReader csv = new JSONReader();
		try {
			csv.read("files/data.json");
		} catch (FileNotExists e) {
			System.out.println(e.getMessage());
		}
	}

}
