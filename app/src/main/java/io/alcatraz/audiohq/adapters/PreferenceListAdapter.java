package io.alcatraz.audiohq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.beans.PreferenceHeader;

public class PreferenceListAdapter extends BaseAdapter {
    private List<PreferenceHeader> data;
    private Context context;
    private LayoutInflater layoutInflater;

    public PreferenceListAdapter(Context context, List<PreferenceHeader> data){
        this.context=context;
        this.data =data;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PreferenceHeader header = data.get(i);
        if(view == null){
            view = layoutInflater.inflate(R.layout.item_preference_header,null);
        }
        ImageView header_image = view.findViewById(R.id.item_pref_header_image);
        TextView header_title = view.findViewById(R.id.item_pref_header_title);
        TextView header_summary = view.findViewById(R.id.item_pref_header_summary);

        header_image.setImageResource(header.getIcon_res());
        header_title.setText(header.getTitle());
        header_summary.setText(header.getSummary());
        return view;
    }
}
