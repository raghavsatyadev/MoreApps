package io.github.raghavsatyadev.moreapps.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/** The 2.x preferences file, so an upgraded app keeps its cached list and show counts. */
internal class SharedPrefsStore(context: Context) : KeyValueStore {
  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences(StoreKeys.FILE, Context.MODE_PRIVATE)

  override fun getString(key: String): String? = prefs.getString(key, null)

  override fun putString(key: String, value: String) = prefs.edit { putString(key, value) }

  override fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)

  override fun putInt(key: String, value: Int) = prefs.edit { putInt(key, value) }

  /** 2.x saved CURRENT_VERSION as an Int; read either. */
  override fun getLong(key: String, default: Long): Long =
    when (val value = prefs.all[key]) {
      is Long -> value
      is Int -> value.toLong()
      else -> default
    }

  override fun putLong(key: String, value: Long) = prefs.edit { putLong(key, value) }

  override fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)

  override fun putBoolean(key: String, value: Boolean) = prefs.edit { putBoolean(key, value) }
}
