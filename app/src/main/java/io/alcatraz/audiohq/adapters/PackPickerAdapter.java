package io.alcatraz.audiohq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.activities.AppPickActivity;
import io.alcatraz.audiohq.beans.PickerPack;

public class PackPickerAdapter extends RecyclerView.Adapter<PickerHolder>{
    private List<PickerPack> data;
    private AppPickActivity activity;
    private LayoutInflater inflater;
    private PackageFilter filter;

    public PackPickerAdapter(AppPickActivity activity, List<PickerPack> data) {
        this.activity = activity;
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public PickerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = inflater.inflate(R.layout.item_app_pick,parent,false);
        return new PickerHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull PickerHolder holder, int position) {
        PickerPack element = data.get(position);
        holder.pack.setText(element.pack);
        holder.label.setText(element.label);
        holder.icon.setImageDrawable(element.icon);
        holder.checkBox.setChecked(element.checked);
        holder.itemView.setOnClickListener(view -> {
            if (holder.checkBox.isChecked()) {
                activity.pickerRemove(element.pack);
            } else {
                activity.pickerAdd(element.pack);
            }
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new PackageFilter(data);
        }
        return filter;
    }

    public void onTextChanged(String newtext){
        getFilter().filter(newtext);
    }

    private class PackageFilter extends Filter {

        List<PickerPack> original;

        public PackageFilter(List<PickerPack> tofilter){
            original = tofilter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if (charSequence == null || charSequence.length() == 0) {
                results.values = original;
                results.count = original.size();
            } else {
                List<PickerPack> mList = new ArrayList<>();
                for(PickerPack info : original){
                    if(info.label.toLowerCase().contains(charSequence.toString().toLowerCase())
                            || info.pack.toLowerCase().contains(charSequence.toString().toLowerCase())){
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
            data = (List<PickerPack>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}

class PickerHolder extends RecyclerView.ViewHolder{
    public TextView label;
    public TextView pack;
    public ImageView icon;
    public CheckBox checkBox;
    public PickerHolder(@NonNull View itemView) {
        super(itemView);
        label = itemView.findViewById(R.id.app_pick_pack_label);
        pack = itemView.findViewById(R.id.app_pick_pack_name);
        icon = itemView.findViewById(R.id.app_pick_pack_icon);
        checkBox = itemView.findViewById(R.id.app_pick_pack_checkbox);
    }
}
