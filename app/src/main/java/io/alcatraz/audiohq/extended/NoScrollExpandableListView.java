package io.alcatraz.audiohq.extended;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;

public class NoScrollExpandableListView extends ExpandableListView{
    public NoScrollExpandableListView(Context context) {
        super(context);
    }

    public NoScrollExpandableListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollExpandableListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);
    }
}
