package com.rocky.moreapps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SoftUpdateDetails implements Parcelable {
    public static final Parcelable.Creator<SoftUpdateDetails> CREATOR = new Parcelable.Creator<SoftUpdateDetails>() {
        @Override
        public SoftUpdateDetails createFromParcel(Parcel source) {
            return new SoftUpdateDetails(source);
        }

        @Override
        public SoftUpdateDetails[] newArray(int size) {
            return new SoftUpdateDetails[size];
        }
    };
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

    public SoftUpdateDetails() {
    }

    SoftUpdateDetails(Parcel in) {
        this.enable = in.readByte() != 0;
        this.dialogTitle = in.readString();
        this.dialogMessage = in.readString();
        this.positiveButton = in.readString();
        this.negativeButton = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
        dest.writeString(this.dialogTitle);
        dest.writeString(this.dialogMessage);
        dest.writeString(this.positiveButton);
        dest.writeString(this.negativeButton);
    }
}
