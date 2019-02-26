package com.rocky.moreapps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class HardUpdateDetails implements Parcelable {
    public static final Parcelable.Creator<HardUpdateDetails> CREATOR = new Parcelable.Creator<HardUpdateDetails>() {
        @Override
        public HardUpdateDetails createFromParcel(Parcel source) {
            return new HardUpdateDetails(source);
        }

        @Override
        public HardUpdateDetails[] newArray(int size) {
            return new HardUpdateDetails[size];
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

    public HardUpdateDetails() {
    }

    HardUpdateDetails(Parcel in) {
        this.enable = in.readByte() != 0;
        this.dialogTitle = in.readString();
        this.dialogMessage = in.readString();
        this.positiveButton = in.readString();
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
    }
}
