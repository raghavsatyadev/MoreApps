package io.github.raghavsatyadev.moreapps.data

/** Small persistent store: SharedPreferences on Android, java.util.prefs on desktop. */
internal interface KeyValueStore {
  fun getString(key: String): String?

  fun putString(key: String, value: String)

  fun getInt(key: String, default: Int): Int

  fun putInt(key: String, value: Int)

  fun getLong(key: String, default: Long): Long

  fun putLong(key: String, value: Long)

  fun getBoolean(key: String, default: Boolean): Boolean

  fun putBoolean(key: String, value: Boolean)
}

/** Keys and file name kept from 2.x, so upgrading apps keep their cache and counts. */
internal object StoreKeys {
  const val FILE = "MORE_APPS"
  const val MORE_APPS = "MORE_APPS"
  const val IS_FIRST_TIME_PERIODIC = "IS_FIRST_TIME_PERIODIC"
  const val DIALOG_SHOW_COUNT = "DIALOG_SHOW_COUNT"
  const val NOTIFICATION_SHOW_COUNT = "NOTIFICATION_SHOW_COUNT"

  /** The `current_version` the dialog count belongs to (2.x name, now dialog-only). */
  const val CURRENT_VERSION = "CURRENT_VERSION"

  /** New in 3.0: the `current_version` the notification count belongs to. */
  const val NOTIFICATION_VERSION = "NOTIFICATION_VERSION"
}

/** In-memory store, for tests and previews. */
internal class MemoryStore : KeyValueStore {
  private val values = mutableMapOf<String, Any>()

  override fun getString(key: String): String? = values[key] as? String

  override fun putString(key: String, value: String) {
    values[key] = value
  }

  override fun getInt(key: String, default: Int): Int = values[key] as? Int ?: default

  override fun putInt(key: String, value: Int) {
    values[key] = value
  }

  override fun getLong(key: String, default: Long): Long = values[key] as? Long ?: default

  override fun putLong(key: String, value: Long) {
    values[key] = value
  }

  override fun getBoolean(key: String, default: Boolean): Boolean =
    values[key] as? Boolean ?: default

  override fun putBoolean(key: String, value: Boolean) {
    values[key] = value
  }
}
