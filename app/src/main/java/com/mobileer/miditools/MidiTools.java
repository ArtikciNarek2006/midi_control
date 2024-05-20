/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mobileer.miditools;

import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

/**
 * Miscellaneous tools for Android MIDI.
 */
public class MidiTools {
    private static boolean is_null(String str) {
        return (str == null) || (str.toLowerCase(Locale.ROOT).equals("null"));
    }

    /**
     * @return a device that matches the manufacturer and product or null
     */
    @Nullable
    public static MidiDeviceInfo findDevice(@NonNull MidiManager midiManager,
                                            String manufacturer, String product) {
        for (MidiDeviceInfo info : midiManager.getDevices()) {
            String deviceManufacturer = info.getProperties().getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
            if ((is_null(manufacturer) && is_null(deviceManufacturer)) || ((manufacturer != null) && manufacturer.equals(deviceManufacturer))) {
                String deviceProduct = info.getProperties().getString(MidiDeviceInfo.PROPERTY_PRODUCT);
                if ((is_null(product) && is_null(deviceProduct)) || ((product != null) && product.equals(deviceProduct))) {
                    return info;
                }
            }
        }
        return null;
    }
}
