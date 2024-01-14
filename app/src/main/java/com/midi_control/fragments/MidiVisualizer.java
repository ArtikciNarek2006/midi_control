package com.midi_control.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.midi_control.R;
import com.midi_control.midi_controllers.MidiSource;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MidiVisualizer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MidiVisualizer extends Fragment {
    // params values
    public static final int FLOW_DIRECTION_UP = 0, FLOW_DIRECTION_DOWN = 1;

    public static int validateFlowDirection(int flow_direction) {
        switch (flow_direction) {
            case FLOW_DIRECTION_UP:
            case FLOW_DIRECTION_DOWN:
                return flow_direction;
            default:
                return FLOW_DIRECTION_UP;
        }
    }

    private static final String NOTES_SOURCE_EXTRA_KEY = "notes_source";

    // public

    public int flow_direction;

    public Boolean show_vertical_separators = true;

    public float speed_pps = 50f; // speed pixels per second


    MidiSource notes_source;


    public MidiVisualizer(int flow_direction, float speed_pps) {
        this.flow_direction = validateFlowDirection(flow_direction);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <before> put to extra:
     * <key>: <value>
     * midi_source: obj of MidiOutput
     *
     * @return A new instance of fragment MidiVisualizer.
     */
    public static MidiVisualizer newInstance(int flow_direction, float speed_y_pps) {
        MidiVisualizer fragment = new MidiVisualizer(flow_direction, speed_y_pps);
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = requireActivity().getIntent();
        notes_source = (MidiSource) i.getSerializableExtra(NOTES_SOURCE_EXTRA_KEY);

        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_midi_visualizer, container, false);
    }
}