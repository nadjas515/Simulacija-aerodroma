package data;

import java.util.ArrayList;
import java.util.List;

import util.TimeUtil;

public class Airplane implements Data {
    private Flight flight;
    private int startTime, endTime;   // simulated take-off and landing time (in minutes)
    private float x, y;               // current position (coordinates)
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

    // The position is computed DIRECTLY from the simulated time (interpolation).
    // It is exact regardless of the timer step (100ms, 200ms...), so no error accumulates.
    public static void move(int time) {
        for (Airplane a : airplanes) {
            if (time >= a.startTime && time < a.endTime) {
                a.flying = true;
                float progress = (time - a.startTime) / (float) a.flight.duration;   // 0..1
                a.x = a.flight.start.x + (a.flight.end.x - a.flight.start.x) * progress;
                a.y = a.flight.start.y + (a.flight.end.y - a.flight.start.y) * progress;
            } else {
                a.flying = false;   // before take-off or after landing: not shown
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

    // after rescheduling (10-minute rule), update the take-off/landing times
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
