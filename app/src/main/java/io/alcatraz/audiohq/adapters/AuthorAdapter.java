package io.alcatraz.audiohq.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;

public class AuthorAdapter extends BaseAdapter {
    private Map<Integer, List<String>> data;
    private List<Integer> img;
    private CompatWithPipeActivity activity;
    private LayoutInflater lf;
    private TextView anniversary;

    public AuthorAdapter(CompatWithPipeActivity activity, Map<Integer, List<String>> data, List<Integer> img) {
        this.data = data;
        this.activity = activity;
        this.img = img;
        lf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int p1) {
        return data.get(p1).get(0);
    }

    @Override
    public long getItemId(int p1) {
        return p1;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int p1, View p2, ViewGroup p3) {
        if (p2 == null) {
            p2 = lf.inflate(R.layout.item_author_main_list, null);
        }
        ImageView iv = p2.findViewById(R.id.authoritemImageView1);
        TextView txv1 = p2.findViewById(R.id.authoritemTextView1);
        TextView txv2 = p2.findViewById(R.id.authoritemTextView2);
        iv.setImageResource(img.get(p1));
        txv1.setText(data.get(p1).get(0));
        txv2.setText(data.get(p1).get(1));
        if(p1 == 0) {
            if(Constants.ANNIVERSARY_1ST){
                anniversary = p2.findViewById(R.id.authoritemTextView3);
                anniversary.setVisibility(View.VISIBLE);
                anniversary.setTextColor(activity.color);
                anniversary.setText(R.string.au_l_1st_anniversary_version);
                anniversary.append(" ");
            }
        }
        if (p1 == 2)
            txv2.setVisibility(View.GONE);
        else
            txv2.setVisibility(View.VISIBLE);
        return p2;
    }

    public TextView getAnniversary() {
        return anniversary;
    }

}
