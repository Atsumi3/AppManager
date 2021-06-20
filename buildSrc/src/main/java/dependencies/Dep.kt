package dependencies

class Dep {
    object GradlePlugin {
        const val android = "com.android.tools.build:gradle:4.2.1"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
    }

    object Kotlin {
        const val version = "1.5.10"
        const val stdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.5.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val material = "com.google.android.material:material:1.2.0"
    }
}
