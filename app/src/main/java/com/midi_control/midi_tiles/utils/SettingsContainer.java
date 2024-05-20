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
        SettingsContainer.connectionsFreePlay = connectionsFreePlay;
        MyMidiController instance = MyMidiController.getInstanceUnsafe(null);
        if (instance != null)
            save_preferences(instance.getSharedPreferences());
    }


    public static Float visViewSlideSpeed = MidiVisualizerView.DEFAULT_SLIDE_SPEED;
    public static Integer keyboardMinPitch = MidiKeyboardView.DefaultLowestPitch;
    public static Integer keyboardNumKeys = MidiKeyboardView.DefaultNumKeys;
    public static void setVisViewSlideSpeed(Float slideSpeed){
        if (slideSpeed != null){
            SettingsContainer.visViewSlideSpeed = slideSpeed;
            MyMidiController instance = MyMidiController.getInstanceUnsafe(null);
            if (instance != null)
                save_preferences(instance.getSharedPreferences());
        }
    }
    public static void setKeyboardMinPitch(Integer MinPitch) {
        if (MinPitch != null) {
            SettingsContainer.keyboardMinPitch = MinPitch;

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
    public static void setKeyboardNumKeys(Integer numKeys) {
        if (numKeys != null) {
            SettingsContainer.keyboardNumKeys = numKeys;

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


    // usage interface
    public static void load_preferences(@NonNull SharedPreferences sp) {
        Gson gson = new Gson();

        String[][][] savedCons = gson.fromJson(sp.getString("connectionsFreePlay", null), String[][][].class);
        if (savedCons == null)
            savedCons = getDefaultConnectionsFreePlay();
        setConnectionsFreePlay(savedCons);

        Float slideSpeed = sp.getFloat("visViewSlideSpeed", MidiVisualizerView.DEFAULT_SLIDE_SPEED);
        setVisViewSlideSpeed(slideSpeed);

        Integer lowestPitch = sp.getInt("keyboardMinPitch", MidiKeyboardView.DefaultLowestPitch);
        setKeyboardMinPitch(lowestPitch);

        Integer numKeys = sp.getInt("keyboardNumKeys", MidiKeyboardView.DefaultNumKeys);
        setKeyboardNumKeys(numKeys);
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
