package data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import util.TimeUtil;

public class Flight{
	static List<Flight> flights = new ArrayList<>();
	
	Airport start, end;
	int plannedH, plannedMin;
	int h,min;
	int duration;
	Airplane plane;

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
	public Airplane getPlane() {return plane;}
	
	public Flight(Airport start, Airport end, int h, int min, int duration) {
		super();
		this.start = start;
		this.end = end;
		this.plannedH = h;
		this.plannedMin = min;
		this.h = h;
		this.min = min;
		this.duration = duration;
		this.plane=new Airplane(this);
	}
	
	public static void addFlights(Airport start, Airport end, int h, int min, int duration) {
		flights.add(new Flight(start,end,h,min,duration));
		flights.sort(Comparator.comparingInt(f -> TimeUtil.toMinutes(f.plannedH, f.plannedMin)));
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
	
	public static void reschedule(){
		// vrati sva vremena na planirano, pa raspoređuj iz nule
		// (da rezultat ne zavisi od prethodnih poziva reschedule-a)
		for (Flight f : flights) {
			f.h = f.plannedH;
			f.min = f.plannedMin;
		}
		for (Flight ref : flights) {
			for (Flight other : flights) {
				if (other == ref || !other.start.code.equals(ref.start.code)) continue;
				int otherTime = TimeUtil.toMinutes(other.h, other.min);
				int refTime = TimeUtil.toMinutes(ref.h, ref.min);
				if (Math.abs(otherTime - refTime) < 10) {
					otherTime = (refTime / 10 + 1) * 10;
					other.h = TimeUtil.hours(otherTime);
					other.min = TimeUtil.minutes(otherTime);
				}
			}
		}
		flights.sort(Comparator.comparingInt(f -> TimeUtil.toMinutes(f.plannedH, f.plannedMin)));
		Airplane.reschedule();
	}

	public Object[] toRow() {
		return new Object[]{
				start.getCode(),
				end.getCode(),
				h==plannedH && min==plannedMin
					? TimeUtil.format(TimeUtil.toMinutes(h, min))
					: TimeUtil.format(TimeUtil.toMinutes(plannedH, plannedMin)) + "->" + TimeUtil.format(TimeUtil.toMinutes(h, min)),
				duration
		};
	}
	
}
