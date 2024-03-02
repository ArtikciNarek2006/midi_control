package com.midi_control.utils;

public class MyMath {
    public static final String TAG = "MyMath";
    public static final long NANOS_PER_MILLISECOND = 1000000L;
    public static final long NANOS_PER_SECOND = NANOS_PER_MILLISECOND * 1000L;
    public static final class Cords<T> {
        public T x, y;

        public Cords(T x, T y) {
            this.x = x;
            this.y = y;
        }
    }


}
