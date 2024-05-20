package com.midi_control.midi_tiles.midi.preference;

import android.app.Activity;
import android.content.Context;
import android.media.midi.MidiDeviceInfo;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.midi_control.midi_tiles.R;
import com.midi_control.midi_tiles.app.SettingsActivity;
import com.midi_control.midi_tiles.midi.MyMidiController;
import com.midi_control.midi_tiles.midi.port_selector.MidiInputPortSelector;
import com.midi_control.midi_tiles.midi.port_selector.MidiOutputPortSelector;
import com.midi_control.midi_tiles.midi.port_selector.MidiPortWrapper;
import com.midi_control.midi_tiles.utils.ML;
import com.mobileer.miditools.MidiTools;

import java.util.Objects;

public class ConnectorPreference extends Preference {
    public static final String TAG = "ConnectorPreference";
    public Spinner input_spinner, output_spinner;
    public ImageButton delete_btn;
    public boolean removed_by_btn = false;
    public MidiInputPortSelector midiInputPortSelector;
    public MidiOutputPortSelector midiOutputPortSelector;

    public static MyMidiController mmc;

    public String[][] connection = null;

    public ConnectorPreference(Context context) {
        super(context);
        if (mmc == null) {
            mmc = MyMidiController.getInstance((Activity) context);
        }
    }

    public ConnectorPreference(Context context, String[][] connection) {
        super(context);
        if (mmc == null) {
            mmc = MyMidiController.getInstance((Activity) context);
        }
        this.connection = connection;
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View itemView = holder.itemView;
        input_spinner = itemView.findViewById(R.id.connector_input_spinner);
        output_spinner = itemView.findViewById(R.id.connector_output_spinner);
        delete_btn = itemView.findViewById(R.id.delete_btn);

        if (input_spinner == null || output_spinner == null || delete_btn == null) {
            ML.err(TAG, "null object; input:" + input_spinner + ", out:" + output_spinner + ", delete_btn:" + delete_btn);
            return;
        }

        midiInputPortSelector = new MidiInputPortSelector(mmc.midiManager, input_spinner);
        midiOutputPortSelector = new MidiOutputPortSelector(mmc.midiManager, output_spinner);

        if (connection != null) {
            MidiDeviceInfo output_deviceInfo = MidiTools.findDevice(mmc.midiManager, connection[0][0], connection[0][1]);
            if (output_deviceInfo == null) {
                ML.err(TAG, "onBindViewHolder: connection: output_deviceInfo is null");
                return;
            }
            MidiDeviceInfo.PortInfo outPortInfo = null;
            for (MidiDeviceInfo.PortInfo port : output_deviceInfo.getPorts()) {
                if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                    outPortInfo = port;
                }
            }
            if (outPortInfo == null) {
                ML.err(TAG, "onBindViewHolder: connection: outPortInfo is null");
                return;
            }

            MidiDeviceInfo input_deviceInfo = MidiTools.findDevice(mmc.midiManager, connection[1][0], connection[1][1]);
            if (input_deviceInfo == null) {
                ML.err(TAG, "onBindViewHolder: connection: output_deviceInfo is null");
                return;
            }
            MidiDeviceInfo.PortInfo inPortInfo = null;
            for (MidiDeviceInfo.PortInfo port : input_deviceInfo.getPorts()) {
                if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_INPUT) {
                    inPortInfo = port;
                }
            }
            if (inPortInfo == null) {
                ML.err(TAG, "onBindViewHolder: connection: inPortInfo is null");
                return;
            }

            output_spinner.setSelection(midiOutputPortSelector.getArrayAdapter().
                    getPosition(new MidiPortWrapper(output_deviceInfo, MidiDeviceInfo.PortInfo.TYPE_OUTPUT, outPortInfo.getPortNumber()))
            );

            input_spinner.setSelection(midiInputPortSelector.getArrayAdapter().
                    getPosition(new MidiPortWrapper(input_deviceInfo, MidiDeviceInfo.PortInfo.TYPE_INPUT, inPortInfo.getPortNumber()))
            );
        }

        Preference pref = this;
        delete_btn.setOnClickListener(v -> {
            removed_by_btn = true;
            Objects.requireNonNull(getParent()).removePreference(pref);
        });
    }

    public ConnectorPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (mmc == null) {
            mmc = MyMidiController.getInstance((Activity) context);
        }
        this.setLayoutResource(R.layout.connector_preference_layout);
    }

    @Override
    public void onDetached() {
        super.onDetached();
        if (!removed_by_btn) {
            MidiPortWrapper input = (MidiPortWrapper) input_spinner.getSelectedItem(), output = (MidiPortWrapper) output_spinner.getSelectedItem();
            if (input != null && output != null && input.getDeviceInfo() != null && output.getDeviceInfo() != null) {
                String[][] con = new String[2][4];
                Bundle inProps = input.getDeviceInfo().getProperties(), outProps = output.getDeviceInfo().getProperties();
                con[0][0] = outProps.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
                con[0][1] = outProps.getString(MidiDeviceInfo.PROPERTY_PRODUCT);
                con[0][2] = outProps.getString(MidiDeviceInfo.PROPERTY_NAME);
                MidiDeviceInfo.PortInfo outPortInfo = null;
                for (MidiDeviceInfo.PortInfo port : output.getDeviceInfo().getPorts()) {
                    if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                        outPortInfo = port;
                    }
                }
                if (outPortInfo == null) {
                    ML.err(TAG, "onDetached: outPortInfo is null");
                    return;
                } else {
                    con[0][3] = outPortInfo.getName();
                }

                con[1][0] = inProps.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
                con[1][1] = inProps.getString(MidiDeviceInfo.PROPERTY_PRODUCT);
                con[1][2] = inProps.getString(MidiDeviceInfo.PROPERTY_NAME);
                MidiDeviceInfo.PortInfo inPortInfo = null;
                for (MidiDeviceInfo.PortInfo port : input.getDeviceInfo().getPorts()) {
                    if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_INPUT) {
                        inPortInfo = port;
                    }
                }
                if (inPortInfo == null) {
                    ML.err(TAG, "onDetached: inPortInfo is null");
                    return;
                } else {
                    con[1][3] = inPortInfo.getName();
                }

                ML.log(TAG, "finalizeing port cons status: " + SettingsActivity.MidiConnectorsFragment.finaliseNewConnection_freePlay(con));
            }
        }

        if (midiInputPortSelector != null)
            midiInputPortSelector.close();
        if (midiOutputPortSelector != null)
            midiOutputPortSelector.close();
    }
}
