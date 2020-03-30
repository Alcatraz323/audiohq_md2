package io.alcatraz.audiohq.extended;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class CancelOutsideRelativeLayout extends RelativeLayout {
    private TouchListener listener;

    public CancelOutsideRelativeLayout(Context context) {
        super(context);
    }

    public CancelOutsideRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CancelOutsideRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CancelOutsideRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getX() >= getWidth() + 10
                    || event.getY() >= getHeight() + 20) {
                if(listener!=null) {
                    listener.onTouchOutside(this);
                }
            }
        }
        return true;
    }

    public void setListener(TouchListener listener) {
        this.listener = listener;
    }

    public interface TouchListener{
        void onTouchOutside(CancelOutsideRelativeLayout view);
    }
}
