package com.midi_control.midi.file_visualizer;

import android.media.midi.MidiReceiver;

import com.midi_control.utils.ML;
import com.midi_control.utils.MidiUtils;

import java.io.IOException;

public class FileVisualizerReceiver extends MidiReceiver implements FileVisualizerContract.FileVisReceiver {
    public static final String TAG = "FileVisualizerReceiver";

    @Override
    public void onSend(byte[] msg, int offset, int count, long timestamp) throws IOException {
        ML.log(TAG, MidiUtils.receiverDataWrapper(msg, offset, count, timestamp));
    }
}
