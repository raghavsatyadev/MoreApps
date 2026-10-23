package io.github.raghavsatyadev.moreapps.data

import java.util.prefs.Preferences

/**
 * java.util.prefs node per app. Values over the 8 KB per-key limit are split into chunks, since the
 * app list JSON can be larger than that.
 */
internal class PreferencesStore(appId: String) : KeyValueStore {
  private val node: Preferences =
    Preferences.userRoot().node("io/github/raghavsatyadev/moreapps/${appId.replace('.', '_')}")

  override fun getString(key: String): String? {
    val chunks = node.getInt("$key.chunks", -1)
    if (chunks < 0) return null
    return buildString { repeat(chunks) { append(node.get("$key.$it", "")) } }
  }

  override fun putString(key: String, value: String) {
    val parts = value.chunked(CHUNK)
    parts.forEachIndexed { index, part -> node.put("$key.$index", part) }
    node.putInt("$key.chunks", parts.size)
    node.flush()
  }

  override fun getInt(key: String, default: Int): Int = node.getInt(key, default)

  override fun putInt(key: String, value: Int) = node.putInt(key, value)

  override fun getLong(key: String, default: Long): Long = node.getLong(key, default)

  override fun putLong(key: String, value: Long) = node.putLong(key, value)

  override fun getBoolean(key: String, default: Boolean): Boolean = node.getBoolean(key, default)

  override fun putBoolean(key: String, value: Boolean) = node.putBoolean(key, value)

  private companion object {
    const val CHUNK = Preferences.MAX_VALUE_LENGTH / 2
  }
}
