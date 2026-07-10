package util;

public class TimeUtil {
    public static String format(int totalMinutes) {            // 510 -> "08:30"
        return String.format("%02d:%02d", hours(totalMinutes), minutes(totalMinutes));
    }
    public static int toMinutes(int h, int min) {              // (8,30) -> 510
        return h * 60 + min;
    }
    public static int hours(int totalMinutes) {                // 510 -> 8
        return totalMinutes / 60;
    }
    public static int minutes(int totalMinutes) {              // 510 -> 30
        return totalMinutes % 60;
    }
}
