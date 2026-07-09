package data;

import java.io.BufferedReader;
import java.io.IOException;

import exceptions.AirportNotExists;
import exceptions.FileNotExists;
import exceptions.FormatException;

public class CSVReader extends Reader {
	
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
		String line = br.readLine();
		if(!line.equals("# AIRPORTS"))
			throw new FormatException("Nedostaje linija # AIRPORTS");
		line = br.readLine();
		line = br.readLine();
		while(!line.isEmpty()) {
			String[] parts = line.split(",");
			try {
				int x=Integer.parseInt(parts[2]);
				int y=Integer.parseInt(parts[3]);
				if(Math.abs(x)>180)
					System.out.println("Aerodrom " + parts[0] + ": X koordinata (" + x
						+ ") mora biti između -180 i 180. Aerodrom je preskočen.");
				else if(Math.abs(y)>90)
					System.out.println("Aerodrom " + parts[0] + ": Y koordinata (" + y
						+ ") mora biti između -90 i 90. Aerodrom je preskočen.");
				else
					Airport.addAirport(x, y, parts[1], parts[0]);
			} catch (NumberFormatException e) {
				System.out.println("Linija :\n"+line+"\nnije ispravna!");
			}
			line = br.readLine();
		}
		Airport.printAirports();
		
		line = br.readLine();
		if(!line.equals("# FLIGHTS"))
			return; //obraditi grešku
		line = br.readLine();
		line = br.readLine();
		while(line!=null && !line.isEmpty()) {
			String[] parts = line.split(",");
			try {
				Airport a1 = Airport.findAirport(parts[0]);
				Airport a2 = Airport.findAirport(parts[1]);
				String[] time = parts[2].split(":");
				Flight.addFlights(a1,a2,Integer.parseInt(time[0]),Integer.parseInt(time[1]),Integer.parseInt(parts[3]));
			} catch (NumberFormatException e) {
				System.out.println("Linija :\n"+line+"\nnije ispravna!");
			}catch (AirportNotExists e) {
				System.out.println(e.getMessage());
			}
			line = br.readLine();
		}
		Flight.printFlights();
	}
	
	public static void main(String[] args) {
		CSVReader csv = new CSVReader();
		try {
			csv.read("files/data.csv");
		} catch (FileNotExists e) {
			System.out.println(e.getMessage());
		}
	}

}
