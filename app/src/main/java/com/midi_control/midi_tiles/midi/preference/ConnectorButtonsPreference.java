package com.midi_control.midi_tiles.midi.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import com.midi_control.midi_tiles.R;
import com.midi_control.midi_tiles.app.SettingsActivity;


public class ConnectorButtonsPreference extends Preference {
    Button add_btn, reset_btn;
    PreferenceCategory cat;
    String[][][] default_cons;

    public ConnectorButtonsPreference(@NonNull Context context, String key, PreferenceCategory category, String[][][] default_cons) {
        super(context);
        setKey(key);
        setOrder(100);
        setLayoutResource(R.layout.button_layout_settings);
        cat = category;
        this.default_cons = default_cons;
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View itemView = holder.itemView;
        reset_btn = itemView.findViewById(R.id.reset_connector_btn);
        add_btn = itemView.findViewById(R.id.add_connector_btn);

        add_btn.setOnClickListener(v -> {
            SettingsActivity.MidiConnectorsFragment.createConnector(getContext(), null, cat);
        });

        reset_btn.setOnClickListener(v -> {
            cat.removeAll();
            SettingsActivity.MidiConnectorsFragment.resetConnections_freePlay();
            SettingsActivity.MidiConnectorsFragment.createConnections(getContext(), default_cons, cat);
            cat.addPreference(this);
        });
    }

    public ConnectorButtonsPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrder(100);
        setLayoutResource(R.layout.button_layout_settings);
    }
}
