package com.midi_control.midi_tiles.midi;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiManager;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.midi_control.midi_tiles.midi.keyboard.MidiKeyboardView;
import com.midi_control.midi_tiles.midi.live_visualizer.MidiLiveVisualizerService;
import com.midi_control.midi_tiles.midi.utils.MidiConnection;
import com.midi_control.midi_tiles.midi.utils.MidiConnectionsManager;
import com.midi_control.midi_tiles.midi.visualizer.MidiVisualizerView;
import com.midi_control.midi_tiles.utils.ML;
import com.midi_control.midi_tiles.utils.MidiUtils;
import com.midi_control.midi_tiles.utils.MyMath;
import com.midi_control.midi_tiles.utils.MyUtils;
import com.midi_control.midi_tiles.utils.SettingsContainer;
import com.mobileer.miditools.MidiDeviceMonitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class MyMidiController extends MidiManager.DeviceCallback implements Serializable {
    public static final String TAG = "MyMidiManager";
    private static MyMidiController instance;
    private static final ArrayList<MidiManager.DeviceCallback> deviceCallbacks = new ArrayList<>();

    @Nullable
    public static MyMidiController getInstance(Activity ctx) {
        if (MidiUtils.midiSupported(ctx)) {
            String msg = "MyMidiController instance request.";
            if (instance == null) {
                msg += ": MyMidiController instance created";
                instance = new MyMidiController(ctx);
            }
            ML.log(TAG, msg);
            return instance;
        } else {
            ML.err(TAG, "MyMidiController instance request: MIDI is NOT supported.");
            return null;
        }
    }

    @Nullable
    public static MyMidiController getInstanceUnsafe(@Nullable Activity ctx) {
        if (ctx == null) {
            if (instance != null) {
                ML.log(TAG, "getInstanceUnsafe(null): success");
                return instance;
            }
            ML.err(TAG, "getInstanceUnsafe(null): failed no instance available");
            return null;
        }
        return getInstance(ctx);
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

    //
    public static void addDeviceCallback(MidiManager.DeviceCallback callback) {
        deviceCallbacks.add(callback);
    }

    public static void removeDeviceCallback(MidiManager.DeviceCallback callback) {
        deviceCallbacks.remove(callback);
    }

    // non static
    private final MidiConnectionsManager midiConnectionsManager;
    private final MidiDeviceMonitor midiDeviceMonitor;
    private final SharedPreferences sharedPreferences;

    public State currentState = State.UNDEFINED;
    public MidiManager midiManager;
    private MidiVisualizerView currentVisView;
    private MidiKeyboardView currentKeyboardView;

    public MyMidiController(@NonNull Activity ctx) {
        midiManager = (MidiManager) ctx.getSystemService(Context.MIDI_SERVICE);
        midiConnectionsManager = new MidiConnectionsManager(midiManager);
        midiDeviceMonitor = MidiDeviceMonitor.getInstance(midiManager);
        sharedPreferences = ctx.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SettingsContainer.load_preferences(sharedPreferences);
    }


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private void init_connections(@NonNull String[][][] connections) {
        int delay = 250, delay_step = 100;
        for (String[][] connection : connections) {
            MyUtils.setTimeout(() -> {
                        MidiConnection.Status status = midiConnectionsManager.createConnection(
                                connection[0][0], connection[0][1], connection[0][2], connection[0][3],
                                connection[1][0], connection[1][1], connection[1][2], connection[1][3]
                        );
                        if (status != MidiConnection.Status.OK) {
                            ML.err(TAG, "init_connections: " + status + ", con:" + Arrays.toString(connection));
                        }
                    }
                    , delay);
            delay += delay_step;
        }
    }

    private void init_liveVisualizerState(@NonNull Activity ctx, int visViewId) {
        MidiVisualizerView visView = ctx.findViewById(visViewId);
        this.setVisView(visView);
        // keyboard fragments view should call setKeyboardView;

        init_connections(SettingsContainer.getConnectionsFreePlay());
    }

    public void setVisView(MidiVisualizerView visView) {
        if (visView != null) {
            currentVisView = visView;
            ML.log(TAG, "setVisView(" + visView + "); " + currentState);
            visView.setSlideSpeed(SettingsContainer.visViewSlideSpeed);
            visView.setMinMaxPitches(new MyMath.Cords<>(SettingsContainer.keyboardMinPitch, SettingsContainer.keyboardMinPitch + SettingsContainer.keyboardNumKeys));

            if (currentState == State.LiveVisualizer) {
                MidiLiveVisualizerService.getPresenter().setVisView(currentVisView);
            } else {
                currentVisView = null;
            }
        }
    }
    public MidiVisualizerView getVisView() {
        return currentVisView;
    }

    public void setKeyboardView(MidiKeyboardView keyboardView) {
        if (keyboardView != null) {
            currentKeyboardView = keyboardView;
            currentKeyboardView.setNumKeys(SettingsContainer.keyboardNumKeys);
            currentKeyboardView.setLowestPitch(SettingsContainer.keyboardMinPitch);

            ML.log(TAG, "setKeyboardView(" + keyboardView + "); " + currentState);
            if (currentState == State.LiveVisualizer) {
                // Nothing to do here
            } else {
                currentKeyboardView = null;
            }
        }
    }
    public MidiKeyboardView getKeyboardView() {
        return currentKeyboardView;
    }
    public void setState(State newState, @NonNull Activity ctx, Integer visViewId) {
        this.currentState = newState;
        ML.log(TAG, "setState(" + newState + ", ..., " + visViewId + ");");

        midiConnectionsManager.closeAll();
        midiDeviceMonitor.registerDeviceCallback(this, new Handler(Looper.getMainLooper()));


        if (currentState == State.LiveVisualizer) {
            assert visViewId != null;
            init_liveVisualizerState(ctx, visViewId);
        } else {
            // state: UNDEFINED -> resetting
            midiDeviceMonitor.unregisterDeviceCallback(this);
            setKeyboardView(null);
            setVisView(null);
        }
    }


    // Midi Device Monitor Events listeners
    @Override
    public void onDeviceAdded(MidiDeviceInfo device) {
        MidiDeviceInfo.PortInfo[] portInfos = device.getPorts();
        for (MidiDeviceInfo.PortInfo portInfo : portInfos) {
            com.midi_control.midi_tiles.midi.port_selector.MidiPortWrapper wrapper = new com.midi_control.midi_tiles.midi.port_selector.MidiPortWrapper(device, portInfo.getType(), portInfo.getPortNumber());
            ML.log(TAG, "onDeviceAdded: device port wrapper: " + wrapper);
        }
        for (MidiManager.DeviceCallback callback : deviceCallbacks) {
            callback.onDeviceAdded(device);
        }
    }

    @Override
    public void onDeviceRemoved(MidiDeviceInfo device) {
        MidiDeviceInfo.PortInfo[] portInfos = device.getPorts();
        for (MidiDeviceInfo.PortInfo portInfo : portInfos) {
            com.midi_control.midi_tiles.midi.port_selector.MidiPortWrapper wrapper = new com.midi_control.midi_tiles.midi.port_selector.MidiPortWrapper(device, portInfo.getType(), portInfo.getPortNumber());
            ML.log(TAG, "onDeviceRemoved: device port wrapper: " + wrapper);
        }
        for (MidiManager.DeviceCallback callback : deviceCallbacks) {
            callback.onDeviceRemoved(device);
        }
    }

    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        ML.log(TAG, "MidiPortSelector.onDeviceStatusChanged status = " + status);
        for (MidiManager.DeviceCallback callback : deviceCallbacks) {
            callback.onDeviceStatusChanged(status);
        }
    }
}
