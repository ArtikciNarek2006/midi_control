package com.midi_control.midi.connector;

import android.media.midi.MidiDeviceInfo;

public interface ConnectorContract {
    interface ConPresenter{
        ConnectorView getView();
        ConnectorView setView();
        void connect(MidiDeviceInfo sourceDeviceInfo, int sourcePortIndex, MidiDeviceInfo destinationDeviceInfo, int destinationPortIndex);
    }

    interface ConView{

    }
}
