package com.midi_control.midi_tiles.app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.midi_control.midi_tiles.R;
import com.midi_control.midi_tiles.midi.MyMidiController;
import com.midi_control.midi_tiles.midi.preference.ConnectorButtonsPreference;
import com.midi_control.midi_tiles.midi.preference.ConnectorPreference;
import com.midi_control.midi_tiles.utils.ML;
import com.midi_control.midi_tiles.utils.SettingsContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        setTitle(R.string.title_activity_settings);
                    }
                });

        // using toolbar as ActionBar
        setSupportActionBar(findViewById(R.id.settings_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, @NonNull Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(), Objects.requireNonNull(pref.getFragment()));
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    // Settings main menu == HeaderFragment
    public static class HeaderFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey);
        }
    }

    public static class MidiConnectorsFragment extends PreferenceFragmentCompat {
        private static final String TAG = "MidiConnectorsFragment";
        PreferenceCategory free_play_cat;
        ConnectorButtonsPreference freePlay_buttons;

        MyMidiController myMidiController;
        MyMidiController.State old_state;

        protected static ArrayList<String[][]> new_freePlay_cons = new ArrayList<>();
        private static int pref_i = 0;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.midi_connector_preference, rootKey);
            myMidiController = MyMidiController.getInstance(requireActivity());
            if (myMidiController != null) {
                old_state = myMidiController.currentState;
                myMidiController.setState(MyMidiController.State.UNDEFINED, requireActivity(), null);
            }

            new_freePlay_cons = new ArrayList<>();
            free_play_cat = findPreference("free_play_cat");
            assert free_play_cat != null;

            freePlay_buttons = new ConnectorButtonsPreference(
                    free_play_cat.getContext(), "freePlay_buttons", free_play_cat, SettingsContainer.getDefaultConnectionsFreePlay()
            );
            free_play_cat.addPreference(freePlay_buttons);

            String[][][] cons_free_play = SettingsContainer.getConnectionsFreePlay();
            createConnections(requireContext(), cons_free_play, free_play_cat);
        }

        public static void createConnector(Context context, String[][] in_out, @NonNull PreferenceCategory cat) {
            Preference pref = new ConnectorPreference(context, in_out);
            pref.setLayoutResource(R.layout.connector_preference_layout);
            pref.setKey("pref" + pref_i++);
            cat.addPreference(pref);
        }

        public static void createConnections(Context context, @NonNull String[][][] cons, @NonNull PreferenceCategory cat) {
            for (String[][] strings : cons) {
                createConnector(context, strings, cat);
            }
        }

        public static void resetConnections_freePlay() {
            new_freePlay_cons = new ArrayList<>();
        }


        /**
         * @param con connection String[2][4]
         * @return boolean is added successfully
         */
        public static boolean finaliseNewConnection_freePlay(@NonNull String[][] con) {
            if (con.length == 2 && con[0].length == 4 && con[1].length == 4)
                new_freePlay_cons.add(con);
            else
                return false;
            return true;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ML.log(TAG, "port cons changed from: " + Arrays.deepToString(SettingsContainer.getConnectionsFreePlay()));
            SettingsContainer.setConnectionsFreePlay(new_freePlay_cons.toArray(new String[0][0][0]));
            ML.log(TAG, "port cons changed to: " + Arrays.deepToString(SettingsContainer.getConnectionsFreePlay()));
        }
    }

    public static class KeyboardVizSettings extends PreferenceFragmentCompat {
        public static final String TAG = "KeyboardVizSettings";
        SeekBarPreference slide_speed, min_pitch, num_keys;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.keyboard_viz_settings_preferences, rootKey);
            slide_speed = findPreference("visualiser_slide_speed");
            min_pitch = findPreference("keyboard_min_pitch");
            num_keys = findPreference("keyboard_num_notes");

            slide_speed.setMin(50);
            slide_speed.setMax(1000);
            slide_speed.setValue(SettingsContainer.visViewSlideSpeed.intValue());

            min_pitch.setUpdatesContinuously(true);
            min_pitch.setSeekBarIncrement(12);
            min_pitch.setMin(0);
            min_pitch.setMax(121 - SettingsContainer.keyboardNumKeys);
            min_pitch.setValue(SettingsContainer.keyboardMinPitch);

            num_keys.setUpdatesContinuously(true);
            num_keys.setSeekBarIncrement(12);
            num_keys.setMin(1);
            num_keys.setMax(121 - SettingsContainer.keyboardMinPitch);
            num_keys.setValue(SettingsContainer.keyboardNumKeys);

            slide_speed.setShowSeekBarValue(true);
            min_pitch.setShowSeekBarValue(true);
            num_keys.setShowSeekBarValue(true);

            min_pitch.setOnPreferenceChangeListener((preference, newValue) -> {
                int nv = (int) newValue / 12 * 12;
                min_pitch.setValue(nv);
                num_keys.setMax(121 - nv);
                return false;
            });

            num_keys.setOnPreferenceChangeListener((preference, newValue) -> {
                int nv = (int) newValue / 12 * 12 + 1;
                num_keys.setValue(nv);
                min_pitch.setMax(121 - nv);
                return false;
            });
        }

        @Override
        public void onDetach() {
            super.onDetach();
            SettingsContainer.setVisViewSlideSpeed((float) slide_speed.getValue());
            SettingsContainer.setKeyboardMinPitch(min_pitch.getValue());
            SettingsContainer.setKeyboardNumKeys(num_keys.getValue());
        }
    }
}