package io.alcatraz.audiohq.extended;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import androidx.annotation.Nullable;

public class NoScrollListView extends ListView {
    public NoScrollListView(Context context) {
        super(context);
    }

    public NoScrollListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);
    }
}
