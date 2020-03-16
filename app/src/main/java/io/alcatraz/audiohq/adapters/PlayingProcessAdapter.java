package io.alcatraz.audiohq.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.activities.PresetInnerActivity;
import io.alcatraz.audiohq.beans.playing.Processes;
import io.alcatraz.audiohq.beans.preset.ControlElement;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;

public class PlayingProcessAdapter extends BaseAdapter {
    private List<Processes> data;
    private CompatWithPipeActivity activity;
    private LayoutInflater inflater;

    public PlayingProcessAdapter(CompatWithPipeActivity activity, List<Processes> data){
        this.activity = activity;
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflater.inflate(R.layout.item_playing_process,null);
        }
        TextView process = view.findViewById(R.id.item_playing_application_label);
        TextView status = view.findViewById(R.id.item_playing_application_status);
        ImageView ico = view.findViewById(R.id.item_playing_application_icon);
        Processes element = data.get(i);

        process.setText(element.getProcess());
        status.setText(element.getActive_count()+" / "+element.getTrack_count());

        ControlElement controlElement = new ControlElement(element.getProcess(),null,element.getProcess(),false,
                null,0);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PresetInnerActivity.class);
                intent.putExtra(PresetInnerActivity.KEY_INNER_PRESET,controlElement);
                activity.startTransition(intent,
                        process,ico);
            }
        });
        return view;
    }
}
