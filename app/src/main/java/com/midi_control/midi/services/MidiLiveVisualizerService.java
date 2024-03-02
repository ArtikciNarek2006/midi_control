package com.midi_control.midi.services;

import android.content.Intent;
import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;
import android.widget.Toast;

import com.midi_control.midi.live_visualizer.LiveVisualizerPresenter;
import com.midi_control.midi.live_visualizer.LiveVisualizerReceiver;
import com.midi_control.midi.utils.MidiPrinter;
import com.midi_control.utils.ML;
import com.mobileer.miditools.MidiFramer;

import java.io.IOException;

public class MidiLiveVisualizerService extends MidiDeviceService {
    public static final String TAG = "MidiLiveVisualizerService";

    private static LiveVisualizerPresenter presenter;
    private static MidiFramer mDeviceFramer;
    private final MidiReceiver mInputReceiver = new MyReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        ML.log(TAG, "onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ML.log(TAG, "onDestroy()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Services Has Been Started!", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        ML.log(TAG, "onGetInputPortReceivers()");
        return new MidiReceiver[]{mInputReceiver};
    }

    public static LiveVisualizerPresenter getPresenter() {
        return presenter;
    }

    public static void setPresenter(LiveVisualizerPresenter presenter1) {
        if (presenter1 != null) {
            // Receiver that prints the messages.
            LiveVisualizerReceiver loggingReceiver = new LiveVisualizerReceiver(presenter1);
            mDeviceFramer = new MidiFramer(loggingReceiver);
        }
        presenter = presenter1;
    }

    static class MyReceiver extends MidiReceiver {
        @Override
        public void onSend(byte[] data, int offset, int count, long timestamp) throws IOException {
            if (presenter != null) {
                // Send raw data to be parsed into discrete messages.
                mDeviceFramer.send(data, offset, count, timestamp);
            }
        }
    }

    /**
     * This will get called when clients connect or disconnect.
     * Log device information.
     */
    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        if (presenter != null) {
            if (status.isInputPortOpen(0)) {
                ML.log(TAG, "=== connected ===");
                ML.log(TAG, MidiPrinter.formatDeviceInfo(status.getDeviceInfo()));
            } else {
                ML.log(TAG, "--- disconnected ---");
            }
        }
    }
}
