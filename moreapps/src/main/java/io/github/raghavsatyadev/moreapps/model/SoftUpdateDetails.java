package io.github.raghavsatyadev.moreapps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SoftUpdateDetails implements Parcelable {
    @SerializedName("enable")
    public boolean enable;
    @SerializedName("dialog_title")
    public String dialogTitle;
    @SerializedName("dialog_message")
    public String dialogMessage;
    @SerializedName("positive_button")
    public String positiveButton;
    @SerializedName("negative_button")
    public String negativeButton;
    @SerializedName("dialog_show_count")
    public int dialogShowCount = 0;
    @SerializedName("notification_show_count")
    public int notificationShowCount = 0;

    public SoftUpdateDetails() {
    }

    protected SoftUpdateDetails(Parcel in) {
        enable = in.readByte() != 0;
        dialogTitle = in.readString();
        dialogMessage = in.readString();
        positiveButton = in.readString();
        negativeButton = in.readString();
        dialogShowCount = in.readInt();
        notificationShowCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (enable ? 1 : 0));
        dest.writeString(dialogTitle);
        dest.writeString(dialogMessage);
        dest.writeString(positiveButton);
        dest.writeString(negativeButton);
        dest.writeInt(dialogShowCount);
        dest.writeInt(notificationShowCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SoftUpdateDetails> CREATOR = new Creator<SoftUpdateDetails>() {
        @Override
        public SoftUpdateDetails createFromParcel(Parcel in) {
            return new SoftUpdateDetails(in);
        }

        @Override
        public SoftUpdateDetails[] newArray(int size) {
            return new SoftUpdateDetails[size];
        }
    };
}
