package com.midi_control.midi_tiles.midi.keyboard;

import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;
import android.os.Handler;
import android.os.Looper;

import com.midi_control.midi_tiles.utils.ML;
import com.midi_control.midi_tiles.utils.MyMath;
import com.mobileer.miditools.MidiConstants;

import java.util.Arrays;

public class MidiKeyboardService extends MidiDeviceService {
    public static final String TAG = "MidiKeyboardService";

    public static byte velocityMin = 90, velocityMax = 110;
    private static MidiReceiver[] connectedReceivers;
    private static boolean receivers_reload_needed = false, reload_handler_started = false;

    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        return new MidiReceiver[0];
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


    public static void broadcast(byte status, byte pitch) {
        if (connectedReceivers != null) {
            receivers_reload_needed = false;

            byte[] msg = new byte[]{status, pitch, 0};
            if (status == MidiConstants.STATUS_NOTE_ON)
                msg[2] = MyMath.random(velocityMin, velocityMax);

//            for (MidiReceiver connectedReceiver : connectedReceivers) {
            for (int i = 0; i < connectedReceivers.length; i++) {
                MidiReceiver connectedReceiver = connectedReceivers[i];
                try {
                    ML.log(TAG, "connectedReceiver[" + i + "]: " + connectedReceiver + "; msg:" + Arrays.toString(msg));
                    connectedReceiver.send(msg, 0, 3);
                } catch (Exception exception) {
                    receivers_reload_needed = true;
                    ML.err(TAG, "broadcast(" + status + ", " + pitch + ") exception:" + exception.getMessage());
                }
            }
        }
    }
}
