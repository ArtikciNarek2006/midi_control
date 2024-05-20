package com.midi_control.midi_tiles.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.midi_control.midi_tiles.R;
import com.midi_control.midi_tiles.databinding.ActivityFreePlayBinding;
import com.midi_control.midi_tiles.midi.MyMidiController;

public class FreePlayActivity extends AppCompatActivity {
    public MyMidiController myMidiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.midi_control.midi_tiles.databinding.ActivityFreePlayBinding binding = ActivityFreePlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        myMidiController = MyMidiController.getInstance(this);
        if (myMidiController != null) {
            myMidiController.setState(MyMidiController.State.LiveVisualizer, this, R.id.visualizerView);
        }

        Toolbar toolbar = findViewById(R.id.free_play_toolbar);

        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.free_play_activity_toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_free_play_settings){
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            }
            return false;
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.free_play_activity_toolbar, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (myMidiController != null) {
            myMidiController.setState(MyMidiController.State.LiveVisualizer, this, R.id.visualizerView);
        }
    }
}