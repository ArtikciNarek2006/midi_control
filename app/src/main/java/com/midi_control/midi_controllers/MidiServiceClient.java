//TODO: add support for bluetooth midi device

package com.midi_control.midi_controllers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import com.midi_control.util.ML;

public class MidiServiceClient {
    public static final String MIDI_LOG_TAG = "MidiServiceClient";
    public byte[] notes; // notes velocity arr
    public String device_manufacturer = "null", device_name = "null", product_name = "null", device_version = "null";

    public int device_type = 0; // MidiDeviceInfo.[TYPE_BLUETOOTH || TYPE_USB || TYPE_VIRTUAL]

    public int midi_device_count = 0, device_input_count = 0, device_output_count = 0;

    // private
    private static MidiServiceClient selfInstance;
    private Context appContext;

    private MidiManager midiManager;
    private MidiDeviceInfo[] connectedMidiDeviceInfos;
    private int curr_midiDeviceInfo_index;
    private MidiDeviceInfo.PortInfo[] device_inputPorts, device_outputPorts;
    private int curr_inputPort_index, curr_outputPort_index;
    private Boolean is_midi_supported;


    private void getCurrentDeviceProperties() {
        MidiDeviceInfo deviceInfo = connectedMidiDeviceInfos[curr_midiDeviceInfo_index];
        Bundle deviceProperties = deviceInfo.getProperties();

        device_type = deviceInfo.getType();
        device_version = deviceProperties.getString(MidiDeviceInfo.PROPERTY_VERSION);
        device_manufacturer = deviceProperties.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
        device_name = deviceProperties.getString(MidiDeviceInfo.PROPERTY_NAME);
        product_name = deviceProperties.getString(MidiDeviceInfo.PROPERTY_PRODUCT);
    }

    private void getAllInputOutputPorts() {
        MidiDeviceInfo deviceInfo = connectedMidiDeviceInfos[curr_midiDeviceInfo_index];
        // setting public properties
        device_input_count = 0;
        device_output_count = 0;

        MidiDeviceInfo.PortInfo[] portInfos = deviceInfo.getPorts();
        MidiDeviceInfo.PortInfo[] inputPorts = new MidiDeviceInfo.PortInfo[deviceInfo.getInputPortCount()];
        MidiDeviceInfo.PortInfo[] outputPorts = new MidiDeviceInfo.PortInfo[deviceInfo.getOutputPortCount()];
        if(portInfos.length != (inputPorts.length + outputPorts.length)) ML.err(MIDI_LOG_TAG, "getAllInputOutputPorts(): portcount != in_port + out_port count");

        int input_i = 0, output_i = 0;
        for (MidiDeviceInfo.PortInfo portInfo : portInfos) {
            if((input_i < inputPorts.length) && (portInfo.getType() == MidiDeviceInfo.PortInfo.TYPE_INPUT)){
                inputPorts[input_i] = portInfo;
                input_i++;
            } else if ((output_i < outputPorts.length) && (portInfo.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT)) {
                outputPorts[output_i] = portInfo;
                output_i++;
            }
        }

        device_inputPorts = inputPorts;
        device_outputPorts = outputPorts;
        // setting public properties
        device_input_count = input_i;
        device_output_count = output_i;
        if(device_input_count != inputPorts.length) ML.err(MIDI_LOG_TAG, "getAllInputOutputPorts(): counted_input_count != api_input_count");
        if(device_output_count != outputPorts.length) ML.err(MIDI_LOG_TAG, "getAllInputOutputPorts(): counted_output_count != api_output_count");
    }

    private void logCurrentDeviceProperties() {
        String props = "Current device properties [";
        props += "name: " + device_name + ", ";
        props += "type: " + device_type + ", ";
        props += "input_count: " + device_input_count + ", ";
        props += "output_count: " + device_output_count + ", ";
        props += "ver: " + device_version + ", ";
        props += "manufacturer: " + device_manufacturer + ", ";
        props += "product: " + product_name + "]";
        ML.log(MIDI_LOG_TAG, props);
    }


    public void connectCurrentDeviceOutputToClientInput() {
        // TODO:
    }

    public void connectDevice(int midi_device_info_index) {
        if ((midi_device_info_index >= midi_device_count) || (midi_device_info_index < 0)) {
            ML.err(MIDI_LOG_TAG, "connectDeviceOutputToClientInput(): device_info_index out of range: index:" + midi_device_info_index + ", device_count: " + midi_device_count);

            this.clearNotes();

            device_input_count = 0;
            device_output_count = 0;
            device_type = 0;
            device_version = "null";
            device_manufacturer = "null";
            device_name = "null";
            product_name = "null";

            curr_inputPort_index = 0;
            curr_outputPort_index = 0;
            device_inputPorts = null;
            device_outputPorts = null;
            return;
        }

        curr_midiDeviceInfo_index = midi_device_info_index;

        this.getCurrentDeviceProperties();
        this.getAllInputOutputPorts(); // port count porperties are inited in there

        this.logCurrentDeviceProperties();

        this.connectCurrentDeviceOutputToClientInput();
    }

    private void autoConnect() {
        // TODO: auto device connection
        // this.connectDevice();
    }

    public void checkForDevices() {
        // get current devices list
        MidiDeviceInfo[] midiDeviceInfos;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // TODO: add support for midi 2.0
            midiDeviceInfos = (MidiDeviceInfo[]) midiManager.getDevicesForTransport(MidiManager.TRANSPORT_MIDI_BYTE_STREAM).toArray(); // support for midi ver 1.0
        } else {
            midiDeviceInfos = midiManager.getDevices();
        }

        connectedMidiDeviceInfos = midiDeviceInfos;
        midi_device_count = connectedMidiDeviceInfos.length;

        if (midi_device_count == 0) {
            ML.log(MIDI_LOG_TAG, "Currently no connected devices found.");
            return;
        }

        ML.log(MIDI_LOG_TAG, "Connected midi device count<" + String.valueOf(midi_device_count) + ">");
    }

    private void registerDeviceListener() {
        midiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
            public void onDeviceAdded(MidiDeviceInfo info) {
                ML.log(MIDI_LOG_TAG, "registerDeviceListener(): Midi device connected");
                clearNotes();
                checkForDevices();
            }

            public void onDeviceRemoved(MidiDeviceInfo info) {
                ML.log(MIDI_LOG_TAG, "registerDeviceListener(): Midi device disconnected");
                clearNotes();
                checkForDevices();
            }
        }, new android.os.Handler(Looper.getMainLooper()));
    }

    private MidiServiceClient(Context context) {
        this.appContext = context;
        is_midi_supported = MidiServiceClient.isMidiSupported(this.appContext);
        if (!is_midi_supported) {
            ML.err(MIDI_LOG_TAG, "Midi API is not supported at constructor creation time");
            return;
        }
        midiManager = (MidiManager) this.appContext.getSystemService(Context.MIDI_SERVICE);

        this.clearNotes();
        this.checkForDevices();
        this.registerDeviceListener();

        ML.log(MIDI_LOG_TAG, "MidiServiceClient initialization success.");
    }

    public void clearNotes() {
        this.notes = new byte[127];
    }

    public static MidiServiceClient getInstance(Context context) {
        if (selfInstance == null)
            selfInstance = new MidiServiceClient(context);
        return selfInstance;
    }

    private static Boolean isMidiSupported(Context ctx) {
        return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI);
    }
}
