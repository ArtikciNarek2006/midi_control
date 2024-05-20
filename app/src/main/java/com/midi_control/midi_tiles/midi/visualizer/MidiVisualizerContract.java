package com.midi_control.midi_tiles.midi.visualizer;

import com.midi_control.midi_tiles.midi.utils.MidiNote;
import com.midi_control.midi_tiles.utils.MyBuffer;
import com.midi_control.midi_tiles.utils.MyMath;

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


        void setMinMaxPitches(MyMath.Cords<Integer> coefficients);
        MyMath.Cords<Integer> getMinMaxPitches();
        // for other
    }

    interface Presenter {

    }
}
