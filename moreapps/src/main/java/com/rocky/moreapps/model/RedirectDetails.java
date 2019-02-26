package com.rocky.moreapps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RedirectDetails implements Parcelable {
    public static final Parcelable.Creator<RedirectDetails> CREATOR = new Parcelable.Creator<RedirectDetails>() {
        @Override
        public RedirectDetails createFromParcel(Parcel source) {
            return new RedirectDetails(source);
        }

        @Override
        public RedirectDetails[] newArray(int size) {
            return new RedirectDetails[size];
        }
    };
    @SerializedName("enable")
    public boolean enable;
    @SerializedName("hard_redirect")
    public boolean hardRedirect;
    @SerializedName("dialog_title")
    public String dialogTitle;
    @SerializedName("dialog_message")
    public String dialogMessage;
    @SerializedName("positive_button")
    public String positiveButton;
    @SerializedName("negative_button")
    public String negativeButton;
    @SerializedName("app_link")
    public String appLink;

    public RedirectDetails() {
    }

    RedirectDetails(Parcel in) {
        this.enable = in.readByte() != 0;
        this.hardRedirect = in.readByte() != 0;
        this.dialogTitle = in.readString();
        this.dialogMessage = in.readString();
        this.positiveButton = in.readString();
        this.negativeButton = in.readString();
        this.appLink = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.enable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hardRedirect ? (byte) 1 : (byte) 0);
        dest.writeString(this.dialogTitle);
        dest.writeString(this.dialogMessage);
        dest.writeString(this.positiveButton);
        dest.writeString(this.negativeButton);
        dest.writeString(this.appLink);
    }
}
