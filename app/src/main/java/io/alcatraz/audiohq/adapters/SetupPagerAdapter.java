package io.alcatraz.audiohq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import io.alcatraz.audiohq.beans.SetupPage;

public class SetupPagerAdapter extends PagerAdapter {
    private List<SetupPage> pages;
    private Context context;
    private LayoutInflater layoutInflater;

    public SetupPagerAdapter(List<SetupPage> pages, Context context) {
        this.pages = pages;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        SetupPage element = pages.get(position);
        View root = element.getRootView();
        if (root == null) {
            root = layoutInflater.inflate(element.getLayout_id(), null);
            element.setRootView(root);
        }
        container.addView(root);
        return root;
    }

    public Context getContext() {
        return context;
    }
}
