package com.rocky.moreapps.example

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public object SampleConfig {
  /** The example list in this repository. */
  public const val JSON_URL: String =
    "https://raw.githubusercontent.com/raghavsatyadev/MoreApps/master/Resources/more_apps.json"

  public const val PACKAGE_NAME: String = "com.rocky.moreapps.example"
}

private val sampleModule = module { viewModelOf(::UpdaterViewModel) }

/** The sample's own Koin graph. The library keeps its own, so the two never clash. */
public fun startSampleKoin() {
  startKoin { modules(sampleModule) }
}
