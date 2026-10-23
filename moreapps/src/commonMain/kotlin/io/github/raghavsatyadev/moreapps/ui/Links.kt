package io.github.raghavsatyadev.moreapps.ui

import androidx.compose.ui.platform.UriHandler

/**
 * Opens [packageName] in the store app when [preferStore] is set (`market://`, Android only), else
 * [link] in the browser. Falls back to [link] when no app can handle the store URI.
 */
internal fun UriHandler.openApp(link: String, packageName: String, preferStore: Boolean) {
  if (preferStore && packageName.isNotBlank()) {
    val opened = runCatching { openUri("market://details?id=$packageName") }.isSuccess
    if (opened) return
  }
  if (link.isNotBlank()) runCatching { openUri(link) }
}
