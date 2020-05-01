package io.alcatraz.audiohq.beans;

import android.graphics.drawable.Drawable;

public class PickerPack {
    public String pack;
    public String label;
    public Drawable icon;
    public boolean checked;
    public PickerPack(String pack, String label, Drawable icon, boolean checked){
        this.pack = pack;
        this.label = label;
        this.icon = icon;
        this.checked = checked;
    }
}
