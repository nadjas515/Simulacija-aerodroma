package exceptions;

public class FlightNotExists extends Exception {
	
	public FlightNotExists(String flight) {
		super("Let "+flight+" ne postoji!");
	}

}
