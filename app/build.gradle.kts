import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

android {
    namespace = "org.kiteio.punica"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.kiteio.punica"
        minSdk = 29
        targetSdk = 35
        versionCode = 2
        versionName = "0.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.materialKolor)
    implementation(project(":request"))
    implementation(libs.tesseract4android)
    implementation(libs.ksoup)
    implementation(libs.markdown)
    implementation(libs.pdfViewer)
    implementation(libs.material.icons.extended)
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