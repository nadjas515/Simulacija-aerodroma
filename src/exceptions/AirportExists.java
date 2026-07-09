package exceptions;

public class AirportExists extends Exception {
    public AirportExists(String airport) {
        super("Aerodrom "+airport+" postoji!");
    }
}
