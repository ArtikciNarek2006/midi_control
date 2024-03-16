package com.midi_control.midi.services;

import android.content.Context;
import android.media.AudioManager;
import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;

import com.mobileer.miditools.synth.LatencyController;
import com.mobileer.miditools.synth.SynthEngine;

public class MidiSynthService extends MidiDeviceService {
    public static final String TAG = "MidiSynthService";
    private static final SynthEngine mSynthEngine = new SynthEngine();
    private boolean mSynthStarted = false;
//    private static MidiSynthService mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
//        mInstance = this;
        queryOptimalAudioSettings();
    }

    // Query the system for the best sample rate and buffer size for low latency.
    public void queryOptimalAudioSettings() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        String text = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        int framesPerBlock = Integer.parseInt(text);
        mSynthEngine.setFramesPerBlock(framesPerBlock);
    }

    @Override
    public void onDestroy() {
        mSynthEngine.stop();
        super.onDestroy();
    }

    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        return new MidiReceiver[] { mSynthEngine };
    }

    /**
     * This will get called when clients connect or disconnect.
     */
    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        if (status.isInputPortOpen(0) && !mSynthStarted) {
            mSynthEngine.start();
            mSynthStarted = true;
        } else if (!status.isInputPortOpen(0) && mSynthStarted){
            mSynthEngine.stop();
            mSynthStarted = false;
        }
    }

    public static LatencyController getLatencyController() {
        return mSynthEngine.getLatencyController();
    }

    public static int getMidiByteCount() {
        return mSynthEngine.getMidiByteCount();
    }
}
