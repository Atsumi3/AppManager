package dependencies

class Dep {
    object GradlePlugin {
        const val android = "com.android.tools.build:gradle:4.2.1"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20"
        const val releaseHub = "com.dipien:releases-hub-gradle-plugin:2.0.2"
    }

    object Kotlin {
        const val stdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.20"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.0.0"
        const val material = "com.google.android.material:material:1.4.0"
    }

    val waveView = "com.gelitenight.waveview:waveview:1.0.0"
}
