plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
}

kotlin { jvmToolchain(21) }

dependencies {
  implementation(projects.sample.composeApp)
  implementation(projects.moreapps)
  implementation(compose.desktop.currentOs)
  implementation(libs.kotlinx.coroutines.swing)
}

compose {
  dependencyCompatibility { exclude("io.coil-kt.coil3") }
  desktop {
    application {
      mainClass = "com.rocky.moreapps.example.MainKt"
      nativeDistributions {
        packageName = "MoreApps"
        packageVersion = libs.versions.sampleVersionName.get()
      }
    }
  }
}
