package data.reader;

import java.io.BufferedReader;
import java.io.IOException;

import data.Airport;
import data.Flight;
import exceptions.AirportExists;
import exceptions.AirportNotExists;
import exceptions.FileNotExists;
import exceptions.FormatException;

public class CSVReader extends Reader {

	@Override
	public void process(BufferedReader br)
			throws IOException, FormatException, AirportExists, AirportNotExists {
		String line = br.readLine();
		if(!line.equals("# AIRPORTS"))
			throw new FormatException("Nedostaje linija # AIRPORTS");
		br.readLine();   // preskoči zaglavlje kolona (CODE,NAME,X,Y)
		line = br.readLine();
		while(line!=null && !line.isEmpty()) {
			String[] parts = line.split(",");
			if (parts.length < 4)
				throw new FormatException("Red nema očekivane 4 kolone (CODE,NAME,X,Y):\n" + line);
			int x, y;
			try {
				x = Integer.parseInt(parts[2].trim());
				y = Integer.parseInt(parts[3].trim());
			} catch (NumberFormatException e) {
				throw new FormatException("Koordinate nisu brojevi u redu:\n" + line);
			}
			if(Math.abs(x)>180)
				throw new FormatException("Aerodrom " + parts[0] + ": X koordinata (" + x
					+ ") mora biti između -180 i 180");
			if(Math.abs(y)>90)
				throw new FormatException("Aerodrom " + parts[0] + ": Y koordinata (" + y
					+ ") mora biti između -90 i 90");
			Airport.addAirport(x, y, parts[1], parts[0]);
			line = br.readLine();
		}

		line = br.readLine();
		if(line==null || !line.equals("# FLIGHTS"))
			throw new FormatException("Nedostaje linija # FLIGHTS");
		br.readLine();   // preskoči zaglavlje kolona (FROM,TO,DEPARTURE,DURATION)
		line = br.readLine();
		while(line!=null && !line.isEmpty()) {
			String[] parts = line.split(",");
			if (parts.length < 4)
				throw new FormatException("Red nema očekivane 4 kolone (FROM,TO,DEPARTURE,DURATION):\n" + line);
			Airport a1 = Airport.findAirport(parts[0]);
			Airport a2 = Airport.findAirport(parts[1]);
			String[] time = parts[2].split(":");
			if (time.length < 2)
				throw new FormatException("Neispravan format vremena (očekuje se HH:MM):\n" + line);
			int h, min, dur;
			try {
				h = Integer.parseInt(time[0].trim());
				min = Integer.parseInt(time[1].trim());
				dur = Integer.parseInt(parts[3].trim());
			} catch (NumberFormatException e) {
				throw new FormatException("Neispravni brojevi u redu:\n" + line);
			}
			Flight.addFlights(a1, a2, h, min, dur);
			line = br.readLine();
		}
	}

	public static void main(String[] args) {
		CSVReader csv = new CSVReader();
		try {
			csv.read("files/data.csv");
		} catch (FileNotExists | IOException | FormatException | AirportExists | AirportNotExists e) {
			System.out.println(e.getMessage());
		}
	}

}
