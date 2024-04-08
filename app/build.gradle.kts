plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")

}

android {
    namespace = "com.example.pointofsales"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pointofsales"
        minSdk = 27
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

    }
    kotlin {
        jvmToolchain(17)
    }



    kotlinOptions {
        jvmTarget = "17"

    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.test:monitor:1.6.1")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    androidTestImplementation("org.testng:testng:6.9.6")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0")

    //lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")


    // AndroidX Test Core library
    androidTestImplementation("androidx.test:core:1.4.0")

    // AndroidX Test JUnit Runner
    androidTestImplementation("androidx.test.ext:junit:1.1.3")

    // JUnit Jupiter API for Android
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")

    // JUnit Jupiter Engine for Android
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")



    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")

    //Navigation
    val nav_version = "2.7.6"

    // Java language implementation
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    val lottieVersion = "3.4.0"
    implementation ("com.airbnb.android:lottie:$lottieVersion")

    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))

    implementation("com.google.firebase:firebase-analytics")


    //BarChart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.squareup.picasso:picasso:2.8")








}