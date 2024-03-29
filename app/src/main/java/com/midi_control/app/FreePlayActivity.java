package com.midi_control.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.midi_control.R;
import com.midi_control.databinding.ActivityFreePlayBinding;
import com.midi_control.midi.MyMidiController;

public class FreePlayActivity extends AppCompatActivity {

    public MyMidiController myMidiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.midi_control.databinding.ActivityFreePlayBinding binding = ActivityFreePlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton main_menu_btn = findViewById(R.id.main_menu_btn);

        myMidiController = MyMidiController.getInstance(this);
        if(myMidiController != null){
            myMidiController.setState(MyMidiController.State.LiveVisualizer, this, R.id.visualizerView);
        }


        main_menu_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
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