package com.midi_control.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.mobileer.miditools.MidiConstants;
import com.mobileer.miditools.MusicKeyboardView;

public class MidiUtils {
    public static final String TAG = "MidiUtils";

    public static boolean midiSupported(@NonNull Context ctx) {
        return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI);
    }

    public static byte getChannel(byte status_code){
        return (byte) (status_code & MidiConstants.STATUS_CHANNEL_MASK);
    }

    public static byte getCommand(byte status_code){
        return (byte) (status_code & MidiConstants.STATUS_COMMAND_MASK);
    }

    public static boolean isBlackKey(byte pitch){
        final boolean[] NOTE_IN_OCTAVE_IS_BLACK = {
                false, true,
                false, true,
                false, false, true,
                false, true,
                false, true,
                false
        };
        return NOTE_IN_OCTAVE_IS_BLACK[pitch % 12];
    }


    /**
     * @param min_pitch inclusive
     * @param max_pitch inclusive
     * @return count of white notes
     */
    public static int countWhiteKeys(byte min_pitch, byte max_pitch){
        int count = 0;
        for (byte i = min_pitch; i <= max_pitch; i++) {
            if (!isBlackKey(i)){
                count++;
            }
        }
        return count;
    }

    @NonNull
    public static String receiverDataWrapper(byte[] msg, int offset, int count, long timestamp){
        StringBuilder text = new StringBuilder("{\n");
        text.append("\tmsg");
        if(msg != null) {
            text.append("[").append(msg.length).append("]: [");
            for (byte t : msg) {
                text.append(t).append(", ");
            }
            text.append("], \n");
        }else {
            text.append(" : null\n");
        }

        text.append("\toffset: ").append(offset);
        text.append("\tcount: ").append(count);
        text.append("\ttimestamp: ").append(timestamp);
        text.append("\n}");
        return text.toString();
    }
}
