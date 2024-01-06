package com.midi_control.midi_controllers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.midi_control.util.ML;

import java.io.IOException;

public class MidiDeviceController {
    private static MidiDeviceController mInstance;
    public byte[] notes;

    public String manufacturer;
    Context ctx;
    public MidiManager midiManager;
    public MidiDeviceInfo[] midiDeviceInfos;

    public void openOutput(MidiDeviceInfo info, int index) {
        ML.w("MIDI openPort(info:" + String.valueOf(info) + ", index:" + String.valueOf(index) + ");");
        midiManager.openDevice(info, new MidiManager.OnDeviceOpenedListener() {
            @Override
            public void onDeviceOpened(MidiDevice device) {
                if (device == null) {
                    ML.e("could not open device " + info);
                } else {
                    class MyReceiver extends MidiReceiver {
                        public void onSend(byte[] data, int offset,
                                           int count, long timestamp) throws IOException {
                            // parse MIDI or whatever
//                            String text = "";
//                            text += "offset: " + String.valueOf(offset) + "; ";
//                            text += "count: " + String.valueOf(count) + "; ";
//                            text += "timestamp: " + String.valueOf(timestamp) + "; ";
//                            text += "deviceSend: data: [";
//                            for (byte datum : data) {
//                                text += String.valueOf(datum) + ", ";
//                            }
//                            text += "];";
//                            ML.l(text);

                            notes[data[2]] = data[3];
                            StringBuilder notes_text = new StringBuilder("notes: [");
                            for(int i = 0; i < 127; i++){
                                notes_text.append(String.valueOf(notes[i])).append(",");
                            }
                            notes_text.append("]");
                            ML.l(notes_text.toString());
                        }
                    }

                    MidiOutputPort outputPort = device.openOutputPort(index);
                    if(outputPort != null)
                        outputPort.connect(new MyReceiver());
                    else{
                    }
                }
            }
        }, new Handler(Looper.getMainLooper()));
    }

    private void init_midi() {
        midiManager = (MidiManager) this.ctx.getSystemService(Context.MIDI_SERVICE);
        midiDeviceInfos = midiManager.getDevices();

        midiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
            public void onDeviceAdded(MidiDeviceInfo info) {
                int numInputs = info.getInputPortCount();
                int numOutputs = info.getOutputPortCount();

                Bundle properties = info.getProperties();
                String manufacturer = properties.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
                ML.l("device manufacturer: " + manufacturer);
                ML.l("midi inputs count:" + String.valueOf(numInputs) + "; midi outputs count:" + String.valueOf(numOutputs) + "; ");

                MidiDeviceInfo.PortInfo[] portInfos = info.getPorts();


                ML.l("midi portInfos:" + String.valueOf(portInfos[0]));

                String portName = portInfos[0].getName();

                openOutput(info, 0);
            }

            public void onDeviceRemoved(MidiDeviceInfo info) {
            }
        }, new android.os.Handler(Looper.getMainLooper()));
    }

    private MidiDeviceController(Context ctx) {
        this.ctx = ctx;
        if (ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            notes = new byte[127];
            init_midi();
        } else {
            ML.e("MIDI RUNTIME NOT SUPPORTED");
        }
    }

    public static MidiDeviceController getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new MidiDeviceController(ctx);
        }
        return mInstance;
    }
}
