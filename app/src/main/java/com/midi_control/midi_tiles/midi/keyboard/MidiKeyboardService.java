package com.midi_control.midi_tiles.midi.keyboard;

import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;
import android.os.Handler;
import android.os.Looper;

import com.midi_control.midi_tiles.utils.ML;
import com.midi_control.midi_tiles.utils.MyMath;
import com.mobileer.miditools.MidiConstants;
import com.mobileer.miditools.MidiFramer;

import java.io.IOException;
import java.util.Arrays;

public class MidiKeyboardService extends MidiDeviceService {
    public static final String TAG = "MidiKeyboardService";

    public static byte velocityMin = 90, velocityMax = 110;
    private static MidiReceiver[] connectedReceivers;

    private static final MidiFramer mDeviceFramer = new MidiFramer(new MidiReceiver() {
        @Override
        public void onSend(byte[] msg, int offset, int count, long timestamp) throws IOException {
            broadcastBytes(msg, offset, count, timestamp);
        }
    });
    private static boolean receivers_reload_needed = false, reload_handler_started = false;


    static class MyReceiver extends MidiReceiver {
        @Override
        public void onSend(byte[] data, int offset, int count, long timestamp) throws IOException {
            if (mDeviceFramer != null) {
                // Send raw data to be parsed into discrete messages.
                mDeviceFramer.send(data, offset, count, timestamp);
            }
        }
    }

    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        return new MidiReceiver[]{new MyReceiver()};
    }

    private void startReloadHandler() {
        if (!reload_handler_started) {
            reload_handler_started = true;
            Handler handler = new Handler(Looper.getMainLooper());
            final Runnable r = new Runnable() {
                public void run() {
                    if (receivers_reload_needed) {
                        connectedReceivers = getOutputPortReceivers();
                        ML.warn(TAG, "onDeviceStatusChanged(): Runnable.run: Receivers:" + Arrays.toString(connectedReceivers));
                        receivers_reload_needed = false;
                    }
                    handler.postDelayed(this, 2000);
                }
            };

            handler.postDelayed(r, 1000);
        }
    }

    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        super.onDeviceStatusChanged(status);
        connectedReceivers = getOutputPortReceivers();
        ML.log(TAG, "onDeviceStatusChanged(): initial call: status:" + status.toString());

        startReloadHandler();
    }


    public static void broadcastBytes(byte[] msg, int offset, int count) {
        if (connectedReceivers != null) {
            receivers_reload_needed = false;
            for (int i = 0; i < connectedReceivers.length; i++) {
                MidiReceiver connectedReceiver = connectedReceivers[i];
                try {
                    ML.log(TAG, "connectedReceiver[" + i + "]: " + connectedReceiver + "; msg:" + Arrays.toString(msg) + "; offset:" + offset);
                    connectedReceiver.send(msg, offset, count);
                } catch (Exception exception) {
                    receivers_reload_needed = true;
                    ML.err(TAG, "broadcastBytes(" + Arrays.toString(msg) + ", " + offset + ", " + count + ") exception:" + exception.getMessage());
                }
            }
        }
    }

    public static void broadcastBytes(byte[] msg, int offset, int count, long timestamp) {
        if (connectedReceivers != null) {
            receivers_reload_needed = false;
            for (int i = 0; i < connectedReceivers.length; i++) {
                MidiReceiver connectedReceiver = connectedReceivers[i];
                try {
                    ML.log(TAG, "connectedReceiver[" + i + "]: " + connectedReceiver + "; msg:" + Arrays.toString(msg) + "; offset:" + offset);
                    connectedReceiver.send(msg, offset, count, timestamp);
                } catch (Exception exception) {
                    receivers_reload_needed = true;
                    ML.err(TAG, "broadcastBytes(" + Arrays.toString(msg) + ", " + offset + ", " + count + ", " + timestamp + ") exception:" + exception.getMessage());
                }
            }
        }
    }

    public static void broadcast(byte status, byte pitch, byte velocity) {
        byte[] msg = new byte[]{status, pitch, velocity};
        if (status == MidiConstants.STATUS_NOTE_OFF)
            msg[2] = 0;

        broadcastBytes(msg, 0, 3);
    }

    public static void broadcast(byte status, byte pitch) {
        byte velocity = 0;
        if (status == MidiConstants.STATUS_NOTE_ON)
            velocity = MyMath.random(velocityMin, velocityMax);

        broadcast(status, pitch, velocity);
    }
}
