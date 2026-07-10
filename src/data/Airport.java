package data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import exceptions.AirportExists;
import exceptions.AirportNotExists;

public class Airport extends Data{
	static List<Airport> airports = new ArrayList<>();
	
	int x,y;
	String name;
	String code;
	boolean visible;
	
	
	public Airport(int x, int y, String name, String code) {
		super();
		this.x = x;
		this.y = y;
		this.name = name;
		this.code = code;
		this.visible = false;
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
	public boolean isVisible() {return visible;}
	
	public static Airport findAirport(String code) throws AirportNotExists{
		for(Airport airp : airports) {
			if(airp.code.equals(code))
				return airp;
		}
		throw new AirportNotExists(code);
	}

	public static boolean showAll() {
		for(Airport airp : airports) {
			if(airp.visible)
				return false;
		}
		return true;
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
		return new Object[]{ code, name, x, y , visible};
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
