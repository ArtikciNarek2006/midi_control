// source https://github.com/pedrolcl/android/tree/master/NativeGMSynth

package com.midi_control.midi_tiles.midi.services.nativeGMSynth;

// TODO: fix this synth method errors at compile time and bugs that cause app crash

// todo: when enabling this service copy JNI folder from source github and paste in  main/java/.
// todo: in android studio right click to that folder and select add C++ library
// todo: select android.mk as make file
// todo: fix native method names in midisynth.c

public class MidiNativeSynthService {
//public class MidiNativeSynthService extends MidiDeviceService {
//    private static final String TAG = "MidiSynthService2";
//    private MIDISynth mSynthEngine = null;
//    private boolean mSynthStarted = false;
//
//    @Override
//    public void onCreate() {
//        try {
//            mSynthEngine = new MIDISynth();
//            ML.log(TAG, "created");
//            super.onCreate();
//        } catch (Exception ex) {
//            ML.err(TAG, String.valueOf(ex.getMessage()));
//        }
//    }
//
//    @Override
//    public void onClose() {
//        if (mSynthEngine != null) {
//            mSynthEngine.stop();
//            mSynthEngine.close();
//            mSynthStarted = false;
//            mSynthEngine = null;
//        }
//        super.onClose();
//        ML.err(TAG, "closed");
//    }
//
//    @Override
//    public void onDestroy() {
//        if (mSynthEngine != null) {
//            mSynthEngine.stop();
//            mSynthEngine.close();
//            mSynthStarted = false;
//            mSynthEngine = null;
//        }
//        super.onDestroy();
//        ML.err(TAG, "destroyed");
//    }
//
//    @Override
//    public MidiReceiver[] onGetInputPortReceivers() {
//        return new MidiReceiver[]{mSynthEngine};
//    }
//
//    /**
//     * This will get called when clients connect or disconnect.
//     */
//    @Override
//    public void onDeviceStatusChanged(MidiDeviceStatus status) {
//        try {
//            if (status.isInputPortOpen(0) && !mSynthStarted) {
//                mSynthEngine.start();
//                mSynthStarted = true;
//            } else if (!status.isInputPortOpen(0) && mSynthStarted) {
//                mSynthEngine.stop();
//                mSynthStarted = false;
//            }
//        } catch (Exception ex) {
//            ML.err(TAG, String.valueOf(ex.getMessage()));
//        }
//    }
//}
}
