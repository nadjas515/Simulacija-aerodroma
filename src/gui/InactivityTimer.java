package gui;

import data.Airplane;
import data.Flight;

import java.awt.EventQueue;
import java.awt.Frame;

public class InactivityTimer extends Thread {

    private static final int LIMIT = 60;   // total seconds of inactivity before the app closes
    private static final int WARN  = 5;    // a countdown warning is shown for the last 5 s

    private final Frame owner;
    private WarningDialog warning;          // only modified on the EDT

    private volatile boolean pause=false;

    public InactivityTimer(Frame owner) {
        this.owner = owner;
        setDaemon(true);   // this thread does not keep the program alive
    }

    // called on every user action -> reset the counter and close the warning
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
                Thread.sleep((LIMIT-WARN)*1000);   // sleep until the warning period starts
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
