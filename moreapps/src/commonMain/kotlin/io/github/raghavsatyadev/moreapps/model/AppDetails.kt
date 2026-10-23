package io.github.raghavsatyadev.moreapps.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One entry of the hosted JSON array. Field names match the 2.x format, so old files still work. */
@Immutable
@Serializable
public data class AppDetails(
  @SerialName("image_link") val imageLink: String = "",
  val name: String = "",
  val rating: Double = 0.0,
  @SerialName("app_link") val appLink: String = "",
  @SerialName("package_name") val packageName: String = "",
  val description: String = "",
  @SerialName("min_version") val minVersion: Long = 0,
  @SerialName("current_version") val currentVersion: Long = 0,
  @SerialName("show_in_dialog") val showInDialog: Boolean = true,
  @SerialName("redirect_details") val redirectDetails: RedirectDetails = RedirectDetails(),
  @SerialName("soft_update_details") val softUpdateDetails: SoftUpdateDetails = SoftUpdateDetails(),
  @SerialName("hard_update_details") val hardUpdateDetails: HardUpdateDetails = HardUpdateDetails(),
)

/** Sends users of this app to another app (for example a replacement listing). */
@Immutable
@Serializable
public data class RedirectDetails(
  val enable: Boolean = false,
  @SerialName("hard_redirect") val hardRedirect: Boolean = false,
  @SerialName("dialog_title") val dialogTitle: String = "",
  @SerialName("dialog_message") val dialogMessage: String = "",
  @SerialName("positive_button") val positiveButton: String = "",
  @SerialName("negative_button") val negativeButton: String = "",
  @SerialName("app_link") val appLink: String = "",
)

/**
 * An optional update. [dialogShowCount] and [notificationShowCount] cap how many times each is
 * shown per published version; 0 means no cap.
 */
@Immutable
@Serializable
public data class SoftUpdateDetails(
  val enable: Boolean = false,
  @SerialName("dialog_show_count") val dialogShowCount: Int = 0,
  @SerialName("notification_show_count") val notificationShowCount: Int = 0,
  @SerialName("dialog_title") val dialogTitle: String = "",
  @SerialName("dialog_message") val dialogMessage: String = "",
  @SerialName("positive_button") val positiveButton: String = "",
  @SerialName("negative_button") val negativeButton: String = "",
)

/** A required update: shown when the installed version is below `min_version`. */
@Immutable
@Serializable
public data class HardUpdateDetails(
  val enable: Boolean = false,
  @SerialName("dialog_title") val dialogTitle: String = "",
  @SerialName("dialog_message") val dialogMessage: String = "",
  @SerialName("positive_button") val positiveButton: String = "",
)
