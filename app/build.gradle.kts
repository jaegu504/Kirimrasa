
plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
}

android {
    namespace = "com.wabulk.sender"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.wabulk.sender"
        minSdk = 26
        targetSdk = 36
        versionCode = 18
        versionName = "2.0.1"
    }

    // From v2.0.0 releases are signed with the Kirimrasa release key (~/Documents/Kirimrasa-signing,
    // passwords in ~/.gradle/gradle.properties). 1.x stays on the debug key so in-place updates keep working.
    val usesReleaseKey = (defaultConfig.versionName ?: "0").substringBefore('.').toInt() >= 2
    signingConfigs {
        if (usesReleaseKey) create("release") {
            fun prop(name: String) = providers.gradleProperty(name).orNull
                ?: error("$name missing from ~/.gradle/gradle.properties: v2+ must be signed with the release key")
            storeFile = file(prop("KIRIMRASA_RELEASE_STORE_FILE"))
            storePassword = prop("KIRIMRASA_RELEASE_STORE_PASSWORD")
            keyAlias = prop("KIRIMRASA_RELEASE_KEY_ALIAS")
            keyPassword = prop("KIRIMRASA_RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName(if (usesReleaseKey) "release" else "debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      compose = true
      aidl = false
      buildConfig = true
      shaders = false
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.extended)
  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)
  // Instrumented tests
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // Local tests: jUnit, coroutines, Android runner
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)

  // Instrumented tests: jUnit rules and runners
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)

  // Navigation
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)

  // Room
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)
  testImplementation(libs.androidx.room.testing)

  // Serialization
  implementation(libs.kotlinx.serialization.json)

  // Network & Media
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging)
  implementation(libs.coil.compose)
}
