package com.midi_control;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.midi_control.settings_db.DbClient;

public class MainMenu extends AppCompatActivity {
    private DbClient dbClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ML.w("CURRENT ACTIVITY: MainMenu");
        dbClient = DbClient.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


    }
}