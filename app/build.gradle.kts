plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  compileSdk = 31
  
  defaultConfig {
    applicationId = "com.ndhzs.slideshowdemo"
    minSdk = 21
    targetSdk = 31
    versionCode = 1
    versionName = "1.0"
  }
  
  buildTypes {
    release {
      isMinifyEnabled = true
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
  buildFeatures {
    dataBinding = true
  }
}

dependencies {
//  implementation(project(":slideshow"))
//  implementation(project(":slideshow2"))
  
  implementation("com.github.985892345:SlideShow:2.0.0-alpha1")
  
  implementation("androidx.core:core-ktx:1.7.0")
  implementation("androidx.appcompat:appcompat:1.4.1")
  implementation("com.google.android.material:material:1.5.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.3")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}