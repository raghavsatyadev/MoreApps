package io.github.raghavsatyadev.moreapps.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class SoftUpdateDetails(
    @SerializedName("enable")
    var enable: Boolean = false,
    @SerializedName("dialog_title")
    var dialogTitle: String,
    @SerializedName("dialog_message")
    var dialogMessage: String,
    @SerializedName("positive_button")
    var positiveButton: String,
    @SerializedName("negative_button")
    var negativeButton: String,
    @SerializedName("dialog_show_count")
    var dialogShowCount: Int = 0,
    @SerializedName("notification_show_count")
    var notificationShowCount: Int = 0,
) : Parcelable