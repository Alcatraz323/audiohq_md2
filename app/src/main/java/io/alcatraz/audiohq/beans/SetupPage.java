package io.alcatraz.audiohq.beans;

import android.view.View;

public class SetupPage {
    private String title;
    private int layout_id;
    private View root_view;

    public SetupPage(String title, int resId) {
        this.title = title;
        layout_id = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLayout_id() {
        return layout_id;
    }

    public void setLayout_id(int layout_id) {
        this.layout_id = layout_id;
    }

    public View getRootView() {
        return root_view;
    }

    public void setRootView(View root_view) {
        this.root_view = root_view;
    }
}
