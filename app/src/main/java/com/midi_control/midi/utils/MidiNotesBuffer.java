package com.midi_control.midi.utils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MidiNotesBuffer<T> {
    public static final String TAG = "MyBuffer<T>";
    public static final int DEFAULT_SIZE = 20;
    private MidiNote[] buffer;
    public int size;
    public MidiNotesBuffer(){
        buffer = new MidiNote[DEFAULT_SIZE];
        size = DEFAULT_SIZE;
    }

    public MidiNotesBuffer(int size){
        if(size > 0){
            buffer = new MidiNote[size];
            this.size = size;
        }
    }

    public MidiNote[] getBufferCopy(){
        // TODO: PERF if everything right return original
        return Arrays.copyOf(buffer, size);
    }
    public void setBuffer(MidiNote[] buffer){
        this.buffer = buffer;
    }

    public MidiNote getI(int index){
        return buffer[index];
    }

    public void update_notes(MidiNoteStatus sts){
        if(sts != null) {
            for (int i = 0; i < size; i++) {
//                buffer[i] = buffer[i].update(sts);
                if(buffer[i] == null){

                }
            }
        }
    }

}
