import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.multiplatform.library)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.maven.publish)
}

kotlin {
  explicitApi()

  android {
    namespace = libs.versions.libraryNameSpace.get()
    compileSdk {
      version =
        release(libs.versions.compileSdk.get().toInt()) {
          minorApiLevel = libs.versions.compileSdkMinor.get().toInt()
        }
    }
    minSdk = libs.versions.minSdk.get().toInt()
    androidResources.enable = true
    optimization { consumerKeepRules.file("consumer-rules.pro") }
    withHostTest {}
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
  }

  jvm { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

  jvmToolchain(21)

  sourceSets {
    commonMain.dependencies {
      implementation(libs.bundles.compose.mp)
      implementation(libs.ktor.client.core)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.kotlinx.collections.immutable)
      implementation(libs.lifecycle.runtime.compose.mp)
      implementation(libs.koin.core)
      implementation(libs.coil.compose.mp)
      implementation(libs.coil.network.ktor.mp)
    }
    androidMain.dependencies {
      implementation(libs.ktor.client.okhttp)
      implementation(libs.androidx.core)
      implementation(libs.androidx.work.runtime)
      implementation(libs.koin.android)
    }
    jvmMain.dependencies { implementation(libs.ktor.client.okhttp) }
    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.ktor.client.mock)
    }
  }
}

compose {
  // Coil 3.6 is built against an older Skiko than CMP 1.13 ships; the runtime uses CMP's.
  dependencyCompatibility { exclude("io.coil-kt.coil3") }
}

compose.resources {
  packageOfResClass = "io.github.raghavsatyadev.moreapps.resources"
  publicResClass = false
}

mavenPublishing {
  coordinates(
    libs.versions.libraryGroup.get(),
    libs.versions.libraryArtifact.get(),
    libs.versions.libraryVersion.get(),
  )
  publishToMavenCentral(automaticRelease = true)
  // Signs only when a key is supplied (CI), so local publishToMavenLocal still works.
  if (providers.gradleProperty("signingInMemoryKey").isPresent) signAllPublications()
}
