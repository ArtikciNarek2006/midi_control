package com.midi_control.midi_tiles.midi.port_selector;

import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiManager;
import android.media.midi.MidiManager.DeviceCallback;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.midi_control.midi_tiles.midi.MyMidiController;
import com.midi_control.midi_tiles.utils.ML;

import java.util.HashSet;

/**
 * Base class that uses a Spinner to select available MIDI ports.
 */
public abstract class MidiPortSelector extends DeviceCallback {
    public static final String TAG = "MidiPortSelector";
//    private int mType = MidiDeviceInfo.PortInfo.TYPE_INPUT;
    private final int mType;
    protected ArrayAdapter<MidiPortWrapper> mAdapter;
    protected HashSet<MidiPortWrapper> mBusyPorts = new HashSet<>();
    private final Spinner mSpinner;
    protected MidiManager mMidiManager;
    private MidiPortWrapper mCurrentWrapper;

    public MidiPortSelector(MidiManager midiManager, Spinner spinner, int type) {
        mMidiManager = midiManager;
        mType = type;
        mSpinner = spinner;

        mAdapter = new ArrayAdapter<>(mSpinner.getContext(), android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAdapter.add(new MidiPortWrapper(null, 0, 0));

        mSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        mCurrentWrapper = mAdapter.getItem(pos);
                        onPortSelected(mCurrentWrapper);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        onPortSelected(null);
                        mCurrentWrapper = null;
                    }
                });
        mSpinner.setAdapter(mAdapter);

        MyMidiController.addDeviceCallback(this);

        MidiDeviceInfo[] infos = mMidiManager.getDevices();
        for (MidiDeviceInfo info : infos)
            onDeviceAdded(info);
    }

    /**
     * Set to no port selected.
     */
    public void clearSelection() {
        mSpinner.setSelection(0);
    }

    private int getInfoPortCount(final MidiDeviceInfo info) {
        return (mType == MidiDeviceInfo.PortInfo.TYPE_INPUT) ? info.getInputPortCount() : info.getOutputPortCount();
    }

    @Override
    public void onDeviceAdded(final MidiDeviceInfo info) {
        int portCount = getInfoPortCount(info);
        for (int i = 0; i < portCount; ++i) {
            MidiPortWrapper wrapper = new MidiPortWrapper(info, mType, i);
            mAdapter.add(wrapper);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDeviceRemoved(final MidiDeviceInfo info) {
        int portCount = getInfoPortCount(info);
        for (int i = 0; i < portCount; ++i) {
            MidiPortWrapper wrapper = new MidiPortWrapper(info, mType, i);
            MidiPortWrapper currentWrapper = mCurrentWrapper;
            mAdapter.remove(wrapper);
            // If the currently selected port was removed then select no port.
            if (wrapper.equals(currentWrapper)) {
                clearSelection();
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDeviceStatusChanged(final MidiDeviceStatus status) {
        // If an input port becomes busy then remove it from the menu.
        // If it becomes free then add it back to the menu.
        if (mType == MidiDeviceInfo.PortInfo.TYPE_INPUT) {
            MidiDeviceInfo info = status.getDeviceInfo();
            ML.log(TAG, "MidiPortSelector.onDeviceStatusChanged status = " + status
                    + ", mType = " + mType
                    + ", info = " + info);
            // Look for transitions from free to busy.
            int portCount = info.getInputPortCount();
            for (int i = 0; i < portCount; ++i) {
                MidiPortWrapper wrapper = new MidiPortWrapper(info, mType, i);
                if (!wrapper.equals(mCurrentWrapper)) {
                    if (status.isInputPortOpen(i)) { // busy?
                        if (!mBusyPorts.contains(wrapper)) {
                            // was free, now busy
                            mBusyPorts.add(wrapper);
                            mAdapter.remove(wrapper);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (mBusyPorts.remove(wrapper)) {
                            // was busy, now free
                            mAdapter.add(wrapper);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }

    /**
     * Implement this method to handle the user selecting a port on a device.
     */
    public abstract void onPortSelected(MidiPortWrapper wrapper);

    /**
     * Implement this method to clean up any open resources.
     */
    public void onClose() {
        onDestroy();
    }

    /**
     * Implement this method to clean up any open resources.
     */
    public void onDestroy() {
        MyMidiController.removeDeviceCallback(this);
    }

    public void close() {
        onClose();
    }
}
