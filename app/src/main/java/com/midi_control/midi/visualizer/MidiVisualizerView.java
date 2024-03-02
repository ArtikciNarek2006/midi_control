package com.midi_control.midi.visualizer;

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
import com.midi_control.midi.live_visualizer.LiveVisualizerPresenter;
import com.midi_control.midi.utils.MidiMessage;
import com.midi_control.midi.utils.MidiNote;
import com.midi_control.utils.ML;
import com.midi_control.utils.MyBuffer;
import com.midi_control.utils.MyMath;
import com.midi_control.utils.MyUtils;

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

    public static float DEFAULT_SLIDE_SPEED = 100f;
    public static FlowDirection DEFAULT_FLOW_DIRECTION = FlowDirection.UP;
    public static MyMath.Cords<Float> DEFAULT_TRANSLATE = new MyMath.Cords<>(0f, 0f);
    public static MyMath.Cords<Float> DEFAULT_ZOOM = new MyMath.Cords<>(0f, 0f);


    private MyBuffer<MidiNote> notes_buffer;
    private MidiNote[] note_buffer_empty_ref = new MidiNote[0];
    private FlowDirection flowDirection;
    private float pxPerSec;
    private MyMath.Cords<Float> translateCords, zoomCords;

    private Paint paint, bg_paint;
    private float step_x = 1;
    private long draw_timestamp_last, draw_timestamp_start, last_buffer_timestamp = 0;
    private int canvas_h = 1, canvas_w = 1;

    public MidiVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setFlowDirection(DEFAULT_FLOW_DIRECTION);
        this.setSlideSpeed(DEFAULT_SLIDE_SPEED);
        this.setTranslate(DEFAULT_TRANSLATE); // TODO: get from attributeSet maybe
        this.setZoom(DEFAULT_ZOOM); // TODO: get from attributeSet maybe

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
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255, 0, 0));

        bg_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg_paint.setColor(Color.rgb(10, 10, 30));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Account for padding.
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());
        canvas_w = w;
        canvas_h = h;

        step_x = w / 128f;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0f, 0f, (float) canvas_w, (float) canvas_h, bg_paint);

        long current_timestamp = System.nanoTime();
        float frame_time_s = (float) (current_timestamp - draw_timestamp_last) / MyMath.NANOS_PER_SECOND;
        draw_timestamp_last = System.nanoTime();

        if (notes_buffer != null) {
            MidiNote[] list = notes_buffer.getLinkedList().toArray(note_buffer_empty_ref);
            for (MidiNote note : list) {
                if (note != null) {
                    float note_start_s = (float) (current_timestamp - note.java_timestamp) / MyMath.NANOS_PER_SECOND;
                    float y1 = note_start_s * pxPerSec, y2;
                    if(note.local_duration == null){
                        y2 = 0;
                    }else{
                        y2 = y1 - ((float) note.local_duration / MyMath.NANOS_PER_SECOND) * pxPerSec;
                    }
                    float x1 = step_x * note.pitch, x2 = x1 + step_x;

                    y2 = canvas_h - y2;
                    y1 = canvas_h - y1;

                    canvas.drawRect(x1, y1, x2, y2, paint);
//                    ML.log(TAG, "step_x = " + step_x + " pitch= " + note.pitch + " drawRect(" + x1 + " , " + y1 + ", " + x2 + ", " + y2 + ")");
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
    public void setTranslate(MyMath.Cords<Float> cords) {
        this.translateCords = cords;
    }

    @Override
    public MyMath.Cords<Float> getTranslate() {
        return translateCords;
    }

    @Override
    public void setZoom(MyMath.Cords<Float> coefficients) {
        this.zoomCords = coefficients;
    }

    @Override
    public MyMath.Cords<Float> getZoom() {
        return this.zoomCords;
    }
}
