import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.multiplatform.library)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  android {
    namespace = libs.versions.sampleSharedNameSpace.get()
    compileSdk {
      version =
        release(libs.versions.compileSdk.get().toInt()) {
          minorApiLevel = libs.versions.compileSdkMinor.get().toInt()
        }
    }
    minSdk = libs.versions.minSdk.get().toInt()
    androidResources.enable = true
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
  }

  jvm { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

  jvmToolchain(21)

  sourceSets {
    commonMain.dependencies {
      implementation(projects.moreapps)
      implementation(libs.bundles.compose.mp)
      implementation(libs.lifecycle.runtime.compose.mp)
      implementation(libs.lifecycle.viewmodel.compose.mp)
      implementation(libs.koin.core)
      implementation(libs.koin.compose.mp)
      implementation(libs.koin.compose.viewmodel.mp)
    }
  }
}

compose {
  // Coil (pulled in by :moreapps) is built against an older Skiko; the runtime uses CMP's.
  dependencyCompatibility { exclude("io.coil-kt.coil3") }
}

compose.resources { packageOfResClass = "com.rocky.moreapps.example.resources" }
