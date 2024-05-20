package com.midi_control.midi_tiles.midi.port_selector;

import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import androidx.annotation.NonNull;


/**
 * Manages a Spinner for selecting a MidiInputPort.
 */
public class MidiInputPortSelector extends MidiPortSelector {
    public static final String TAG = "MidiInputPortSelector";

    public MidiInputPortSelector(MidiManager midiManager, Spinner spinner) {
        super(midiManager, spinner, MidiDeviceInfo.PortInfo.TYPE_INPUT);
    }

    @Override
    public void onPortSelected(@NonNull final MidiPortWrapper wrapper) {
        close();
    }

    @Override
    public void onClose() {
        super.onClose();
    }
    public ArrayAdapter<MidiPortWrapper> getArrayAdapter() {
        return this.mAdapter;
    }
}
