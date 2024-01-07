//TODO: add support for bluetooth midi device

package com.midi_control.midi_controllers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.midi_control.util.ML;

import java.io.IOException;

public class MidiServiceClient {
    public static final String MIDI_LOG_TAG = "MidiServiceClient";

    // public for client
    public Boolean useAutoConnect = true;
    public byte[] notes; // notes velocity arr

    // public for current device
    public String device_manufacturer = "null", device_name = "null", product_name = "null", device_version = "null";
    public int device_type = 0; // MidiDeviceInfo.[TYPE_BLUETOOTH || TYPE_USB || TYPE_VIRTUAL]
    public int device_input_count = 0, device_output_count = 0;

    // public for midiManager
    public int midi_device_count = 0;


    // private for client
    private Boolean is_midi_supported;
    private static MidiServiceClient selfInstance;
    private Context appContext;

    // private for MidiManager
    private MidiManager midiManager;
    private MidiDeviceInfo[] connectedMidiDeviceInfos = null;
    private int curr_midiDeviceInfo_index = -1;

    // private for current device
    private MidiOutputPort curr_midiOutputPort = null;
    private MidiInputPort curr_midiInputPort = null;
    private MidiDeviceInfo.PortInfo[] device_inputPorts = null, device_outputPorts = null;
    private int curr_inputPort_index = -1, curr_outputPort_index = -1;


    private class MyMidiReceiver extends MidiReceiver {
        public void onSend(byte[] data, int offset,
                           int count, long timestamp) throws IOException {
            // parse MIDI or whatever
            notes[data[2]] = data[3];
        }
    }


    // private methods for current device
    private void getCurrentDeviceProperties() {
        MidiDeviceInfo deviceInfo = this.connectedMidiDeviceInfos[this.curr_midiDeviceInfo_index];
        Bundle deviceProperties = deviceInfo.getProperties();

        this.device_type = deviceInfo.getType();
        this.device_version = deviceProperties.getString(MidiDeviceInfo.PROPERTY_VERSION);
        this.device_manufacturer = deviceProperties.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
        this.device_name = deviceProperties.getString(MidiDeviceInfo.PROPERTY_NAME);
        this.product_name = deviceProperties.getString(MidiDeviceInfo.PROPERTY_PRODUCT);
    }

    private void getAllInputOutputPorts() {
        MidiDeviceInfo deviceInfo = this.connectedMidiDeviceInfos[this.curr_midiDeviceInfo_index];
        // setting public properties
        this.device_input_count = 0;
        this.device_output_count = 0;

        MidiDeviceInfo.PortInfo[] portInfos = deviceInfo.getPorts();
        MidiDeviceInfo.PortInfo[] inputPorts = new MidiDeviceInfo.PortInfo[deviceInfo.getInputPortCount()];
        MidiDeviceInfo.PortInfo[] outputPorts = new MidiDeviceInfo.PortInfo[deviceInfo.getOutputPortCount()];
        if (portInfos.length != (inputPorts.length + outputPorts.length))
            ML.err(MIDI_LOG_TAG, "getAllInputOutputPorts(): portcount != in_port + out_port count");

        int input_i = 0, output_i = 0;
        for (MidiDeviceInfo.PortInfo portInfo : portInfos) {
            if ((input_i < inputPorts.length) && (portInfo.getType() == MidiDeviceInfo.PortInfo.TYPE_INPUT)) {
                inputPorts[input_i] = portInfo;
                input_i++;
            } else if ((output_i < outputPorts.length) && (portInfo.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT)) {
                outputPorts[output_i] = portInfo;
                output_i++;
            }
        }

        // setting public properties
        this.device_inputPorts = inputPorts;
        this.device_outputPorts = outputPorts;
        this.device_input_count = input_i;
        this.device_output_count = output_i;
        if (this.device_input_count != inputPorts.length)
            ML.err(MIDI_LOG_TAG, "getAllInputOutputPorts(): counted_input_count != api_input_count");
        if (this.device_output_count != outputPorts.length)
            ML.err(MIDI_LOG_TAG, "getAllInputOutputPorts(): counted_output_count != api_output_count");
    }

    private void logCurrentDeviceProperties() {
        String props = "Current device properties [";
        props += "name: " + this.device_name + ", ";
        props += "type: " + this.device_type + ", ";
        props += "input_count: " + this.device_input_count + ", ";
        props += "output_count: " + this.device_output_count + ", ";
        props += "ver: " + this.device_version + ", ";
        props += "manufacturer: " + this.device_manufacturer + ", ";
        props += "product: " + this.product_name + "]";
        ML.log(MIDI_LOG_TAG, props);
    }

