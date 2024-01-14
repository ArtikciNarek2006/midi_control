package com.midi_control.midi_controllers;

import android.graphics.Color;
import android.graphics.Paint;

public interface MidiSource {
    int[] channels_color_full_keys = {
            Color.HSVToColor(1, new float[]{(128f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(197f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(34f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(64f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(277f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(0f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(82f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(313f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(145f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(233f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(258f / 360f), (72f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(165f / 360f), (45f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(59f / 360f), (45f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(246f / 360f), (45f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(120f / 360f), (45f / 100f), (86f / 100f)}),
            Color.HSVToColor(1, new float[]{(122f / 360f), (0f / 100f), (80f / 100f)}),
    };

    float[] channels_color_half_keys = {
            Color.HSVToColor(1, new float[]{(128f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(197f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(34f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(64f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(277f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(0f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(82f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(313f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(145f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(233f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(258f / 360f), (72f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(165f / 360f), (45f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(59f / 360f), (45f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(246f / 360f), (45f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(120f / 360f), (45f / 100f), (65f / 100f)}),
            Color.HSVToColor(1, new float[]{(122f / 360f), (0f / 100f), (59f / 100f)}),
    };
    byte[][] notes = new byte[16][0];
}
