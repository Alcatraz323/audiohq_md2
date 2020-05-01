package io.alcatraz.audiohq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.activities.PresetInnerActivity;
import io.alcatraz.audiohq.beans.preset.ControlElement;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.utils.Utils;

public class PresetGeneralRecyclerAdapter extends RecyclerView.Adapter<PresetGeneralRecyclerAdapter.RecyclerHolder> implements Filterable {
    private List<ControlElement> data;
    private CompatWithPipeActivity activity;
    private LayoutInflater inflater;
    private FloatingActionButton floatingActionButton;
    private PackageFilter filter;

    public PresetGeneralRecyclerAdapter(CompatWithPipeActivity activity, List<ControlElement> data, FloatingActionButton floatingActionButton) {
        this.activity = activity;
        this.floatingActionButton = floatingActionButton;
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = inflater.inflate(R.layout.item_preset_general,parent,false);
        return new RecyclerHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        ControlElement element = data.get(position);
        holder.label.setText(element.getLabel());
        holder.status.setText(element.getStatus());
        if(element.getIcon()!=null) {
            holder.icon.setImageDrawable(element.getIcon());
        }
        if(element.getColor()!=0){
            holder.status.setTextColor(element.getColor());
        }
        if(element.isConflicted()){
            holder.conflicted.setVisibility(View.VISIBLE);
            Utils.retintImage(holder.icon,activity.getColor(R.color.base_gray_tint));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionButton.setTransitionName(activity.getString(R.string.transition_preset_inner_fab));
                Intent intent = new Intent(activity, PresetInnerActivity.class);
                intent.putExtra(PresetInnerActivity.KEY_INNER_PRESET,element);
                activity.startTransition(intent,floatingActionButton,
                        holder.label,holder.icon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new PackageFilter(data);
        }
        return filter;
    }

    public void onTextChanged(String newtext){
        getFilter().filter(newtext);
    }

    class RecyclerHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView label;
        TextView status;
        TextView conflicted;
        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_preset_application_icon);
            label = itemView.findViewById(R.id.item_preset_application_label);
            status = itemView.findViewById(R.id.item_preset_application_status);
            conflicted = itemView.findViewById(R.id.item_preset_application_warning);
        }
    }

    private class PackageFilter extends Filter{

        List<ControlElement> original;

        public PackageFilter(List<ControlElement> tofilter){
            original = tofilter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if (charSequence == null || charSequence.length() == 0) {
                results.values = original;
                results.count = original.size();
            } else {
                List<ControlElement> mList = new ArrayList<>();
                for(ControlElement info : original){
                    if(info.getLabel().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || info.getProcess().toLowerCase().contains(charSequence.toString().toLowerCase())){
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
            data = (List<ControlElement>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}


