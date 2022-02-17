package io.github.raghavsatyadev.moreapps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MoreAppsDetails implements Parcelable {
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

    protected MoreAppsDetails(Parcel in) {
        imageLink = in.readString();
        name = in.readString();
        rating = in.readDouble();
        packageName = in.readString();
        description = in.readString();
        minVersion = in.readInt();
        currentVersion = in.readInt();
        redirectDetails = in.readParcelable(RedirectDetails.class.getClassLoader());
        softUpdateDetails = in.readParcelable(SoftUpdateDetails.class.getClassLoader());
        hardUpdateDetails = in.readParcelable(HardUpdateDetails.class.getClassLoader());
        appLink = in.readString();
        showInDialog = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageLink);
        dest.writeString(name);
        dest.writeDouble(rating);
        dest.writeString(packageName);
        dest.writeString(description);
        dest.writeInt(minVersion);
        dest.writeInt(currentVersion);
        dest.writeParcelable(redirectDetails, flags);
        dest.writeParcelable(softUpdateDetails, flags);
        dest.writeParcelable(hardUpdateDetails, flags);
        dest.writeString(appLink);
        dest.writeByte((byte) (showInDialog ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MoreAppsDetails> CREATOR = new Creator<MoreAppsDetails>() {
        @Override
        public MoreAppsDetails createFromParcel(Parcel in) {
            return new MoreAppsDetails(in);
        }

        @Override
        public MoreAppsDetails[] newArray(int size) {
            return new MoreAppsDetails[size];
        }
    };
}