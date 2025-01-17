plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.lh.superresolutiongilde"
    compileSdk = 33

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")




    //图片加载库  glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    // Skip this if you don't want to use integration libraries or configure Glide.
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    //工具类
    implementation("com.blankj:utilcodex:1.31.1")

//    implementation(project(mapOf("path" to ":super-resolution-core")))
    implementation ("com.github.shougonyiren:superresolutioncore:0.0.6")



}