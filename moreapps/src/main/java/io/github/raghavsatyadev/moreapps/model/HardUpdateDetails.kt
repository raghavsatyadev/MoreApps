package io.github.raghavsatyadev.moreapps.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class HardUpdateDetails(
    @SerializedName("enable")
    var enable: Boolean,

    @SerializedName("dialog_title")
    var dialogTitle: String,

    @SerializedName("dialog_message")
    var dialogMessage: String,

    @SerializedName("positive_button")
    var positiveButton: String,
) : Parcelable