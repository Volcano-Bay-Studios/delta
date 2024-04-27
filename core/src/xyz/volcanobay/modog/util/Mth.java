package xyz.volcanobay.modog.util;

public class Mth {
    public static float lerp(float a, float b, float f)
    {
        return a + f * (b - a);
    }
    public static double roundToZero(double val) {
        if (val < 0) {
            return Math.ceil(val);
        }
        return Math.floor(val);
    }
}
