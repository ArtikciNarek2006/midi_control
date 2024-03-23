package com.midi_control.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.midi_control.R;
import com.midi_control.midi.MyMidiController;

public class MainActivity extends AppCompatActivity {
    public MyMidiController myMidiController;

    Button fff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMidiController = MyMidiController.getInstance(this);

        fff = findViewById(R.id.btn);
        fff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FreePlayActivity.class);
                startActivity(intent);
            }
        });
    }
}