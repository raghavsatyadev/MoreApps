package io.github.raghavsatyadev.moreapps.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
class RedirectDetails(
    @SerializedName("enable")
    var enable: Boolean = false,
    @SerializedName("hard_redirect")
    var hardRedirect: Boolean = false,
    @SerializedName("dialog_title")
    var dialogTitle: String,
    @SerializedName("dialog_message")
    var dialogMessage: String,
    @SerializedName("positive_button")
    var positiveButton: String,
    @SerializedName("negative_button")
    var negativeButton: String,
    @SerializedName("app_link")
    var appLink: String,
) : Parcelable