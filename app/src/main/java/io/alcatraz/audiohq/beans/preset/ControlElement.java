package io.alcatraz.audiohq.beans.preset;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class ControlElement implements Parcelable {
    private String label;
    private String status;
    private String process;
    private Drawable icon;
    private boolean conflicted = false;
    private boolean isweakkey;
    private int color;

    public ControlElement(String label, String status, String process,boolean isweakkey, Drawable icon, int color) {
        this.label = label;
        this.status = status;
        this.icon = icon;
        this.process = process;
        this.color = color;
        this.isweakkey = isweakkey;
    }

    protected ControlElement(Parcel in) {
        label = in.readString();
        status = in.readString();
        process = in.readString();
        conflicted = in.readByte() != 0;
        isweakkey = in.readByte() != 0;
        color = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(status);
        dest.writeString(process);
        dest.writeByte((byte) (conflicted ? 1 : 0));
        dest.writeByte((byte) (isweakkey ? 1 : 0));
        dest.writeInt(color);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ControlElement> CREATOR = new Creator<ControlElement>() {
        @Override
        public ControlElement createFromParcel(Parcel in) {
            return new ControlElement(in);
        }

        @Override
        public ControlElement[] newArray(int size) {
            return new ControlElement[size];
        }
    };

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isConflicted() {
        return conflicted;
    }

    public void setConflicted(boolean conflicted) {
        this.conflicted = conflicted;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public boolean isIsweakkey() {
        return isweakkey;
    }

    public void setIsweakkey(boolean isweakkey) {
        this.isweakkey = isweakkey;
    }

}
