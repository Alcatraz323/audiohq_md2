package io.alcatraz.audiohq.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class PreferenceHeader implements Parcelable {
    private String title;
    private String summary;
    private int icon_res;

    public PreferenceHeader(String title,String summary,int icon_res){
        this.title = title;
        this.summary = summary;
        this.icon_res = icon_res;
    }

    protected PreferenceHeader(Parcel in) {
        title = in.readString();
        summary = in.readString();
        icon_res = in.readInt();
    }

    public static final Creator<PreferenceHeader> CREATOR = new Creator<PreferenceHeader>() {
        @Override
        public PreferenceHeader createFromParcel(Parcel in) {
            return new PreferenceHeader(in);
        }

        @Override
        public PreferenceHeader[] newArray(int size) {
            return new PreferenceHeader[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getIcon_res() {
        return icon_res;
    }

    public void setIcon_res(int icon_res) {
        this.icon_res = icon_res;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(summary);
        parcel.writeInt(icon_res);
    }
}
