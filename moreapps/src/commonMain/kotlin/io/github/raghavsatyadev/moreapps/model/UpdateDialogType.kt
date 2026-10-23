package io.github.raghavsatyadev.moreapps.model

/** Which update prompt, if any, the current app's entry asks for. */
public enum class UpdateDialogType {
  NONE,
  SOFT_UPDATE,
  HARD_UPDATE,
  SOFT_REDIRECT,
  HARD_REDIRECT;

  /** Hard prompts cannot be dismissed: the user must update or leave the app. */
  public val isBlocking: Boolean
    get() = this == HARD_UPDATE || this == HARD_REDIRECT
}

/** The prompt to show for [app], ready for [io.github.raghavsatyadev.moreapps.ui.UpdateDialog]. */
public data class UpdatePrompt(val type: UpdateDialogType, val app: AppDetails) {
  internal val title: String
    get() =
      when (type) {
        UpdateDialogType.SOFT_UPDATE -> app.softUpdateDetails.dialogTitle
        UpdateDialogType.HARD_UPDATE -> app.hardUpdateDetails.dialogTitle
        UpdateDialogType.SOFT_REDIRECT,
        UpdateDialogType.HARD_REDIRECT -> app.redirectDetails.dialogTitle
        UpdateDialogType.NONE -> ""
      }

  internal val message: String
    get() =
      when (type) {
        UpdateDialogType.SOFT_UPDATE -> app.softUpdateDetails.dialogMessage
        UpdateDialogType.HARD_UPDATE -> app.hardUpdateDetails.dialogMessage
        UpdateDialogType.SOFT_REDIRECT,
        UpdateDialogType.HARD_REDIRECT -> app.redirectDetails.dialogMessage
        UpdateDialogType.NONE -> ""
      }

  internal val positiveButton: String
    get() =
      when (type) {
        UpdateDialogType.SOFT_UPDATE -> app.softUpdateDetails.positiveButton
        UpdateDialogType.HARD_UPDATE -> app.hardUpdateDetails.positiveButton
        UpdateDialogType.SOFT_REDIRECT,
        UpdateDialogType.HARD_REDIRECT -> app.redirectDetails.positiveButton
        UpdateDialogType.NONE -> ""
      }

  /** Empty for blocking prompts, which have no dismiss button. */
  internal val negativeButton: String
    get() =
      when (type) {
        UpdateDialogType.SOFT_UPDATE -> app.softUpdateDetails.negativeButton
        UpdateDialogType.SOFT_REDIRECT -> app.redirectDetails.negativeButton
        else -> ""
      }

  /** Where the positive button goes: the redirect target, or this app's store page. */
  public val link: String
    get() = if (type.isRedirect) app.redirectDetails.appLink else app.appLink
}

internal val UpdateDialogType.isRedirect: Boolean
  get() = this == UpdateDialogType.SOFT_REDIRECT || this == UpdateDialogType.HARD_REDIRECT
