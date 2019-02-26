package com.rocky.moreapps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MoreAppsDetails implements Parcelable {
    public static final Parcelable.Creator<MoreAppsDetails> CREATOR = new Parcelable.Creator<MoreAppsDetails>() {
        @Override
        public MoreAppsDetails createFromParcel(Parcel source) {
            return new MoreAppsDetails(source);
        }

        @Override
        public MoreAppsDetails[] newArray(int size) {
            return new MoreAppsDetails[size];
        }
    };
    @SerializedName("image_link")
    public String imageLink;
    @SerializedName("name")
    public String name;
    @SerializedName("rating")
    public double rating;
    @SerializedName("package_name")
    public String packageName;
    @SerializedName("description")
    public String description;
    @SerializedName("min_version")
    public int minVersion;
    @SerializedName("current_version")
    public int currentVersion;
    @SerializedName("redirect_details")
    public RedirectDetails redirectDetails;
    @SerializedName("soft_update_details")
    public SoftUpdateDetails softUpdateDetails;
    @SerializedName("hard_update_details")
    public HardUpdateDetails hardUpdateDetails;
    @SerializedName("app_link")
    public String appLink;
    @SerializedName("show_in_dialog")
    public boolean showInDialog = true;

    public MoreAppsDetails() {
    }

    private MoreAppsDetails(Parcel in) {
        this.imageLink = in.readString();
        this.name = in.readString();
        this.rating = in.readDouble();
        this.packageName = in.readString();
        this.description = in.readString();
        this.minVersion = in.readInt();
        this.currentVersion = in.readInt();
        this.redirectDetails = in.readParcelable(RedirectDetails.class.getClassLoader());
        this.softUpdateDetails = in.readParcelable(SoftUpdateDetails.class.getClassLoader());
        this.hardUpdateDetails = in.readParcelable(HardUpdateDetails.class.getClassLoader());
        this.appLink = in.readString();
        this.showInDialog = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageLink);
        dest.writeString(this.name);
        dest.writeDouble(this.rating);
        dest.writeString(this.packageName);
        dest.writeString(this.description);
        dest.writeInt(this.minVersion);
        dest.writeInt(this.currentVersion);
        dest.writeParcelable(this.redirectDetails, flags);
        dest.writeParcelable(this.softUpdateDetails, flags);
        dest.writeParcelable(this.hardUpdateDetails, flags);
        dest.writeString(this.appLink);
        dest.writeByte(this.showInDialog ? (byte) 1 : (byte) 0);
    }
}