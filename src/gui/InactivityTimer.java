package gui;

import java.awt.EventQueue;
import java.awt.Frame;

public class InactivityTimer extends Thread {

    private static final int LIMIT = 60;   // ukupno sekundi neaktivnosti pre zatvaranja
    private static final int WARN  = 5;    // poslednjih 5s ide upozorenje sa odbrojavanjem

    private final Frame owner;
    private volatile int elapsed = 0;
    private WarningDialog warning;          // menja se samo na EDT-u

    public InactivityTimer(Frame owner) {
        this.owner = owner;
        setDaemon(true);   // nit ne drži program otvorenim
    }

    // poziva se na svaku akciju korisnika -> vrati brojač na 0 i zatvori upozorenje
    public void reset() {
        elapsed = 0;
        if (warning != null) {
            warning.dispose();
            warning = null;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);   // odspavaj 1s
            } catch (InterruptedException e) {
                // ignore
            }
            elapsed++;
            int remaining = LIMIT - elapsed;

            if (remaining <= 0) {
                // 60s neaktivnosti -> zatvori program
                EventQueue.invokeLater(() -> {
                    owner.dispose();
                    System.exit(0);
                });
                return;
            }

            if (remaining <= WARN) {
                // poslednjih 5s: prikaži/osveži dijalog sa preostalim vremenom
                final int rem = remaining;
                EventQueue.invokeLater(() -> {
                    if (warning == null)
                        warning = new WarningDialog(owner, this);
                    warning.setRemaining(rem);
                });
            }
        }
    }
}
