package com.midi_control.midi.services;

import android.media.midi.MidiDeviceService;
import android.media.midi.MidiReceiver;

public class MidiSynthService extends MidiDeviceService {
    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        return new MidiReceiver[0];
    }
}
