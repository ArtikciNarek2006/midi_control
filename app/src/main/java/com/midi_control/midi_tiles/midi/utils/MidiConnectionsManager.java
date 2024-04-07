package com.midi_control.midi_tiles.midi.utils;


import static com.midi_control.midi_tiles.utils.MyUtils.stringComp;

import android.media.midi.MidiManager;


import java.util.ArrayList;

public class MidiConnectionsManager {
    private final ArrayList<MidiConnection> midiConnections = new ArrayList<>();
    private final MidiManager midiManager;

    public MidiConnectionsManager(MidiManager midiManager) {
        this.midiManager = midiManager;
    }

    public MidiConnection.Status createConnection(String srcManuf, String srcProduct, String srcName, String srcPortName,
                                                  String destManuf, String destProduct, String destName, String destPortName) {
        MidiConnection mc = new MidiConnection(midiManager, srcManuf, srcProduct, srcName, srcPortName, destManuf, destProduct, destName, destPortName);
        midiConnections.add(mc);
        return mc.status;
    }

    public void closeAll() {
        for (MidiConnection mc : midiConnections)
            mc.safeClose();
    }

    public void closeByUid(int uid){
        for (MidiConnection mc : midiConnections)
            if (mc.uid == uid)
                mc.safeClose();
    }

    public void closeByProps(String manuf, String product, String name){
        for (MidiConnection mc : midiConnections) {
            if (stringComp(mc.srcManuf, manuf) && stringComp(mc.srcProduct, product) && stringComp(mc.srcName, name))
                mc.safeClose();
            else if (stringComp(mc.destManuf, manuf) && stringComp(mc.destProduct, product) && stringComp(mc.destName, name))
                mc.safeClose();
        }
    }

    public MidiConnection[] getAllConnections(){
        return midiConnections.toArray(new MidiConnection[0]);
    }

    public MidiConnection[] getOpenConnections(){
        ArrayList<MidiConnection> mcs = new ArrayList<>();
        for (MidiConnection mc : midiConnections)
            if(mc.isOpen)
                mcs.add(mc);
        return mcs.toArray(new MidiConnection[0]);
    }
}
