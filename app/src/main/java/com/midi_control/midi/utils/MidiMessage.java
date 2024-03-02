package com.midi_control.midi.utils;

import androidx.annotation.NonNull;

import com.midi_control.utils.MidiUtils;

public class MidiMessage {
    public static final String TAG = "MidiMessage";

    public long timestamp, java_timestamp;
    public int offset, count;
    public byte status_code, command, channel;
    public byte[] data;
    public MidiNoteStatus noteStatus;

    public MidiMessage(@NonNull byte[] data, int offset, int count, long timestamp) {
        this.data = data;
        this.offset = offset;
        this.count = count;
        this.timestamp = timestamp;

        this.java_timestamp = System.nanoTime();
        this.status_code = data[offset];
        this.command = MidiUtils.getCommand(status_code);
        this.channel = MidiUtils.getChannel(status_code);
        this.noteStatus = MidiNoteStatus.getInstance(this);
    }


    @NonNull
    @Override
    public String toString() {
        return MidiPrinter.formatMessage(data, offset, count);
    }
}
