package com.midi_control.midi.visualizer;

import static com.midi_control.utils.MidiUtils.countWhiteKeys;
import static com.midi_control.utils.MidiUtils.isBlackKey;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.midi_control.R;
import com.midi_control.midi.utils.MidiNote;
import com.midi_control.utils.ML;
import com.midi_control.utils.MyBuffer;
import com.midi_control.utils.MyMath;
import com.midi_control.utils.MyUtils;
import com.mobileer.miditools.MidiConstants;

public class MidiVisualizerView extends View implements MidiVisualizerContract.VisualizerView {
    public static final String TAG = "MidiVisualizerView";

    public enum FlowDirection {
        UP(0), DOWN(1), LEFT(2), RIGHT(3);
        public final int id;

        FlowDirection(int id) {
            this.id = id;
        }

        @NonNull
        static FlowDirection fromId(int id) {
            for (FlowDirection f : values()) {
                if (f.id == id) return f;
            }
            throw new IllegalArgumentException();
        }
    }

    public static float DEFAULT_SLIDE_SPEED = 250f;
    public static FlowDirection DEFAULT_FLOW_DIRECTION = FlowDirection.UP;
    public static MyMath.Cords<Byte> DEFAULT_MIN_MAX_PITCH = new MyMath.Cords<>((byte) 36, (byte) 96);

    private static final float BLACK_KEY_WIDTH_FACTOR = 0.6f, BLACK_KEY_OFFSET_FACTOR = 0.18f;
    private static final int WHITE_KEY_GAP = 10, HALF_KEY_GAP = 5, NOTES_PER_OCTAVE = 12, WHITE_NOTES_PER_OCTAVE = 7;
    private static final int[] BLACK_KEY_HORIZONTAL_OFFSETS = {
            -1, 1, -1, 0, 1
    };
    private static final int[] WHITE_KEY_LEFT_COMPLEMENTS = {
            0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6, 6
    };
    private static final int[] BLACK_KEY_LEFT_COMPLEMENTS = {
            0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5
    };
    private static final int[] BLACK_KEY_INDEX = {
            15, 0, 15, 1, 15, 15, 2, 15, 3, 15, 4, 15
    };


    private MyBuffer<MidiNote> notes_buffer;
    private final MidiNote[] note_buffer_empty_ref = new MidiNote[0];
    private FlowDirection flowDirection;
    private float pxPerSec;
    private MyMath.Cords<Byte> minMaxPitches;

    private float mWhiteKeyWidth = 1, mBlackKeyWidth = 1;


    private Paint redPaint;
    private Paint[] whiteNotesPaint_ch = new Paint[MidiConstants.MAX_CHANNELS]; // GM standard channel count is 16 :
    private Paint[] blackNotesPaint_ch = new Paint[whiteNotesPaint_ch.length]; // WARN: if in some case length changed refactor init_draw Paints generation for block
    private long draw_timestamp_last, draw_timestamp_start, last_buffer_timestamp = 0;
    private float canvas_h = 1, canvas_w = 1;

