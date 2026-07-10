package data;

import java.util.ArrayList;
import java.util.List;

import util.TimeUtil;

public class Airplane implements Data {
    private Flight flight;
    private int startTime, endTime;   // sim-vreme poletanja i sletanja (u minutima)
    private float x, y;               // trenutna pozicija (koordinate)
    private boolean flying,finished;
    private static List<Airplane> airplanes = new ArrayList<Airplane>();

    public Airplane(Flight flight) {
        this.flight = flight;
        this.x = flight.start.x;
        this.y = flight.start.y;
        this.flying = false;
        this.finished = false;
        this.startTime = TimeUtil.toMinutes(flight.h, flight.min);
        this.endTime = startTime + flight.duration;
        airplanes.add(this);
    }

    // Pozicija se računa DIREKTNO iz simuliranog vremena (interpolacija).
    // Radi tačno bez obzira na korak tajmera (100ms, 200ms...) - nema akumulacije greške.
    public static void move(int time) {
        for (Airplane a : airplanes) {
            if (time >= a.startTime && time < a.endTime) {
                a.flying = true;
                float progress = (time - a.startTime) / (float) a.flight.duration;   // 0..1
                a.x = a.flight.start.x + (a.flight.end.x - a.flight.start.x) * progress;
                a.y = a.flight.start.y + (a.flight.end.y - a.flight.start.y) * progress;
            } else {
                a.flying = false;   // pre poletanja ili posle sletanja - ne prikazuje se
                if (time >= a.endTime) a.finished = true;
            }
        }
    }

    public boolean getFlying() {
        return flying;
    }

    public static void resetAll() {
        for (Airplane a : airplanes) {
            a.x = a.flight.start.x;
            a.y = a.flight.start.y;
            a.flying = false;
            a.finished = false;
        }
    }



    public static void clearAll() {
        airplanes.clear();
    }

    // posle preraspoređivanja (pravilo 10 min) osveži vreme poletanja/sletanja
    public static void reschedule() {
        for (Airplane a : airplanes) {
            a.startTime = TimeUtil.toMinutes(a.flight.h, a.flight.min);
            a.endTime = a.startTime + a.flight.duration;
        }
    }

    public static boolean allFinished() {
        for (Airplane a : airplanes) {
            if (!a.finished) return false;
        }
        return true;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }
}
