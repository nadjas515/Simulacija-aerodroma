package exceptions;

public class FileNotExists extends Exception{
	
	public FileNotExists(String file) {
		super("Fajl "+file+" ne postoji!");
	}

}
