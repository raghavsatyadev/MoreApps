package io.github.raghavsatyadev.moreapps

import io.github.raghavsatyadev.moreapps.data.PreferencesStore

/**
 * Desktop setup. Desktop apps have no package manager, so pass the id and version the JSON entry
 * should be compared with.
 */
public fun MoreApps.initialize(jsonUrl: String, packageName: String, versionCode: Long) {
  start(MoreAppsConfig(jsonUrl, packageName, versionCode), PreferencesStore(packageName))
}
