package io.github.raghavsatyadev.moreapps

import io.github.raghavsatyadev.moreapps.data.KeyValueStore
import io.github.raghavsatyadev.moreapps.data.MoreAppsKoin
import io.github.raghavsatyadev.moreapps.data.MoreAppsRepository
import io.github.raghavsatyadev.moreapps.model.AppDetails
import io.github.raghavsatyadev.moreapps.model.UpdateDialogType
import io.github.raghavsatyadev.moreapps.model.UpdatePrompt
import io.github.raghavsatyadev.moreapps.update.SoftUpdateChannel
import io.github.raghavsatyadev.moreapps.update.SoftUpdateLimiter
import io.github.raghavsatyadev.moreapps.update.decideDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * What the library needs to know about the host app.
 *
 * @param jsonUrl the hosted JSON array of apps (see the README for the format).
 * @param packageName this app's id, used to find its own entry for update prompts.
 * @param versionCode the installed version, compared with `min_version` and `current_version`.
 */
public data class MoreAppsConfig(
  val jsonUrl: String,
  val packageName: String,
  val versionCode: Long,
)

/**
 * Entry point. Call the platform `MoreApps.initialize(...)` once (Android: `Application.onCreate`),
 * then show [io.github.raghavsatyadev.moreapps.ui.MoreAppsDialog] or
 * [io.github.raghavsatyadev.moreapps.ui.UpdatePromptHost].
 */
public object MoreApps {

  private val warmUpScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  internal fun start(config: MoreAppsConfig, store: KeyValueStore) {
    MoreAppsKoin.start(config, store)
    // Load the saved list now, so the dialog opens with it instead of a loading state.
    warmUpScope.launch { repository.cached() }
  }

  public val config: MoreAppsConfig
    get() = MoreAppsKoin.get()

  private val repository: MoreAppsRepository
    get() = MoreAppsKoin.get()

  private val limiter: SoftUpdateLimiter
    get() = MoreAppsKoin.get()

  /** Downloads the list now and caches it. */
  public suspend fun refresh(): Result<List<AppDetails>> = repository.refresh(config.jsonUrl)

  /** The cached list, downloading it first when the cache is empty. */
  public suspend fun apps(): List<AppDetails> = repository.cachedOrFetch(config.jsonUrl)

  /** This app's own entry in the list, if the JSON has one. */
  public suspend fun currentApp(): AppDetails? =
    apps().firstOrNull { it.packageName == config.packageName }

  /** The prompt the JSON asks for right now, ignoring soft-update show limits. */
  public suspend fun dialogToShow(): UpdateDialogType =
    decideDialog(currentApp(), config.versionCode)

  /**
   * The prompt to show now, or `null`. Soft updates respect `dialog_show_count`; call
   * [markPromptShown] once one is actually displayed.
   */
  public suspend fun pendingPrompt(): UpdatePrompt? {
    val app = currentApp() ?: return null
    val type = decideDialog(app, config.versionCode)
    if (type == UpdateDialogType.NONE) return null
    if (type == UpdateDialogType.SOFT_UPDATE) {
      val soft = app.softUpdateDetails
      if (!limiter.shouldShow(SoftUpdateChannel.DIALOG, soft.dialogShowCount, app.currentVersion)) {
        return null
      }
    }
    return UpdatePrompt(type, app)
  }

  /** Counts a displayed soft-update dialog towards `dialog_show_count`. */
  public fun markPromptShown(prompt: UpdatePrompt) {
    if (prompt.type == UpdateDialogType.SOFT_UPDATE) limiter.recordShown(SoftUpdateChannel.DIALOG)
  }

  /** The list already in memory, or null; lets the dialog skip its loading state. */
  internal fun appsInMemory(): List<AppDetails>? =
    if (MoreAppsKoin.isStarted) repository.inMemory?.takeIf { it.isNotEmpty() } else null

  internal fun appsToList(apps: List<AppDetails>, hidePackages: Set<String>): List<AppDetails> =
    apps.filter { it.showInDialog && it.packageName !in hidePackages }
}
