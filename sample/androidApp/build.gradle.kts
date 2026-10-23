plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
}

android {
  namespace = libs.versions.sampleNameSpace.get()
  compileSdk {
    version =
      release(libs.versions.compileSdk.get().toInt()) {
        minorApiLevel = libs.versions.compileSdkMinor.get().toInt()
      }
  }
  buildToolsVersion = libs.versions.buildTools.get()

  defaultConfig {
    applicationId = libs.versions.sampleAppId.get()
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = libs.versions.sampleVersionCode.get().toInt()
    versionName = libs.versions.sampleVersionName.get()
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }

  buildFeatures { compose = true }
}

kotlin { jvmToolchain(21) }

dependencies {
  implementation(projects.sample.composeApp)
  implementation(projects.moreapps)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core)
}
