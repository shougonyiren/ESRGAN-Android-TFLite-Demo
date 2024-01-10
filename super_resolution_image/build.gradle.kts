plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.lh.SuperResolutionImage"
    compileSdkVersion(34)

    defaultConfig {
        minSdkVersion(28)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
//    implementation(project(mapOf("path" to ":app"))) 不依赖主项目
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.6.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //图片加载库
    implementation("io.coil-kt:coil:2.2.2")

    //工具类
    implementation("com.blankj:utilcodex:1.31.1")
    //kotlinx-coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")


    implementation("com.google.guava:guava:30.0-android")


    //华为
    implementation ("com.huawei.hiai.hiai-engine:huawei-hiai-vision:11.0.2.300")
    implementation ("com.huawei.hiai.hiai-engine:huawei-hiai-pdk:11.0.2.300")
    implementation ("com.huawei.hiai.hiai-engine:huawei-hiai-nlu:11.0.2.300")
    implementation ("com.huawei.hiai.hiai-engine:huawei-hiai-asr:11.0.2.300")
}