package com.rocky.moreapps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class HardUpdateDetails implements Parcelable {
    @SerializedName("enable")
    public boolean enable;

    @SerializedName("dialog_title")
    public String dialogTitle;

    @SerializedName("dialog_message")
    public String dialogMessage;

    @SerializedName("positive_button")
    public String positiveButton;

    protected HardUpdateDetails(Parcel in) {
        enable = in.readByte() != 0;
        dialogTitle = in.readString();
        dialogMessage = in.readString();
        positiveButton = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (enable ? 1 : 0));
        dest.writeString(dialogTitle);
        dest.writeString(dialogMessage);
        dest.writeString(positiveButton);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HardUpdateDetails> CREATOR = new Creator<HardUpdateDetails>() {
        @Override
        public HardUpdateDetails createFromParcel(Parcel in) {
            return new HardUpdateDetails(in);
        }

        @Override
        public HardUpdateDetails[] newArray(int size) {
            return new HardUpdateDetails[size];
        }
    };
}
