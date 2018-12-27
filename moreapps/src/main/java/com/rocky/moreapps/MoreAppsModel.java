package com.rocky.moreapps;

import android.os.Parcel;
import android.os.Parcelable;

public class MoreAppsModel implements Parcelable {
    public static final String TAG = MoreAppsModel.class.getSimpleName();
    public static final Parcelable.Creator<MoreAppsModel> CREATOR = new Parcelable.Creator<MoreAppsModel>() {
        @Override
        public MoreAppsModel createFromParcel(Parcel source) {
            return new MoreAppsModel(source);
        }

        @Override
        public MoreAppsModel[] newArray(int size) {
            return new MoreAppsModel[size];
        }
    };
    public double rating;
    public String image_link, name, play_store_link, description, package_name;

    public MoreAppsModel() {
    }

    protected MoreAppsModel(Parcel in) {
        this.rating = in.readDouble();
        this.image_link = in.readString();
        this.name = in.readString();
        this.play_store_link = in.readString();
        this.description = in.readString();
        this.package_name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.rating);
        dest.writeString(this.image_link);
        dest.writeString(this.name);
        dest.writeString(this.play_store_link);
        dest.writeString(this.description);
        dest.writeString(this.package_name);
    }
}