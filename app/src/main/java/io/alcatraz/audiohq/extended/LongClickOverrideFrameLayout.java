package io.alcatraz.audiohq.extended;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LongClickOverrideFrameLayout extends FrameLayout {
    private int x = 0;
    private int y = 0;
    private LongClickListener longClickListener;

    public LongClickOverrideFrameLayout(@NonNull Context context) {
        super(context);
        setupDefaultLongClickListener();
    }

    public LongClickOverrideFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupDefaultLongClickListener();
    }

    public LongClickOverrideFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDefaultLongClickListener();
    }

    public LongClickOverrideFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupDefaultLongClickListener();
    }

    public void setLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    private void setupDefaultLongClickListener() {
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    return longClickListener.onLongClick(LongClickOverrideFrameLayout.this, x, y);
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performLongClick(float x, float y) {
        if (longClickListener != null) {
            return longClickListener.onLongClick(this, (int) x, (int) y);
        }
        return super.performLongClick(x, y);
    }

    public interface LongClickListener {
        boolean onLongClick(LongClickOverrideFrameLayout view, int x, int y);
    }
}
