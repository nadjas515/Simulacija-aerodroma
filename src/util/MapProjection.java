package util;

public class MapProjection {
    public static int toPixelX(float x, int width)  { return Math.round((1 + x / 180f) * (width / 2f)); }
    public static int toPixelY(float y, int height) { return Math.round((1 - y / 90f) * (height / 2f)); }
}