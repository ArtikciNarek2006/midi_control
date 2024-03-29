package com.midi_control.midi.keyboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.midi_control.R;
import com.midi_control.midi.MyMidiController;

public class MidiKeyboardFragment extends Fragment {
    public static final String TAG = "MidiKeyboardFragment";
    private MyMidiController myMidiController;

    public MidiKeyboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_midi_keyboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(myMidiController == null)
            myMidiController = MyMidiController.getInstance(requireActivity());
        MidiKeyboardView mkView = view.findViewById(R.id.midi_keyboard_view);
        if (myMidiController != null)
            myMidiController.setKeyboardView(mkView);
    }
}