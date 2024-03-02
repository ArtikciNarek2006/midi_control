package com.midi_control.midi.utils;

import androidx.annotation.NonNull;

import com.midi_control.utils.ML;

public class MidiNote {
    public static final String TAG = "MidiNote";

    public byte channel, pitch, velocity;
    public long timestamp, java_timestamp;
    public Long local_duration;
    public boolean isOn, isComplete = false;

    public MidiNote(@NonNull MidiNoteStatus sts){
        this.channel = sts.channel;
        this.pitch = sts.pitch;
        this.velocity = sts.velocity;
        this.isOn = sts.isOn;
        this.timestamp = sts.timestamp;
        this.java_timestamp = sts.java_timestamp;
        this.local_duration = null;
    }

    public MidiNote(boolean isOn, byte channel, byte pitch, byte velocity, long timestamp, long java_timestamp) {
        this.channel = channel;
        this.pitch = pitch;
        this.velocity = velocity;
        this.isOn = isOn;
        this.timestamp = timestamp;
        this.java_timestamp = java_timestamp;
        this.local_duration = null;
    }

    // returns true when status is for this note
    public boolean update(@NonNull MidiNoteStatus sts) {
        if (!isComplete) {
            if (sts.channel == channel && sts.pitch == pitch) {
                if (sts.isOn) {
                    this.timestamp = sts.timestamp;
                    this.java_timestamp = sts.java_timestamp;
                    this.local_duration = null;
                } else if (this.isOn) {
                    this.local_duration = sts.java_timestamp - this.java_timestamp;
                    isComplete = true;
                }
                ML.log(TAG, "this: " + this + "; sts: " + sts);
                this.isOn = sts.isOn;
                return true;
            }
        }
        return false;
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
