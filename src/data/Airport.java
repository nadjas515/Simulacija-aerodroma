package data;

import java.util.ArrayList;
import java.util.List;

import exceptions.AirportExists;
import exceptions.AirportNotExists;

public class Airport extends Data{
	static List<Airport> airports = new ArrayList<>();
	
	int x,y;
	String name;
	String code;
	
	
	public Airport(int x, int y, String name, String code) {
		super();
		this.x = x;
		this.y = y;
		this.name = name;
		this.code = code;
	}
	
	public static void addAirport(int x, int y, String name, String code) throws AirportExists {
		for(Airport airp : airports) {
			if(airp.code.equals(code))
				throw new AirportExists(code);
		}
		airports.add(new Airport(x,y,name,code));
	}
	
	public static void addAirport(Airport a) {
		airports.add(a);
	}

	public static List<Airport> getAirports() {
		return airports;
	}

	public static void clearAll() {
		airports.clear();
	}
	
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public String getName() {
		return name;
	}
	public String getCode() {
		return code;
	}
	
	public static Airport findAirport(String code) throws AirportNotExists{
		for(Airport airp : airports) {
			if(airp.code.equals(code))
				return airp;
		}
		throw new AirportNotExists(code);
	}
	
	@Override
	public String toString() {
		return "Ime: "+name+" Kod: "+code;
	}
	
	public static void printAirports(){
		for(Airport airp : airports) {
			System.out.println(airp.toString());
		}
	}

	public Object[] toRow() {
		return new Object[]{ code, name, x, y };
	}
	
}
