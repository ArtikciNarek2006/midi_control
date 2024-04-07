package com.midi_control.midi_tiles.midi.live_visualizer;

import com.midi_control.midi_tiles.midi.utils.MidiMessage;

public interface LiveVisualizerContract {
    interface LiveVisPresenter{
        void receiveMidiMessage(MidiMessage midiMessage);
    }

    interface LiveVisReceiver{

    }
}
