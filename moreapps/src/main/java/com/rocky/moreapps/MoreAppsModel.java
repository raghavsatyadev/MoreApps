package com.rocky.moreapps;

import com.google.gson.annotations.SerializedName;

public class MoreAppsModel {

    @SerializedName("image_link")
    public String imageLink;
    @SerializedName("name")
    public String name;
    @SerializedName("rating")
    public double rating;
    @SerializedName("play_store_link")
    public String playStoreLink;
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

    public static class RedirectDetails {
        @SerializedName("enable")
        public boolean enable;
        @SerializedName("hard_redirect")
        public boolean hardRedirect;
        @SerializedName("dialog_message")
        public String dialogMessage;
        @SerializedName("positive_button")
        public String positiveButton;
        @SerializedName("negative_button")
        public String negativeButton;
        @SerializedName("play_store_link")
        public String playStoreLink;
    }

    public static class SoftUpdateDetails {
        @SerializedName("enable")
        public boolean enable;
        @SerializedName("dialog_message")
        public String dialogMessage;
        @SerializedName("positive_button")
        public String positiveButton;
        @SerializedName("negative_button")
        public String negativeButton;
    }

    public static class HardUpdateDetails {
        @SerializedName("enable")
        public boolean enable;
        @SerializedName("dialog_message")
        public String dialogMessage;
        @SerializedName("positive_button")
        public String positiveButton;
        @SerializedName("negative_button")
        public String negativeButton;
    }
}