package com.midi_control.midi;

import android.app.Activity;
import android.content.Context;
import android.media.midi.MidiManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.midi_control.midi.live_visualizer.LiveVisualizerPresenter;
import com.midi_control.midi.services.MidiLiveVisualizerService;
import com.midi_control.midi.visualizer.MidiVisualizerView;
import com.midi_control.utils.ML;
import com.midi_control.utils.MidiUtils;

import java.io.Serializable;

public class MyMidiController implements Serializable {
    public static final String TAG = "MyMidiManager";
    private static MyMidiController instance;

    @Nullable
    public static MyMidiController getInstance(Activity ctx) {
        if (MidiUtils.midiSupported(ctx)) {
            if (instance == null) {
                instance = new MyMidiController(ctx);
            }
            ML.log(TAG, "MIDI is supported.");
            return instance;
        } else {
            ML.err(TAG, "MIDI is NOT supported.");
            return null;
        }
    }

    // non static
    public MidiManager midiManager;
    public MidiVisualizerView visView;

    public MyMidiController(@NonNull Activity ctx) {
        midiManager = (MidiManager) ctx.getSystemService(Context.MIDI_SERVICE);
        init_liveVisualizerService();
    }
    private void init_liveVisualizerService(){
        LiveVisualizerPresenter presenter = new LiveVisualizerPresenter();
        presenter.setVisView(visView);
        MidiLiveVisualizerService.setPresenter(presenter);
    }

    public void setVisView(@NonNull Activity ctx, int visViewId){
        this.visView = ctx.findViewById(visViewId);
        if(visView != null){
            MidiLiveVisualizerService.getPresenter().setVisView(visView);
        }
    }
}
