package com.midi_control.midi_tiles.midi.visualizer;

import static com.midi_control.midi_tiles.utils.MidiUtils.countWhiteKeys;
import static com.midi_control.midi_tiles.utils.MidiUtils.isBlackKey;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.midi_control.midi_tiles.R;
import com.midi_control.midi_tiles.midi.utils.MidiNote;
import com.midi_control.midi_tiles.utils.ML;
import com.midi_control.midi_tiles.utils.MyBuffer;
import com.midi_control.midi_tiles.utils.MyMath;
import com.midi_control.midi_tiles.utils.MyUtils;
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

    public static float DEFAULT_SLIDE_SPEED = 350f;
    public static FlowDirection DEFAULT_FLOW_DIRECTION = FlowDirection.UP;
    public static MyMath.Cords<Integer> DEFAULT_MIN_MAX_PITCH = new MyMath.Cords<>(48, 108);

    private static final float BLACK_KEY_WIDTH_FACTOR = 0.6f, BLACK_KEY_OFFSET_FACTOR = 0.18f;
    private static final
    int WHITE_KEY_GAP = 10, HALF_KEY_GAP = 5, NOTES_PER_OCTAVE = 12, WHITE_NOTES_PER_OCTAVE = 7;
    private static final int[] BLACK_KEY_HORIZONTAL_OFFSETS = {
            -1, 1, -1, 0, 1
    };
    private static final int[] WHITE_KEY_LEFT_COMPLEMENTS = {
            0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6, 6
    };
    private static final int[] BLACK_KEY_INDEX = {
            15, 0, 15, 1, 15, 15, 2, 15, 3, 15, 4, 15
    };


    private MyBuffer<MidiNote> notes_buffer;
    private final MidiNote[] note_buffer_empty_ref = new MidiNote[0];
    private FlowDirection flowDirection;
    private float pxPerSec;
    private MyMath.Cords<Integer> minMaxPitches;

    private float mWhiteKeyWidth = 1, mBlackKeyWidth = 1;


    private int note_radius = 1; // TODO: get radius from out
    private final float note_radius_factor = 0.25f; // factor * white_note_width
    private final RectF note_rect = new RectF();
    private float[] note_radius_top = new float[]{note_radius, note_radius, note_radius, note_radius, 0, 0, 0, 0};
    private float[] note_radius_all = new float[]{note_radius, note_radius, note_radius, note_radius, note_radius, note_radius, note_radius, note_radius};
    private final Path note_path = new Path();
    private final Paint[] whiteNotesPaint_ch = new Paint[MidiConstants.MAX_CHANNELS]; // GM standard channel count is 16 :
    private final Paint[] blackNotesPaint_ch = new Paint[whiteNotesPaint_ch.length]; // WARN: if in some case length changed refactor init_draw Paints generation for block
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
        int length = whiteNotesPaint_ch.length / 2;
        float h_step = 360f / length, h = 135f, s1 = 0.80f, vWh = 0.95f, s2 = 0.50f, vBl = 0.65f;
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

        ML.log(TAG, "init_draw(): whiteNotesPaint_ch: " + MyUtils.paintArray_toString(whiteNotesPaint_ch));
        ML.log(TAG, "init_draw(): blackNotesPaint_ch: " + MyUtils.paintArray_toString(blackNotesPaint_ch));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Account for padding.
//        float xPad = (float) (getPaddingLeft() + getPaddingRight());
//        float yPad = (float) (getPaddingTop() + getPaddingBottom());
        canvas_w = w;
        canvas_h = h;

        mWhiteKeyWidth = canvas_w / countWhiteKeys(minMaxPitches.x, minMaxPitches.y);
        mBlackKeyWidth = mWhiteKeyWidth * BLACK_KEY_WIDTH_FACTOR;

        note_radius = (int) (mWhiteKeyWidth * note_radius_factor);
        note_radius_top = new float[]{note_radius, note_radius, note_radius, note_radius, 0, 0, 0, 0};
        note_radius_all = new float[]{note_radius, note_radius, note_radius, note_radius, note_radius, note_radius, note_radius, note_radius};
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        long current_timestamp = System.nanoTime();
//        float frame_time_s = (float) (current_timestamp - draw_timestamp_last) / MyMath.NANOS_PER_SECOND;
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

                    Paint note_paint;
                    x1 += WHITE_KEY_LEFT_COMPLEMENTS[draw_pitch] * mWhiteKeyWidth;
                    if (isBlackKey(note.pitch)) {
                        int temp_i = BLACK_KEY_INDEX[draw_pitch];
                        float offset = BLACK_KEY_OFFSET_FACTOR * BLACK_KEY_HORIZONTAL_OFFSETS[temp_i];
                        x1 = x1 - mBlackKeyWidth * (0.5f - offset);
                        x2 = x1 + mBlackKeyWidth;

                        note_paint = blackNotesPaint_ch[note.channel];
//                        canvas.drawRect(x1, y1, x2, y2, blackNotesPaint_ch[note.channel]);
                    } else {
                        x1 += HALF_KEY_GAP;
                        x2 = x1 + mWhiteKeyWidth - WHITE_KEY_GAP;

                        note_paint = whiteNotesPaint_ch[note.channel];
//                        canvas.drawRect(x1, y1, x2, y2, whiteNotesPaint_ch[note.channel]);
                    }
                    note_rect.set(x1, y1, x2, y2);
                    note_path.reset();
                    if (note.local_duration == null) {
                        note_path.addRoundRect(note_rect, note_radius_top, Path.Direction.CW);
                    } else {
                        note_path.addRoundRect(note_rect, note_radius_all, Path.Direction.CW);
                    }
                    canvas.drawPath(note_path, note_paint);


//                    ML.log(TAG, "mWhiteKeyWidth = " + mWhiteKeyWidth + " pitch= " + note.pitch + " drawRect(" + x1 + " , " + y1 + ", " + x2 + ", " + y2 + ")");
//                    ML.log(TAG, "pitch= " + note.pitch + " is_white:" + isBlackKey(note.pitch));
                }
            }
        }

        this.invalidate();
//        MyUtils.setTimeout(this::invalidate, 20);
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
    public void setMinMaxPitches(MyMath.Cords<Integer> cords) {
        this.minMaxPitches = cords;
        postInvalidate();
        onSizeChanged((int) canvas_w, (int) canvas_h, (int) canvas_w, (int) canvas_h);
    }

    @Override
    public MyMath.Cords<Integer> getMinMaxPitches() {
        return minMaxPitches;
    }
}
