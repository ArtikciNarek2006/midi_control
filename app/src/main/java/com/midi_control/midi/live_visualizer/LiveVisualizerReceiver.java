package com.midi_control.midi.live_visualizer;

import static com.midi_control.utils.MyMath.NANOS_PER_MILLISECOND;
import static com.midi_control.utils.MyMath.NANOS_PER_SECOND;

import android.media.midi.MidiReceiver;

import com.midi_control.midi.utils.MidiMessage;
import com.midi_control.utils.ML;
import com.midi_control.utils.MidiUtils;

import java.io.IOException;

public class LiveVisualizerReceiver extends MidiReceiver implements LiveVisualizerContract.LiveVisReceiver {
    public static final String TAG = "LiveVisualizerReceiver";
    private long mLastTimeStamp = 0, mStartTime;
    public LiveVisualizerPresenter presenter;

    public LiveVisualizerReceiver(LiveVisualizerPresenter presenter){
        mStartTime = System.nanoTime();
        this.presenter = presenter;
    }

    @Override
    public void onSend(byte[] msg, int offset, int count, long timestamp) throws IOException {
        String log_line = "{";

        long monoTime = timestamp - mStartTime;
        long delayTimeNanos = timestamp - System.nanoTime();
        int delayTimeMillis = (int) (delayTimeNanos / NANOS_PER_MILLISECOND);
        double seconds = (double) monoTime / NANOS_PER_SECOND;

        if (timestamp < mLastTimeStamp) {
            log_line += "\"out of timeline order.\" ";
        }

        mLastTimeStamp = timestamp;
        MidiMessage midiMessage = new MidiMessage(msg, offset, count, timestamp);
        log_line += "MidiMessage: " + midiMessage;
        log_line += "\n wrap:" + MidiUtils.receiverDataWrapper(msg, offset, count, timestamp);
        log_line += "}";

        ML.log(TAG, log_line);

        // send data to presenter
        presenter.receiveMidiMessage(midiMessage);
    }
}
