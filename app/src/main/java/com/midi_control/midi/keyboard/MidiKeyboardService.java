package com.midi_control.midi.keyboard;

import android.content.Intent;
import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;
import android.os.IBinder;

import com.midi_control.utils.ML;
import com.midi_control.utils.MyMath;
import com.midi_control.utils.MyUtils;
import com.mobileer.miditools.MidiConstants;

import java.io.IOException;

public class MidiKeyboardService extends MidiDeviceService {
    public static final String TAG = "MidiKeyboardService";
    private Binder binder;
    public static byte velocityMin = 90, velocityMax = 110;
    private static MidiKeyboardService mInstance;
    private MidiReceiver[] connectedReceivers;

    public static MidiKeyboardService getInstance(){
        if (mInstance == null){
            new MidiKeyboardService();
        }
        ML.log(TAG, "static getInstance(): mInstance:" + mInstance.toString());

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new Binder();
        ML.log(TAG, "onCreate(): mInstance:" + mInstance.toString());
    }

    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        return new MidiReceiver[0];
    }

    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        super.onDeviceStatusChanged(status);
        connectedReceivers = getOutputPortReceivers();
        ML.log(TAG, "onDeviceStatusChanged(): status:" + status.toString());
    }



    public void broadcast(byte status, byte pitch) {
        if(connectedReceivers != null){
            boolean receivers_reload_needed = false;

            byte[] msg = new byte[] {MidiConstants.STATUS_NOTE_ON, pitch, MyMath.random(velocityMin, velocityMax)};
            for (MidiReceiver connectedReceiver : connectedReceivers) {
                try {
                    connectedReceiver.send(msg, 0, 3);
                } catch (IOException exception) {
                    receivers_reload_needed = true;
                    ML.warn(TAG, "broadcast(" + status + ", " + pitch + ") exception:" + exception.getMessage());
                }
            }

            if (receivers_reload_needed){
                connectedReceivers = getOutputPortReceivers();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class Binder extends android.os.Binder {
        public MidiKeyboardService getService() {
            return MidiKeyboardService.this;
        }
    }
}
