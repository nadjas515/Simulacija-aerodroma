package data;

import java.util.ArrayList;
import java.util.List;

public class Flight extends Data{
	static List<Flight> flights = new ArrayList<>();
	
	Airport start, end;
	int h,min;
	int duration;
	
	
	public Flight(Airport start, Airport end, int h, int min, int duration) {
		super();
		this.start = start;
		this.end = end;
		this.h = h;
		this.min = min;
		this.duration = duration;
	}
	
	public static void addFlights(Airport start, Airport end, int h, int min, int duration) {
		flights.add(new Flight(start,end,h,min,duration));
	}
	
	public static void addFlights(Flight f) {
		flights.add(f);
	}

	public static List<Flight> getFlights() {
		return flights;
	}

	public static void clearAll() {
		flights.clear();
	}
	
	@Override
	public String toString() {
		return start.toString()+" "+end.toString()+" "+duration;
	}
	
	public static void printFlights() {
		for(Flight f:flights) {
			System.out.println(f);
		}
	}
	
	public Airport getStart() {
		return start;
	}
	public Airport getEnd() {
		return end;
	}
	public int getH() {
		return h;
	}
	public int getMin() {
		return min;
	}
	public int getDuration() {
		return duration;
	}

	public Object[] toRow() {
		return new Object[]{
				start.getCode(),
				end.getCode(),
				String.format("%02d:%02d", h, min),   // vreme kao "08:30"
				duration
		};
	}
	
}
