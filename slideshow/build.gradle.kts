plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  `maven-publish`
  signing
}

android {
  compileSdk = 32
  
  defaultConfig {
    minSdk = 21
    targetSdk = 32
    
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }
  
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
  implementation("androidx.core:core-ktx:1.8.0")
  implementation("androidx.appcompat:appcompat:1.4.2")
  implementation("com.google.android.material:material:1.6.1")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

group = "io.github.985892345"
version = "2.0.0-SNAPSHOT"
val projectArtifact = "SlideShow"
val projectGithubName = projectArtifact
val projectDescription = "ViewPager2的整合库，可以更方便的用于轮播图。部分代码参考了第三方库Banner"

android {
  publishing {
    singleVariant("release") {
      withJavadocJar()
      withSourcesJar()
    }
  }
}

afterEvaluate {
  publishing {
    publications {
      create<MavenPublication>("release") {
        groupId = project.group.toString()
        artifactId = projectArtifact
        version = project.version.toString()
        from(components["release"])
        signing {
          sign(this@create)
        }

        pom {
          name.set(projectArtifact)
          description.set(projectDescription)
          url.set("https://github.com/985892345/$projectGithubName")

          licenses {
            license {
              name.set("Apache-2.0 license")
              url.set("https://github.com/985892345/$projectGithubName/blob/main/LICENSE")
            }
          }

          developers {
            developer {
              id.set("985892345")
              name.set("GuoXiangrui")
              email.set("guo985892345@formail.com")
            }
          }

          scm {
            connection.set("https://github.com/985892345/$projectGithubName.git")
            developerConnection.set("https://github.com/985892345/$projectGithubName.git")
            url.set("https://github.com/985892345/$projectGithubName")
          }
        }
      }
      repositories {
        maven {
          // https://s01.oss.sonatype.org/
          name = "mavenCentral" // 点击 publishReleasePublicationToMavenCentralRepository 发布到 mavenCentral
          val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
          val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
          setUrl(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
          credentials {
            username = project.properties["mavenCentralUsername"].toString()
            password = project.properties["mavenCentralPassword"].toString()
          }
        }
      }
    }
  }
}
