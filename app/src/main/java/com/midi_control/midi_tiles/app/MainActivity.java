package com.midi_control.midi_tiles.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.midi_control.midi_tiles.R;
import com.midi_control.midi_tiles.midi.MyMidiController;

public class MainActivity extends AppCompatActivity {
    public MyMidiController myMidiController;
    private long pressedTime;
    Button fff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMidiController = MyMidiController.getInstance(this);
        assert myMidiController != null;
        myMidiController.setState(MyMidiController.State.UNDEFINED, this, null);

        Toolbar toolbar = findViewById(R.id.MainToolbar);

        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.main_activity_toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_main_activity_settings){
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            }
            return false;
        });


        //TODO: Refactor
        fff = findViewById(R.id.btn);
        fff.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FreePlayActivity.class)));
    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_toolbar, menu);
        return true;
    }
}