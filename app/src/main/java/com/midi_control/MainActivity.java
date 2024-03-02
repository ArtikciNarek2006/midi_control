package com.midi_control;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.midi_control.midi.MyMidiController;

public class MainActivity extends AppCompatActivity {
    public MyMidiController myMidiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMidiController = MyMidiController.getInstance(this);
        if (myMidiController != null) {
            myMidiController.setVisView(this, R.id.visView);
        }
    }
}