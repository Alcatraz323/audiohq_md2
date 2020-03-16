package io.alcatraz.audiohq.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.activities.PresetInnerActivity;
import io.alcatraz.audiohq.beans.playing.Pkgs;
import io.alcatraz.audiohq.beans.preset.ControlElement;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.extended.NoScrollListView;
import io.alcatraz.audiohq.utils.PackageCtlUtils;
import io.alcatraz.audiohq.utils.Utils;

public class PlayingRecyclerAdapter extends RecyclerView.Adapter<PlayingRecyclerAdapter.RecyclerHolder> implements Filterable {
    private List<Pkgs> data;
    private CompatWithPipeActivity activity;
    private LayoutInflater inflater;

    private ActiveFilter filter;

    public PlayingRecyclerAdapter(CompatWithPipeActivity activity, List<Pkgs> data) {
        this.activity = activity;
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ActiveFilter(data);
        }
        return filter;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = inflater.inflate(R.layout.item_playing,parent,false);
        return new PlayingRecyclerAdapter.RecyclerHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        Pkgs element = data.get(position);
        String label = PackageCtlUtils.getLabel(activity,element.getPkg());

        holder.label.setText(label);
        holder.pkg.setText(element.getPkg());

        Drawable icon = PackageCtlUtils.getIcon(activity,element.getPkg());
        if(icon!=null) {
            holder.icon.setImageDrawable(icon);
        }else {
            Utils.retintImage(holder.icon,activity.getColor(R.color.base_gray_tint));
        }

        ControlElement controlElement = new ControlElement(label,null,element.getPkg(),true,
                icon,0);

        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.process_list.getVisibility() == View.VISIBLE){
                    holder.process_list.setVisibility(View.GONE);
                    holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }else {
                    holder.process_list.setVisibility(View.VISIBLE);
                    holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PresetInnerActivity.class);
                intent.putExtra(PresetInnerActivity.KEY_INNER_PRESET,controlElement);
                activity.startTransition(intent,
                        holder.label,holder.icon);
            }
        });

        PlayingProcessAdapter processAdapter = new PlayingProcessAdapter(activity,element.getProcesses());
        holder.process_list.setAdapter(processAdapter);
    }

    public void filterActive(boolean enable){
        getFilter().filter(enable+"");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView label;
        TextView pkg;
        NoScrollListView process_list;
        ImageButton expand;
        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_playing_application_icon);
            label = itemView.findViewById(R.id.item_playing_application_label);
            pkg = itemView.findViewById(R.id.item_playing_application_status);
            process_list = itemView.findViewById(R.id.item_playing_process_list);
            expand =itemView.findViewById(R.id.item_playing_expand);
        }
    }

    private class ActiveFilter extends Filter {

        List<Pkgs> original;

        public ActiveFilter(List<Pkgs> tofilter){
            original = tofilter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if (charSequence.toString().equals("false")) {
                results.values = original;
                results.count = original.size();
            } else {
                List<Pkgs> mList = new ArrayList<>();
                for(Pkgs info : original){
                    if(info.getHas_active_track()){
                        mList.add(info);
                    }
                }
                results.values = mList;
                results.count = mList.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            data = (List<Pkgs>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
