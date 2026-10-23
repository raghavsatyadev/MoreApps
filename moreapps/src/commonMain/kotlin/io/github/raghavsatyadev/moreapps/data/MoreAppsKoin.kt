package io.github.raghavsatyadev.moreapps.data

import io.github.raghavsatyadev.moreapps.MoreAppsConfig
import io.github.raghavsatyadev.moreapps.update.SoftUpdateLimiter
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import kotlin.concurrent.Volatile
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module

/**
 * The library's own Koin container, kept apart from any Koin the host app starts, so neither can
 * override or close the other's definitions.
 */
internal object MoreAppsKoin {
  @Volatile private var app: KoinApplication? = null

  val isStarted: Boolean
    get() = app != null

  val application: KoinApplication
    get() = checkNotNull(app) { "Call MoreApps.initialize(...) before using MoreApps." }

  inline fun <reified T : Any> get(): T = application.koin.get()

  /** Starts the container, or replaces its config when the host initializes again. */
  fun start(config: MoreAppsConfig, store: KeyValueStore) {
    val current = app
    if (current != null) {
      current.koin.loadModules(listOf(configModule(config)), allowOverride = true)
      return
    }
    app = koinApplication {
      allowOverride(true)
      modules(
        module {
          single { store }
          single { createHttpClient() }
          single { MoreAppsRepository(get(), get()) }
          single { SoftUpdateLimiter(get()) }
        },
        configModule(config),
      )
    }
  }

  private fun configModule(config: MoreAppsConfig) = module { single { config } }

  private fun createHttpClient() = HttpClient {
    expectSuccess = false
    install(UserAgent) { agent = "MoreApps/3" }
    install(HttpTimeout) {
      requestTimeoutMillis = 20_000
      connectTimeoutMillis = 15_000
    }
  }
}
