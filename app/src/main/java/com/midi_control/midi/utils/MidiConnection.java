package com.midi_control.midi.utils;

import static com.midi_control.utils.MyUtils.stringComp;

import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.midi_control.utils.ML;
import com.mobileer.miditools.MidiPortConnector;

import java.io.IOException;

public class MidiConnection {
    public static final String TAG = "MidiConnection";

    public enum Status {
        UNKNOWN_SOURCE("UNKNOWN_SOURCE"), UNKNOWN_DESTINATION("UNKNOWN_DESTINATION"), INVALID_SOURCE_PORT("INVALID_SOURCE_PORT"), INVALID_DESTINATION_PORT("INVALID_DESTINATION_PORT"), OK("OK");
        public final String text;

        Status(String val) {
            text = val;
        }

        @NonNull
        @Override
        public String toString() {
            return text;
        }
    }

    private static int connection_counter = 0;
    public final int uid = connection_counter++;
    public MidiPortConnector midiPortConnector;

    public Status status;

    public boolean isOpen = false;
    public final String srcManuf, srcProduct, srcName, destManuf, destProduct, destName;
    public final String srcPortName, destPortName;
    public int srcPortNumber = -1, destPortNumber = -1;

    public MidiConnection(@NonNull MidiManager midiManager, String srcManuf, String srcProduct, String srcName, String srcPortName,
                          String destManuf, String destProduct, String destName, String destPortName) {
        this.srcManuf = srcManuf;
        this.srcProduct = srcProduct;
        this.srcName = srcName;
        this.destManuf = destManuf;
        this.destProduct = destProduct;
        this.destName = destName;
        this.srcPortName = srcPortName;
        this.destPortName = destPortName;


        MidiDeviceInfo src_info = null, dest_info = null;
        String dManuf, dProduct, dName;
        for (MidiDeviceInfo info : midiManager.getDevices()) {
            dManuf = info.getProperties().getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
            dProduct = info.getProperties().getString(MidiDeviceInfo.PROPERTY_PRODUCT);
            dName = info.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
            if (stringComp(dManuf, srcManuf) && stringComp(dProduct, srcProduct) && stringComp(dName, srcName))
                src_info = info;
            if (stringComp(dManuf, destManuf) && stringComp(dProduct, destProduct) && stringComp(dName, destName))
                dest_info = info;
        }

        if (src_info == null) {
            status = MidiConnection.Status.UNKNOWN_SOURCE;
            return;
        }
        if (dest_info == null) {
            status = Status.UNKNOWN_DESTINATION;
            return;
        }

        // check src port
        boolean isSrcPortOk = false;
        MidiDeviceInfo.PortInfo[] srcPortInfos = src_info.getPorts();
        for (MidiDeviceInfo.PortInfo portInfo : srcPortInfos) {
            if (stringComp(portInfo.getName(), srcPortName) && (portInfo.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT)) {
                srcPortNumber = portInfo.getPortNumber();
                isSrcPortOk = true;
                break;
            }
        }
        if (!isSrcPortOk) {
            status = Status.INVALID_SOURCE_PORT;
            return;
        }

        // check dest port
        boolean isDestPortOk = false;
        MidiDeviceInfo.PortInfo[] destPortInfos = dest_info.getPorts();
        for (MidiDeviceInfo.PortInfo portInfo : destPortInfos) {
            if (stringComp(portInfo.getName(), destPortName) && (portInfo.getType() == MidiDeviceInfo.PortInfo.TYPE_INPUT)) {
                destPortNumber = portInfo.getPortNumber();
                isDestPortOk = true;
                break;
            }
        }
        if (!isDestPortOk) {
            status = Status.INVALID_DESTINATION_PORT;
            return;
        }

        status = Status.OK;

        midiPortConnector = new MidiPortConnector(midiManager);
        midiPortConnector.connectToDevicePort(src_info, srcPortNumber, dest_info, destPortNumber);
        isOpen = true;
    }

    public void safeClose() {
        if (isOpen) {
            try {
                midiPortConnector.close();
                isOpen = true;
            } catch (IOException e) {
                ML.err(TAG, "safeClose(): cant close connection:" + this);
            }
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof MidiConnection) {
            MidiConnection o = (MidiConnection) obj;
            return (stringComp(srcManuf, o.srcManuf) &&
                    stringComp(srcProduct, o.srcProduct) &&
                    stringComp(srcName, o.srcName) &&
                    stringComp(destManuf, o.destManuf) &&
                    stringComp(destProduct, o.destProduct) &&
                    stringComp(destName, o.destName) &&
                    (srcPortNumber == o.srcPortNumber) &&
                    stringComp(srcPortName, o.srcPortName) &&
                    (destPortNumber == o.destPortNumber) &&
                    stringComp(destPortName, o.destPortName) &&
                    (isOpen == o.isOpen)
            );
        } else {
            return false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "MidiConnection{" +
                "uid=" + uid +
                ", status=" + status +
                ", srcManuf='" + srcManuf + '\'' +
                ", srcProduct='" + srcProduct + '\'' +
                ", srcName='" + srcName + '\'' +
                ", destManuf='" + destManuf + '\'' +
                ", destProduct='" + destProduct + '\'' +
                ", destName='" + destName + '\'' +
                ", srcPortNumber=" + srcPortNumber +
                ", srcPortName=" + srcPortName +
                ", destPortNumber=" + destPortNumber +
                ", destPortName=" + destPortName +
                ", isOpen=" + isOpen +
                '}';
    }
}
