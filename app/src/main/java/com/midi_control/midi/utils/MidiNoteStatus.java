package com.midi_control.midi.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.midi_control.utils.ML;
import com.mobileer.miditools.MidiConstants;

public class MidiNoteStatus {
    public static final String TAG = "MidiNote";

    public byte channel, pitch, velocity;
    public long timestamp, java_timestamp;
    public boolean isOn;

    @Nullable
    public static MidiNoteStatus getInstance(MidiMessage msg) {
        if (msg == null) {
            ML.err(TAG, "MidiMessage is NULL");
            return null;
        }
        boolean is_on;
        byte channel = msg.channel;

        if (msg.command == MidiConstants.STATUS_NOTE_ON) {
            is_on = true;
        } else if (msg.command == MidiConstants.STATUS_NOTE_OFF) {
            is_on = false;
        } else {
            // message isn't note
            return null;
        }

        if (msg.data.length < (msg.offset + 3)) {
            ML.err(TAG, "MidiMessage data is invalid, MidiMessage:" + msg);
            return null;
        }

        byte pitch = msg.data[msg.offset + 1], velocity = msg.data[msg.offset + 2];

        // fix for bad keyboard which dont send STATUS_OFF
        if ((velocity == 0) && is_on) {
            is_on = false;
        }

        return new MidiNoteStatus(is_on, channel, pitch, velocity, msg.timestamp, msg.java_timestamp);
    }

    public MidiNoteStatus(boolean isOn, byte channel, byte pitch, byte velocity, long timestamp, long java_timestamp) {
        this.channel = channel;
        this.pitch = pitch;
        this.velocity = velocity;
        this.isOn = isOn;
        this.timestamp = timestamp;
        this.java_timestamp = java_timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" + (isOn ? "ON" : "OFF") +
                " (" + channel +
                ", " + pitch +
                ", " + velocity +
                ") " +
                "}";
    }
}
