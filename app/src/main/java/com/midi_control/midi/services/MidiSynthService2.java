// source https://github.com/pedrolcl/android/tree/master/NativeGMSynth

package com.midi_control.midi.services;

import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;

import com.midi_control.utils.ML;

public class MidiSynthService2 extends MidiDeviceService {
    private static final String TAG = "MidiSynthService2";
    private MIDISynth mSynthEngine = null;
    private boolean mSynthStarted = false;

    @Override
    public void onCreate() {
        try {
            mSynthEngine = new MIDISynth();
            ML.log(TAG, "created");
            super.onCreate();
        } catch (Exception ex) {
            ML.err(TAG, String.valueOf(ex.getMessage()));
        }
    }

    @Override
    public void onClose() {
        if (mSynthEngine != null) {
            mSynthEngine.stop();
            mSynthEngine.close();
            mSynthStarted = false;
            mSynthEngine = null;
        }
        super.onClose();
        ML.err(TAG, "closed");
    }

    @Override
    public void onDestroy() {
        if (mSynthEngine != null) {
            mSynthEngine.stop();
            mSynthEngine.close();
            mSynthStarted = false;
            mSynthEngine = null;
        }
        super.onDestroy();
        ML.err(TAG, "destroyed");
    }

    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        return new MidiReceiver[]{mSynthEngine};
    }

    /**
     * This will get called when clients connect or disconnect.
     */
    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        try {
            if (status.isInputPortOpen(0) && !mSynthStarted) {
                mSynthEngine.start();
                mSynthStarted = true;
            } else if (!status.isInputPortOpen(0) && mSynthStarted) {
                mSynthEngine.stop();
                mSynthStarted = false;
            }
        } catch (Exception ex) {
            ML.err(TAG, String.valueOf(ex.getMessage()));
        }
    }
}
