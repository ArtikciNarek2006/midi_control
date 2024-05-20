package com.midi_control.midi_tiles.utils;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.midi_control.midi_tiles.midi.MyMidiController;
import com.midi_control.midi_tiles.midi.keyboard.MidiKeyboardView;
import com.midi_control.midi_tiles.midi.visualizer.MidiVisualizerView;

public class SettingsContainer {
    // defaults
    private static final String[][][] defaultConnectionsFreePlay = new String[][][]{
            {{"MidiControl", "Keyboard", "Virtual Keyboard", "Output"}, {"MidiControl", "Visualizer", "Live Visualizer", "Input"}},
            {{"MidiControl", "Keyboard", "Virtual Keyboard", "Output"}, {"MidiControl", "Synthesizer", "MidiSynth", "Input"}}
    };

    public static String[][][] getDefaultConnectionsFreePlay() {
        return defaultConnectionsFreePlay;
    }


    // current
    private static String[][][] connectionsFreePlay = new String[][][]{
            {{"MidiControl", "Keyboard", "Virtual Keyboard", "Output"}, {"MidiControl", "Visualizer", "Live Visualizer", "Input"}},
            {{"MidiControl", "Keyboard", "Virtual Keyboard", "Output"}, {"Volcano Mobile", "FluidSynth", "FluidSynth MIDI", "input"}}
    };

    public static String[][][] getConnectionsFreePlay() {
        return connectionsFreePlay;
    }

    public static void setConnectionsFreePlay(String[][][] connectionsFreePlay) {
        setConnectionsFreePlay(connectionsFreePlay, true);
    }

    public static void setConnectionsFreePlay(String[][][] connectionsFreePlay, boolean save) {
        SettingsContainer.connectionsFreePlay = connectionsFreePlay;
        if (save) {
            MyMidiController instance = MyMidiController.getInstanceUnsafe(null);
            if (instance != null)
                save_preferences(instance.getSharedPreferences());
        }
    }


    public static Float visViewSlideSpeed = MidiVisualizerView.DEFAULT_SLIDE_SPEED;
    public static Integer keyboardMinPitch = MidiKeyboardView.DefaultLowestPitch;
    public static Integer keyboardNumKeys = MidiKeyboardView.DefaultNumKeys;

    public static void setVisViewSlideSpeed(Float slideSpeed) {
        setVisViewSlideSpeed(slideSpeed, true);
    }
    public static void setVisViewSlideSpeed(Float slideSpeed, boolean save) {
        if (slideSpeed != null) {
            SettingsContainer.visViewSlideSpeed = slideSpeed;
            if (save) {
                MyMidiController instance = MyMidiController.getInstanceUnsafe(null);
                if (instance != null)
                    save_preferences(instance.getSharedPreferences());
            }
        }
    }
    public static void setKeyboardMinPitch(Integer MinPitch) {
        setKeyboardMinPitch(MinPitch, true);
    }
    public static void setKeyboardMinPitch(Integer MinPitch, boolean save) {
        if (MinPitch != null) {
            SettingsContainer.keyboardMinPitch = MinPitch;

            if (save) {
                MyMidiController instance = MyMidiController.getInstanceUnsafe(null);
                if (instance != null) {
                    MidiVisualizerView vv = instance.getVisView();
                    MidiKeyboardView kv = instance.getKeyboardView();

                    if (vv != null) {
                        MyMath.Cords<Integer> minMaxPitches = vv.getMinMaxPitches();
                        minMaxPitches.x = MinPitch;
                        minMaxPitches.y = minMaxPitches.x + keyboardNumKeys;
                        vv.setMinMaxPitches(minMaxPitches);
                    }
                    if (kv != null) {
                        kv.setLowestPitch(MinPitch);
                    }
                    save_preferences(instance.getSharedPreferences());
                }
            }
        }
    }
    public static void setKeyboardNumKeys(Integer numKeys) {
        setKeyboardNumKeys(numKeys, true);
    }
    public static void setKeyboardNumKeys(Integer numKeys, boolean save) {
        if (numKeys != null) {
            SettingsContainer.keyboardNumKeys = numKeys;
            if (save) {
                MyMidiController instance = MyMidiController.getInstanceUnsafe(null);
                if (instance != null) {
                    MidiVisualizerView vv = instance.getVisView();
                    MidiKeyboardView kv = instance.getKeyboardView();

                    if (vv != null) {
                        MyMath.Cords<Integer> minMaxPitches = vv.getMinMaxPitches();
                        minMaxPitches.y = minMaxPitches.x + numKeys;
                        vv.setMinMaxPitches(minMaxPitches);
                    }
                    if (kv != null) {
                        kv.setNumKeys(numKeys);
                    }
                    save_preferences(instance.getSharedPreferences());
                }
            }
        }
    }


    // usage interface
    public static void load_preferences(@NonNull SharedPreferences sp) {
        Gson gson = new Gson();

        String[][][] savedCons = gson.fromJson(sp.getString("connectionsFreePlay", null), String[][][].class);
        if (savedCons == null)
            savedCons = getDefaultConnectionsFreePlay();
        setConnectionsFreePlay(savedCons, false);

        Float slideSpeed = sp.getFloat("visViewSlideSpeed", MidiVisualizerView.DEFAULT_SLIDE_SPEED);
        setVisViewSlideSpeed(slideSpeed, false);

        Integer lowestPitch = sp.getInt("keyboardMinPitch", MidiKeyboardView.DefaultLowestPitch);
        setKeyboardMinPitch(lowestPitch, false);

        Integer numKeys = sp.getInt("keyboardNumKeys", MidiKeyboardView.DefaultNumKeys);
        setKeyboardNumKeys(numKeys, false);
    }

    public static void save_preferences(@NonNull SharedPreferences sp) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("connectionsFreePlay", gson.toJson(SettingsContainer.getConnectionsFreePlay()));

        editor.putFloat("visViewSlideSpeed", SettingsContainer.visViewSlideSpeed);
        editor.putInt("keyboardMinPitch", SettingsContainer.keyboardMinPitch);
        editor.putInt("keyboardNumKeys", SettingsContainer.keyboardNumKeys);

        editor.apply();
        editor.commit();
    }
}
