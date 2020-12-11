package io.alcatraz.audiohq.extended;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

@SuppressLint("AppCompatCustomView")
public class ClickOverrideImageButton extends ImageButton {
    private int x = 0;
    private int y = 0;
    private ClickListener clickListener;

    public ClickOverrideImageButton(Context context) {
        super(context);
        setupDefaultClickListener();
    }

    public ClickOverrideImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDefaultClickListener();
    }

    public ClickOverrideImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDefaultClickListener();
    }

    public ClickOverrideImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupDefaultClickListener();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private void setupDefaultClickListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(ClickOverrideImageButton.this, x, y);
                }
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
    public boolean performClick() {
        if (clickListener != null) {
            return clickListener.onClick(this, x, y);
        }
        return super.performClick();
    }

    public interface ClickListener {
        boolean onClick(ClickOverrideImageButton view, int x, int y);
    }
}
