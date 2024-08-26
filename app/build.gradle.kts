plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
//    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") version "1.9.23-1.0.20"
}

android {
    namespace = "com.example.quizletappandroidv1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.quizletappandroidv1"
        minSdk = 28
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
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
//        jvmTarget = "1.8"
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)


    val nav_version = "2.7.7"

    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    implementation("com.github.rizmaulana:floating-slideup-sheet:0.1.0")
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
//    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
//    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")


    implementation("com.jakewharton.timber:timber:4.7.1")


    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt("androidx.room:room-compiler:$room_version")



    implementation("com.github.yuyakaido:cardstackview:2.3.4")
    implementation("com.github.amsiq:swipereveallayout:1.4.1-x")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")


//    implementation ("com.ismaeldivita.chipnavigation:chip-navigation-bar:1.3.4")
    implementation("com.github.nguyenhoanglam:ImagePicker:1.6.2")

    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.github.dhaval2404:imagepicker:2.1")

    // To recognize Latin script
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // To recognize Chinese script
    implementation("com.google.mlkit:text-recognition-chinese:16.0.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

    // To recognize Chinese script
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-chinese:16.0.0")

//    implementation("com.itextpdf:itextg:5.5.10")
//    implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")

    implementation("org.apache.poi:poi:5.0.0")
//    implementation("com.github.naimdjon:docx4j-ImportXHTML:8.2.1-li-SNAPSHOT")
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    implementation("com.google.mlkit:translate:17.0.2")
    implementation("com.google.android.gms:play-services-mlkit-language-id:17.0.0")

    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
//    implementation("at.favre.lib", "bcrypt", "0.10.2")


    //    Hilt
    implementation(libs.hilt.android)
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.android.compiler)

    implementation ("com.jakewharton.timber:timber:5.0.1")


    implementation("me.relex:circleindicator:2.1.6")
    implementation("com.airbnb.android:lottie:3.5.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}