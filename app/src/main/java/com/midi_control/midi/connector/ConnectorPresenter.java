package com.midi_control.midi.connector;

import android.media.midi.MidiDeviceInfo;

public class ConnectorPresenter implements ConnectorContract.ConPresenter{
    @Override
    public ConnectorView getView() {
        return null;
    }

    @Override
    public ConnectorView setView() {
        return null;
    }

    @Override
    public void connect(MidiDeviceInfo sourceDeviceInfo, int sourcePortIndex, MidiDeviceInfo destinationDeviceInfo, int destinationPortIndex) {

    }
}
