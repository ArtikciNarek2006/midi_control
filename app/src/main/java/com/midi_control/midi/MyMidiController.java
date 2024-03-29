package com.midi_control.midi;


import android.app.Activity;
import android.content.Context;
import android.media.midi.MidiManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.midi_control.midi.keyboard.MidiKeyboardView;
import com.midi_control.midi.live_visualizer.MidiLiveVisualizerService;
import com.midi_control.midi.utils.MidiConnectionsManager;
import com.midi_control.midi.visualizer.MidiVisualizerView;
import com.midi_control.utils.ML;
import com.midi_control.utils.MidiUtils;
import com.midi_control.utils.MyUtils;

import java.io.Serializable;

public class MyMidiController implements Serializable {
    public static final String TAG = "MyMidiManager";
    private static MyMidiController instance;

    @Nullable
    public static MyMidiController getInstance(Activity ctx) {
        if (MidiUtils.midiSupported(ctx)) {
            if (instance == null) {
                instance = new MyMidiController(ctx);
            }
            ML.log(TAG, "MIDI is supported.");
            return instance;
        } else {
            ML.err(TAG, "MIDI is NOT supported.");
            return null;
        }
    }

    public enum State {
        LiveVisualizer("LiveVisualizer"), FileVisualizer("FileVisualizer"), UNDEFINED("UNDEFINED");
        public final String text;

        State(String value) {
            text = value;
        }

        @NonNull
        @Override
        public String toString() {
            return "State: " + text;
        }
    }

    // non static
    private final MidiConnectionsManager midiConnectionsManager;
    public State currentState = State.UNDEFINED;
    public MidiManager midiManager;
    private MidiVisualizerView currentVisView;
    private MidiKeyboardView currentKeyboardView;

    public MyMidiController(@NonNull Activity ctx) {
        midiManager = (MidiManager) ctx.getSystemService(Context.MIDI_SERVICE);
        midiConnectionsManager = new MidiConnectionsManager(midiManager);
    }

    private void init_liveVisualizerState(@NonNull Activity ctx, int visViewId) {
        MidiVisualizerView visView = ctx.findViewById(visViewId);
        this.setVisView(visView);
        // keyboard fragments view should call setKeyboardView;

        // connect services
        MyUtils.setTimeout(() -> midiConnectionsManager.createConnection(
                "MidiControl", "Keyboard", "Virtual Keyboard", "Output",
                "MidiControl", "Visualizer", "Live Visualizer", "Input"
        ), 500);

//        MyUtils.setTimeout(() -> midiConnectionsManager.createConnection(
//                        "MidiControl", "Keyboard", "Virtual Keyboard", "Output",
//                        "MidiControl", "Synthesizer", "MidiSynth", "Input"
//        ), 700);

        MyUtils.setTimeout(() -> midiConnectionsManager.createConnection(
                "MidiControl", "Keyboard", "Virtual Keyboard", "Output",
                "Volcano Mobile", "FluidSynth", "FluidSynth MIDI", "input"
        ), 700);
    }

    public void setVisView(MidiVisualizerView visView) {
        if (visView != null) {
            currentVisView = visView;
            ML.log(TAG, "setVisView(" + visView + "); " + currentState);

            if (currentState == State.LiveVisualizer) {
                MidiLiveVisualizerService.getPresenter().setVisView(currentVisView);
            } else if (currentState == State.FileVisualizer) {
                // TODO: fill this place
            } else {
                currentVisView = null;
            }
        }
    }

    public void setKeyboardView(MidiKeyboardView keyboardView) {
        if (keyboardView != null) {
            currentKeyboardView = keyboardView;
            ML.log(TAG, "setKeyboardView(" + keyboardView + "); " + currentState);

            if (currentState == State.LiveVisualizer) {
                // Nothing to do here
            } else if (currentState == State.FileVisualizer) {
                // TODO: fill this place
            } else {
                currentKeyboardView = null;
            }
        }
    }

    public void setState(State newState, @NonNull Activity ctx, int visViewId) {
        this.currentState = newState;
        ML.log(TAG, "setState(" + newState + ", ..., " + visViewId + ");");

        midiConnectionsManager.closeAll();

        if (currentState == State.LiveVisualizer) {
            init_liveVisualizerState(ctx, visViewId);
        } else if (currentState == State.FileVisualizer) {
//            init_fileVisualizerState(ctx, visViewId); // TODO: implement
        } else {
            // state: UNDEFINED -> resetting
            setKeyboardView(null);
            setVisView(null);
        }
    }
}
