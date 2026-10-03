package data;

// one flight: {"from":..,"to":..,"departure":"08:30","duration":..}
public class FlightJson {
	public String from;
	public String to;
	public String departure;   // kept as a String because it looks like "08:30"
	public int duration;
}
