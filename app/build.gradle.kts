import com.android.build.api.artifact.SingleArtifact

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.modi.dynamic"
    compileSdk = 32

    defaultConfig {
        applicationId = "com.modi.dynamic"
        minSdk = 24
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(project(mapOf("path" to ":dySolib")))
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.android.tools.build:gradle:7.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

androidComponents.finalizeDsl { applicationExtension ->

    applicationExtension.packagingOptions {
        jniLibs {

        }
    }

}
androidComponents.beforeVariants { variant ->


}
androidComponents.onVariants { variant ->
    println("onVariant---${variant.name}")
    val abi = arrayListOf("lib/arm64-v8a/", "lib/armeabi-v7a/", "lib/x86/", "lib/x86_64/")
    val excludeSo = arrayListOf("libA.so", "libB.so")

    if (variant.name.contains("release", true)) {
//        println("${variant.externalNativeBuild?.abiFilters?.get()}")
        abi.forEach { abiPath ->
            excludeSo.forEach { so ->
                println("$abiPath$so")
                variant.packaging.jniLibs.excludes.add("$abiPath$so")
            }
        }
    }
}

