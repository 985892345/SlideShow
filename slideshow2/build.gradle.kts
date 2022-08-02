plugins {
  id("com.android.library")
  kotlin("android")
  `maven-publish`
}

android {
  compileSdk = 31
  
  defaultConfig {
    minSdk = 21
    targetSdk = 31
    
    consumerProguardFiles("consumer-rules.pro")
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
  publishing {
    singleVariant("release")
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.7.0")
  implementation("androidx.appcompat:appcompat:1.4.1")
  implementation("com.google.android.material:material:1.5.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

afterEvaluate {
  publishing {
    publications {
      // Creates a Maven publication called "release".
      create<MavenPublication>("release") {
        from(components["release"])
      }
//
//      repositories {
//        maven{
//          url = uri("$rootDir/maven")
//          group = "com.mredrock.team"
//          name = name
//          version = "cache"
//        }
//      }
    }
  }
}