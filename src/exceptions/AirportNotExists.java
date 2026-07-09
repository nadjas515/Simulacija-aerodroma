package exceptions;

public class AirportNotExists  extends Exception{
	public AirportNotExists(String airport) {
		super("Aerodrom "+airport+" ne postoji!");
	}

}
