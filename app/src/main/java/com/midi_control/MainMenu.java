package com.midi_control;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.midi_control.midi_controllers.MidiDeviceController;
import com.midi_control.settings.Settings;
import com.midi_control.util.ML;


public class MainMenu extends AppCompatActivity {
    Settings settings;

    MidiDeviceController mdc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ML.w("CURRENT ACTIVITY: MainMenu");
        settings = Settings.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mdc = MidiDeviceController.getInstance(this);
    }
}