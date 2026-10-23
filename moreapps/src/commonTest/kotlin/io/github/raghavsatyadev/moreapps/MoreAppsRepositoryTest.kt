package io.github.raghavsatyadev.moreapps

import io.github.raghavsatyadev.moreapps.data.MemoryStore
import io.github.raghavsatyadev.moreapps.data.MoreAppsRepository
import io.github.raghavsatyadev.moreapps.data.StoreKeys
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MoreAppsRepositoryTest {
  /** What 2.x's Gson wrote: null detail objects and an unknown field. */
  private val legacyJson =
    """[{"name":"A","package_name":"a","rating":4.5,"min_version":3,"current_version":4,
       "show_in_dialog":true,"redirect_details":null,"soft_update_details":null,
       "hard_update_details":{"enable":true},"extra":1}]"""

  @Test
  fun readsLegacyCacheWithNulls() = runTest {
    val store = MemoryStore().apply { putString(StoreKeys.MORE_APPS, legacyJson) }
    val client = HttpClient(MockEngine { respondError(HttpStatusCode.NotFound) })
    val apps = MoreAppsRepository(client, store).cached()
    assertEquals(1, apps.size)
    assertFalse(apps[0].redirectDetails.enable)
    assertTrue(apps[0].hardUpdateDetails.enable)
  }

  @Test
  fun refreshSavesBodyAndKeepsCacheOnError() = runTest {
    val store = MemoryStore()
    val ok = MoreAppsRepository(HttpClient(MockEngine { respond(legacyJson) }), store)
    assertEquals(1, ok.refresh("https://x").getOrThrow().size)

    val failing =
      MoreAppsRepository(HttpClient(MockEngine { respondError(HttpStatusCode.NotFound) }), store)
    assertTrue(failing.refresh("https://x").isFailure)
    assertEquals(1, failing.cached().size)
  }

  @Test
  fun garbageBodyIsAFailureNotACrash() = runTest {
    val store = MemoryStore()
    val repo = MoreAppsRepository(HttpClient(MockEngine { respond("<html>404</html>") }), store)
    assertTrue(repo.refresh("https://x").isFailure)
    assertEquals(null, store.getString(StoreKeys.MORE_APPS))
  }
}
