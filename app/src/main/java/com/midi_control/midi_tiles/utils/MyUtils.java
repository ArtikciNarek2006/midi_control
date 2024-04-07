package com.midi_control.midi_tiles.utils;

import android.graphics.Paint;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MyUtils {
    public static final String TAG = "MyUtils";
    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                ML.err(TAG, "setTimeout() exception:" + e.getMessage());
            }
        }).start();
    }

    @NonNull
    public static String paintArray_toString(@NonNull Paint[] arr){
        StringBuilder str = new StringBuilder("[" + arr.length + "] {");
        for (Paint p:arr  ) {
            str.append("#").append(String.format("%08x", p.getColor())).append(", ");
        }
        str.delete(str.length() - 2, str.length());
        str.append("}");
        return str.toString();
    }

    public static boolean stringComp(String str1, String str2){
        if (str1 == null){
            return Objects.equals(null, str2);
        }else{
            return str1.equals(str2);
        }
    }
}
