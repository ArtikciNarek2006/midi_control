package com.midi_control.midi.visualizer;

import com.midi_control.midi.utils.MidiMessage;
import com.midi_control.midi.utils.MidiNote;
import com.midi_control.utils.MyBuffer;
import com.midi_control.utils.MyMath;

public interface MidiVisualizerContract {
    interface VisualizerView {
        // for presenter usage
        void clearViewDrawings();
        void setNotesBuffer(MyBuffer<MidiNote> midiNotes);
        MyBuffer<MidiNote> getNotesBuffer();

        void setFlowDirection(MidiVisualizerView.FlowDirection flowDirection);
        MidiVisualizerView.FlowDirection getFlowDirection();

        void setSlideSpeed(float pxPerSec);
        float getSlideSpeed(); // returns pxPerSec


        void setMinMaxPitches(MyMath.Cords<Byte> coefficients);
        MyMath.Cords<Byte> getMinMaxPitches();
        // for other
    }

    interface Presenter {

    }
}
