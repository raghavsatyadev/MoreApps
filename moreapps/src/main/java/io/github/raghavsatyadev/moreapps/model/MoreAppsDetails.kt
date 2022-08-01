package io.github.raghavsatyadev.moreapps.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class MoreAppsDetails(

    @SerializedName("image_link")
    var imageLink: String,

    @SerializedName("name")
    var name: String,

    @SerializedName("rating")
    var rating: Double = 0.0,

    @SerializedName("package_name")
    var packageName: String,

    @SerializedName("description")
    var description: String,

    @SerializedName("min_version")
    var minVersion: Int = 0,

    @SerializedName("current_version")
    var currentVersion: Int = 0,

    @SerializedName("redirect_details")
    var redirectDetails: RedirectDetails,

    @SerializedName("soft_update_details")
    var softUpdateDetails: SoftUpdateDetails,

    @SerializedName("hard_update_details")
    var hardUpdateDetails: HardUpdateDetails,

    @SerializedName("app_link")
    var appLink: String,

    @SerializedName("show_in_dialog")
    var showInDialog: Boolean = true,
) : Parcelable