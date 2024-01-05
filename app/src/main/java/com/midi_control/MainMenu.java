package com.midi_control;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.midi_control.settings.Settings;
import com.midi_control.settings_db.DbClient;

import java.util.Calendar;
import java.util.Date;

public class MainMenu extends AppCompatActivity {
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ML.w("CURRENT ACTIVITY: MainMenu");
        settings = Settings.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button aaa = findViewById(R.id.aaa);
        aaa.setOnClickListener(new View.OnClickListener() {
            public int i = 0;

            public void show_text(){
                TextView tv = findViewById(R.id.tv);
                String a = "";
                a += "name: " + settings.get_param("name", "No Name");
                a += "; susname: " + settings.get_param("susname", "No Name");
                a += "; surname: " + settings.get_param("surname", "No Name");
                tv.setText(a);
            }
            @Override
            public void onClick(View v) {
                Date currentTime = Calendar.getInstance().getTime();
                if(i % 3 == 0)
                    settings.set_param("name", currentTime.toString());
                else if(i % 3 == 1)
                    settings.set_param("susname", currentTime.toString());
                else if(i % 3 == 2)
                    settings.set_param("surname", currentTime.toString());
                i++;
                show_text();
            }
        });
    }
}