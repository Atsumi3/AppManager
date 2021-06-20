package atsumi.android.appmanager.entity

import android.graphics.drawable.Drawable

class AppInfo private constructor(
    val appName: String,
    val packageName: String,
    val appType: AppType,
    val appIcon: Drawable,
    val minSdkVersionText: String,
    val targetSdkVersion: Int
) {
    companion object {
        fun from(
            appName: String,
            appType: AppType,
            appIcon: Drawable,
            packageName: String,
            minSdkVersionText: String,
            targetSdkVersion: Int
        ): AppInfo {
            return AppInfo(
                appName = appName,
                appType = appType,
                appIcon = appIcon,
                packageName = packageName,
                minSdkVersionText = minSdkVersionText,
                targetSdkVersion = targetSdkVersion
            )
        }
    }

    val targetSdkVersionText: String
        get() = "$targetSdkVersion"

    enum class AppType {
        SYSTEM, MANUALLY
    }
}