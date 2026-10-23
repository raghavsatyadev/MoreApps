package io.github.raghavsatyadev.moreapps.data

import io.github.raghavsatyadev.moreapps.model.AppDetails
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlin.concurrent.Volatile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** Lenient on purpose: the cache may hold a 2.x Gson write with `null` detail objects. */
internal val MoreAppsJson = Json {
  ignoreUnknownKeys = true
  explicitNulls = false
  coerceInputValues = true
  isLenient = true
}

private val listSerializer = ListSerializer(AppDetails.serializer())

/** Downloads the app list and keeps the last good copy, raw, under the 2.x key. */
internal class MoreAppsRepository(private val client: HttpClient, private val store: KeyValueStore) {

  /** The list held in memory, so a dialog can show it on its first frame; null until loaded. */
  @Volatile
  var inMemory: List<AppDetails>? = null
    private set

  /**
   * The cached list; empty when nothing was saved yet or the saved copy cannot be read. Reading
   * and parsing run off the main thread, once; later calls answer from memory.
   */
  suspend fun cached(): List<AppDetails> =
    inMemory
      ?: withContext(Dispatchers.Default) {
        store.getString(StoreKeys.MORE_APPS)?.let(::decode).orEmpty().also { inMemory = it }
      }

  /** Fetches [url]; saves and returns the list, or the failure. The cache is untouched on errors. */
  suspend fun refresh(url: String): Result<List<AppDetails>> =
    try {
      val response = client.get(url)
      if (!response.status.isSuccess()) {
        Result.failure(IllegalStateException("HTTP ${response.status.value} from $url"))
      } else {
        val body = response.bodyAsText()
        val apps =
          withContext(Dispatchers.Default) {
            decode(body)?.also { store.putString(StoreKeys.MORE_APPS, body) }
          } ?: error("Not a MoreApps JSON array: $url")
        inMemory = apps
        Result.success(apps)
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Result.failure(e)
    }

  /** The cache when it has entries, otherwise a fresh download (an empty list if that fails). */
  suspend fun cachedOrFetch(url: String): List<AppDetails> =
    cached().ifEmpty { refresh(url).getOrDefault(emptyList()) }

  private fun decode(json: String): List<AppDetails>? =
    runCatching { MoreAppsJson.decodeFromString(listSerializer, json) }.getOrNull()
}
