package com.midi_control.midi.live_visualizer;

import com.midi_control.midi.utils.MidiMessage;
import com.midi_control.midi.utils.MidiNote;
import com.midi_control.midi.visualizer.MidiVisualizerContract;
import com.midi_control.midi.visualizer.MidiVisualizerView;
import com.midi_control.utils.ML;
import com.midi_control.utils.MyBuffer;

public class LiveVisualizerPresenter implements MidiVisualizerContract.Presenter, LiveVisualizerContract.LiveVisPresenter {
    public static final String TAG = "LiveVisualizerPresenter";
    public static int MIDI_MESSAGES_BUFFER_LENGTH = 1000;

    public MyBuffer<MidiNote> midiNotesBuffer = new MyBuffer<>(MIDI_MESSAGES_BUFFER_LENGTH);
    private MidiVisualizerView visView;


    public MidiVisualizerView getVisView() {
        return visView;
    }

    public void setVisView(MidiVisualizerView visView) {
        this.visView = visView;
    }

    private void updateViewsNotesBuffer() {
        if(visView != null) {
            // TODO: send only visible notes
            visView.setNotesBuffer(midiNotesBuffer);
        }else{
            ML.log(TAG, "updateViewsNotesBuffer() visView is NULL");
        }
    }

    @Override
    public void receiveMidiMessage(MidiMessage midiMessage) {
        if (midiMessage != null) {
            if (midiMessage.noteStatus != null) {
                ML.log(TAG, "send update");
                boolean is_new_note_status = true;
                for (MidiNote note : midiNotesBuffer.getLinkedList().toArray(new MidiNote[0])) {
                    if (note != null) {
                        if (note.update(midiMessage.noteStatus)) {
                            is_new_note_status = false;
                            break;
                        }

                    }
                }

                if(is_new_note_status) {
                    midiNotesBuffer.pushToStart(new MidiNote(midiMessage.noteStatus));
                }
                updateViewsNotesBuffer();
            }
        } else {
            ML.warn(TAG, "receiveMidiMessage() arg<midiMessage> is NULL");
        }
    }
}