    public MidiVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setFlowDirection(DEFAULT_FLOW_DIRECTION);
        this.setSlideSpeed(DEFAULT_SLIDE_SPEED);
        this.setMinMaxPitches(DEFAULT_MIN_MAX_PITCH); // TODO: get from attributeSet maybe

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MidiVisualizerView, 0, 0);
        try {
            this.setFlowDirection(FlowDirection.fromId(a.getInt(R.styleable.MidiVisualizerView_flowDirection, DEFAULT_FLOW_DIRECTION.id)));
            this.setSlideSpeed(a.getFloat(R.styleable.MidiVisualizerView_slideSpeed, DEFAULT_SLIDE_SPEED));
        } finally {
            a.recycle();
        }

        draw_timestamp_last = System.nanoTime();
        draw_timestamp_start = System.nanoTime();
        this.init_draw();
    }

    private void init_draw() {
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.rgb(255, 0, 0));

        int length = whiteNotesPaint_ch.length / 2;
        float h_step = 360f / length, h = 135f, s1 = 80f, vWh = 85f, s2 = 50f, vBl = 65f;
        for (int i = 0; i < length; i++) {
            whiteNotesPaint_ch[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            whiteNotesPaint_ch[i + length] = new Paint(Paint.ANTI_ALIAS_FLAG);

            blackNotesPaint_ch[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            blackNotesPaint_ch[i + length] = new Paint(Paint.ANTI_ALIAS_FLAG);

            whiteNotesPaint_ch[i].setColor(Color.HSVToColor(new float[]{h, s1, vWh}));
            whiteNotesPaint_ch[i + length].setColor(Color.HSVToColor(new float[]{h, s2, vWh}));

            blackNotesPaint_ch[i].setColor(Color.HSVToColor(new float[]{h, s1, vBl}));
            blackNotesPaint_ch[i + length].setColor(Color.HSVToColor(new float[]{h, s2, vBl}));
            h = (h + h_step) % 360f;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Account for padding.
        float xPad = (float) (getPaddingLeft() + getPaddingRight());
        float yPad = (float) (getPaddingTop() + getPaddingBottom());
        canvas_w = w;
        canvas_h = h;

        mWhiteKeyWidth = canvas_w / countWhiteKeys(minMaxPitches.x, minMaxPitches.y);
        mBlackKeyWidth = mWhiteKeyWidth * BLACK_KEY_WIDTH_FACTOR;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawRect(0f, 0f, (float) canvas_w, (float) canvas_h, bg_paint);

        long current_timestamp = System.nanoTime();
        float frame_time_s = (float) (current_timestamp - draw_timestamp_last) / MyMath.NANOS_PER_SECOND;
        draw_timestamp_last = System.nanoTime();

        if (notes_buffer != null) {
            MidiNote[] list = notes_buffer.getLinkedList().toArray(note_buffer_empty_ref);
            for (MidiNote note : list) {
                if (note != null && (note.pitch >= minMaxPitches.x) && (note.pitch <= minMaxPitches.y)) {
                    float note_start_s = (float) (current_timestamp - note.java_timestamp) / MyMath.NANOS_PER_SECOND;
                    float y1 = note_start_s * pxPerSec, y2;
                    if (note.local_duration == null) {
                        y2 = 0;
                    } else {
                        y2 = y1 - ((float) note.local_duration / MyMath.NANOS_PER_SECOND) * pxPerSec;
                    }

                    y2 = canvas_h - y2;
                    y1 = canvas_h - y1;

                    byte draw_pitch = (byte) (note.pitch - minMaxPitches.x);
                    float x1 = (byte) (draw_pitch / NOTES_PER_OCTAVE) * mWhiteKeyWidth * WHITE_NOTES_PER_OCTAVE, x2;
                    draw_pitch %= NOTES_PER_OCTAVE;

                    x1 += WHITE_KEY_LEFT_COMPLEMENTS[draw_pitch] * mWhiteKeyWidth;

                    if (isBlackKey(note.pitch)) {
                        float offset = BLACK_KEY_OFFSET_FACTOR * BLACK_KEY_HORIZONTAL_OFFSETS[BLACK_KEY_INDEX[draw_pitch]];
                        x1 = x1 - mBlackKeyWidth * (0.5f - offset);
                        x2 = x1 + mBlackKeyWidth;
                        canvas.drawRect(x1, y1, x2, y2, blackNotesPaint_ch[note.channel]);
                    } else {
                        x1 += HALF_KEY_GAP;
                        x2 = x1 + mWhiteKeyWidth - WHITE_KEY_GAP;
                        canvas.drawRect(x1, y1, x2, y2, whiteNotesPaint_ch[note.channel]);
                    }

//                    ML.log(TAG, "mWhiteKeyWidth = " + mWhiteKeyWidth + " pitch= " + note.pitch + " drawRect(" + x1 + " , " + y1 + ", " + x2 + ", " + y2 + ")");
//                    ML.log(TAG, "pitch= " + note.pitch + " is_white:" + isBlackKey(note.pitch));
                }
            }
        }

//        this.invalidate();
        MyUtils.setTimeout(this::invalidate, 20);
    }

    // interface implements
    @Override
    public void clearViewDrawings() {
        notes_buffer = null;
        // TODO: call redraw here;
    }

    @Override
    public synchronized void setNotesBuffer(MyBuffer<MidiNote> midiNotes) {
        notes_buffer = midiNotes;
        ML.log(TAG, "setNotesBuffer(" + midiNotes + ")");
        last_buffer_timestamp = System.nanoTime();
    }

    @Override
    public MyBuffer<MidiNote> getNotesBuffer() {
        return notes_buffer;
    }

    @Override
    public void setFlowDirection(FlowDirection flowDirection) {
        this.flowDirection = flowDirection;
    }

    @Override
    public FlowDirection getFlowDirection() {
        return flowDirection;
    }

    @Override
    public void setSlideSpeed(float pxPerSec) {
        this.pxPerSec = pxPerSec;
    }

    @Override
    public float getSlideSpeed() {
        return this.pxPerSec;
    }

    @Override
    public void setMinMaxPitches(MyMath.Cords<Byte> cords) {
        this.minMaxPitches = cords;
    }

    @Override
    public MyMath.Cords<Byte> getMinMaxPitches() {
        return minMaxPitches;
    }
}
