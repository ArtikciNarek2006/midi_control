package com.midi_control.midi.live_visualizer;

import com.midi_control.midi.utils.MidiMessage;

public interface LiveVisualizerContract {
    interface LiveVisPresenter{
        void receiveMidiMessage(MidiMessage midiMessage);
    }

    interface LiveVisReceiver{

    }
}
