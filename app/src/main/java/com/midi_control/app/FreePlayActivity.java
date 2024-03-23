package com.midi_control.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.midi_control.R;
import com.midi_control.databinding.ActivityFreePlayBinding;
import com.midi_control.midi.MyMidiController;
import com.midi_control.midi.visualizer.MidiVisualizerView;

public class FreePlayActivity extends AppCompatActivity {

    public MyMidiController myMidiController;
    private ActivityFreePlayBinding binding;
    private ImageButton main_menu_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFreePlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        main_menu_btn = findViewById(R.id.main_menu_btn);

        myMidiController = MyMidiController.getInstance(this);
        if(myMidiController != null){
            myMidiController.setVisView(this, R.id.visualizerView);
        }


        main_menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}