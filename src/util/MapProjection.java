package util;

public class MapProjection {
    public static int toPixelX(int x, int width)  { return (int)((1 + x / 180.0) * (width / 2)); }
    public static int toPixelY(int y, int height) { return (int)((1 - y / 90.0) * (height / 2)); }
}