    private void resetConnectedDevice() {
        this.clearNotes();

        // reset properties
        this.device_input_count = 0;
        this.device_output_count = 0;
        this.device_type = 0;
        this.device_version = "null";
        this.device_manufacturer = "null";
        this.device_name = "null";
        this.product_name = "null";

        curr_inputPort_index = -1;
        curr_outputPort_index = -1;
        device_inputPorts = null;
        device_outputPorts = null;
    }

    private void openDeviceOutputPort() {
        if (curr_midiOutputPort != null) {
            curr_midiOutputPort.connect(new MyMidiReceiver());
        } else {
            ML.warn(MIDI_LOG_TAG, "openDeviceOutputPort(): Current midiOutputPort is null.");
        }
    }

    public void connectDevice(int midi_device_info_index, int inputPort_index, int outputPort_index) {
        curr_midiOutputPort = null;
        curr_midiInputPort = null;
        if(midi_device_count == 0) return;

        if ((midi_device_info_index >= midi_device_count) || (midi_device_info_index < 0)) {
            ML.err(MIDI_LOG_TAG, "connectDevice(): midi_device_info_index out of range: index:" + midi_device_info_index + ", device_count: " + midi_device_count);
            this.resetConnectedDevice();
            return;
        }
        if (((inputPort_index >= device_input_count) || (inputPort_index < 0)) && (device_input_count != 0)) {
            ML.err(MIDI_LOG_TAG, "connectDevice(): inputPort_index out of range: index:" + inputPort_index + ", input_count: " + device_input_count);
            this.resetConnectedDevice();
            return;
        }
        if (((outputPort_index >= device_output_count) || (outputPort_index < 0)) && (device_output_count != 0)) {
            ML.err(MIDI_LOG_TAG, "connectDevice(): outputPort_index out of range: index:" + outputPort_index + ", output_count: " + device_output_count);
            this.resetConnectedDevice();
            return;
        }

        curr_midiDeviceInfo_index = midi_device_info_index;
        curr_inputPort_index = (device_input_count != 0) ? inputPort_index : -1;
        curr_outputPort_index = (device_output_count != 0) ? outputPort_index : -1;

        this.getCurrentDeviceProperties();
        this.getAllInputOutputPorts(); // port count porperties are inited in there

        this.logCurrentDeviceProperties();

        if (this.device_type == MidiDeviceInfo.TYPE_USB) {
            midiManager.openDevice(
                    connectedMidiDeviceInfos[curr_midiDeviceInfo_index],
                    new MidiManager.OnDeviceOpenedListener() {
                        @Override
                        public void onDeviceOpened(MidiDevice device) {
                            if (device == null) {
                                ML.err(MIDI_LOG_TAG, "connectDevice(): Current midi device is null.");
                                curr_midiOutputPort = null;
                                curr_midiInputPort = null;
                            } else {
                                if (curr_outputPort_index != -1) {
                                    curr_midiOutputPort = device.openOutputPort(device_outputPorts[curr_outputPort_index].getPortNumber());
                                    openDeviceOutputPort();
                                }
                                if (curr_inputPort_index != -1) {
//                                    openDeviceInputPort();
                                }
                            }
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        } else if (this.device_type == MidiDeviceInfo.TYPE_VIRTUAL) {
            // TODO: open virtual midi device;
        } else if (this.device_type == MidiDeviceInfo.TYPE_BLUETOOTH) {
            // TODO: open bluetooth midi device;
        } else {
            ML.err(MIDI_LOG_TAG, "connectDevice(): Unknown midi device type.");
        }

        ML.log(MIDI_LOG_TAG, "connectDevice(): deviceInfoIndex: " + curr_midiDeviceInfo_index + ", inputPort: " + curr_inputPort_index + ", outputPort: " + curr_outputPort_index);
    }

    private void autoConnect() {
        if ((midi_device_count > 0) && (device_input_count > 0) && (device_output_count > 0)) {
            this.connectDevice(0, 0, 0);
            ML.log(MIDI_LOG_TAG, "autoConnect(): device: 0, input: 0, output: 0");
        }
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
        if(this.useAutoConnect){
            autoConnect();
        }
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
