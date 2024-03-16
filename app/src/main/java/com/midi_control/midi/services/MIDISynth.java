package com.midi_control.midi.services;

import android.media.midi.MidiReceiver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MIDISynth extends MidiReceiver {

    static {
        System.loadLibrary("midisynth");
    }

    private ByteBuffer mContext;

    /**
     * Constructor
     *
     * @throws IOException if not supported.
     */
    public MIDISynth() throws IOException {
        mContext = MIDISynth.open().order(ByteOrder.nativeOrder());
        if (mContext == null) {
            throw new IOException("Unsupported");
        }
    }

    /**
     * Must be called before this object is garbage collected. Safe to call more than once.
     */
    public void close() {
        if (mContext != null) {
            MIDISynth.close(mContext);
            mContext = null;
        }
    }

    /**
     * Starts the OpenSL audio stream; will have no effect if the object has already been started. May
     * not be called after close() has been called.
     *
     * @throws IOException if the stream cannot be started.
     */
    public void start() throws IOException {
        if (mContext == null) {
            throw new IllegalStateException("Stream closed.");
        }
        if (MIDISynth.start(mContext) != 0) {
            throw new IOException("Unable to start OpenSL stream.");
        }
    }

    /**
     * Stops the OpenSL audio stream; will have no effect if the object has already been started. May
     * not be called after close() has been called.
     */
    public void stop() {
        if (mContext == null) {
            throw new IllegalStateException("Stream closed.");
        }
        MIDISynth.stop(mContext);
    }

    /**
     * May not be called after close() has been called.
     *
     * @return true if the OpenSL audio stream filter is running.
     */
    public boolean isRunning() {
        if (mContext == null) {
            throw new IllegalStateException("Stream closed.");
        }
        return MIDISynth.isRunning(mContext);
    }

    public void write(byte[] data) {
        if (mContext == null) {
            throw new IllegalStateException("Stream closed.");
        }
        MIDISynth.write(mContext, data);
    }

    public int getSampleRate() {
        if (mContext == null) {
            throw new IllegalStateException("Stream closed.");
        }
        return mContext.getInt(0);
    }

    public int getBufferSize() {
        if (mContext == null) {
            throw new IllegalStateException("Stream closed.");
        }
        return mContext.getInt(4);
    }

    public int getNumberOfChannels() {
        if (mContext == null) {
            throw new IllegalStateException("Stream closed.");
        }
        return mContext.getInt(8);
    }

    @Override
    public void onSend(byte[] msg, int offset, int count, long timestamp)
            throws IOException {
        write(Arrays.copyOfRange(msg, offset, offset + count));
    }

    private static native ByteBuffer open();

    private static native void close(ByteBuffer ctx);

    private static native int start(ByteBuffer ctx);

    private static native void stop(ByteBuffer ctx);

    private static native boolean isRunning(ByteBuffer ctx);

    private static native void write(ByteBuffer ctx, byte[] data);

}