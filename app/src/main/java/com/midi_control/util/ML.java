package com.midi_control.util;

import android.util.Log;

public class ML {
    // classname ML = MY_LOG
//    public static String[] log_tags = {"ML", "ME", "MW"};
    public static String[] log_tags = {"MY_LOG", "MY_ERR", "MY_WARN"};
    // log_tags[] = ["MY_LOG", "MY_ERR", "MY_WARN"]
    public static void l(Object data){
        Log.i(log_tags[0], String.valueOf(data));
    }

    public static void e(Object data){
        Log.e(log_tags[1], String.valueOf(data));
    }

    public static void w(Object data){
        Log.w(log_tags[2], String.valueOf(data));
    }
}
