package atsumi.android.appmanager.entity

import android.graphics.drawable.Drawable

data class AppInfo(
    val appName: String,
    val packageName: String,
    val appType: AppType,
    val appIcon: Drawable,
    val minSdkVersionText: String,
    val targetSdkVersion: Int
)
