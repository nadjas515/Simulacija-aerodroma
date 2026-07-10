package gui;

import data.Airplane;
import data.Flight;

import java.awt.EventQueue;
import java.awt.Frame;

public class InactivityTimer extends Thread {

    private static final int LIMIT = 60;   // ukupno sekundi neaktivnosti pre zatvaranja
    private static final int WARN  = 5;    // poslednjih 5s ide upozorenje sa odbrojavanjem

    private final Frame owner;
    private WarningDialog warning;          // menja se samo na EDT-u

    private volatile boolean pause=false;

    public InactivityTimer(Frame owner) {
        this.owner = owner;
        setDaemon(true);   // nit ne drži program otvorenim
    }

    // poziva se na svaku akciju korisnika -> vrati brojač na 0 i zatvori upozorenje
    public void reset() {
        interrupt();
        if (warning != null) {
            warning.dispose();
            warning = null;
        }
    }

    public void pause() {
        pause = true;
    }

    public void cont() {
        pause = false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep((LIMIT-WARN)*1000);   // odspavaj 1s
            } catch (InterruptedException e) {
                continue;
            }
            if(pause){
                if(!Flight.getFlights().isEmpty() && Airplane.allFinished())
                    pause=false;
                else
                    continue;
            }
            try {
                for(int i=0;i<WARN;i++){
                    Thread.sleep(1000);
                    final int rem = WARN-i;
                    EventQueue.invokeLater(() -> {
                        if (warning == null)
                            warning = new WarningDialog(owner, this);
                        warning.setRemaining(rem);
                    });
                }
            } catch (InterruptedException ex) {
                continue;
            }

            EventQueue.invokeLater(() -> {
                owner.dispose();
                System.exit(0);
            });
        }
    }
}
