package io.github.raghavsatyadev.moreapps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RedirectDetails implements Parcelable {
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

    protected RedirectDetails(Parcel in) {
        enable = in.readByte() != 0;
        hardRedirect = in.readByte() != 0;
        dialogTitle = in.readString();
        dialogMessage = in.readString();
        positiveButton = in.readString();
        negativeButton = in.readString();
        appLink = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (enable ? 1 : 0));
        dest.writeByte((byte) (hardRedirect ? 1 : 0));
        dest.writeString(dialogTitle);
        dest.writeString(dialogMessage);
        dest.writeString(positiveButton);
        dest.writeString(negativeButton);
        dest.writeString(appLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RedirectDetails> CREATOR = new Creator<RedirectDetails>() {
        @Override
        public RedirectDetails createFromParcel(Parcel in) {
            return new RedirectDetails(in);
        }

        @Override
        public RedirectDetails[] newArray(int size) {
            return new RedirectDetails[size];
        }
    };
}
