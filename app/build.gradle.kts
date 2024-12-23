import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

android {
    namespace = "org.kiteio.punica"
    compileSdk = 35

    defaultConfig {
        applicationId = namespace
        minSdk = 29
        targetSdk = 35
        versionCode = 5
        versionName = "0.0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    applicationVariants.all {
        outputs.all {
            if (this is ApkVariantOutputImpl) {
                // punica-[Build]-[Version].apk
                outputFileName =
                    "punica-${defaultConfig.versionCode}-${defaultConfig.versionName}.apk"
            }
        }
    }
    splits {
        abi {
            isEnable = true
            reset()
            // noinspection ChromeOsAbiSupport
            include("arm64-v8a")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions.jvmTarget = "21"
    buildFeatures.compose = true
}

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.materialKolor)
    implementation(project(":request"))
    implementation(libs.tesseract4android)
    implementation(libs.ksoup)
    implementation(libs.markdown)
    implementation(libs.pdfViewer)
    implementation(libs.navigation)
    implementation(libs.datastore)
    implementation(libs.serialization.json)
    implementation(libs.composeIcons.simple)
    implementation(libs.composeIcons.tabler)
    implementation(libs.coil.compose)
    implementation(libs.palette)
    implementation(libs.paging)
    implementation(libs.paging.compose)
    implementation(libs.qrose)
    implementation(libs.colorPicker)
}