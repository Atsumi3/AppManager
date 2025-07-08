package atsumi.android.appmanager.entity

import android.graphics.drawable.Drawable
import java.lang.ref.WeakReference

data class AppInfo(
    val appName: String,
    val packageName: String,
    val appType: AppType,
    private val _appIcon: WeakReference<Drawable>,
    val minSdkVersionText: String,
    val targetSdkVersion: Int
) {
    constructor(
        appName: String,
        packageName: String,
        appType: AppType,
        appIcon: Drawable,
        minSdkVersionText: String,
        targetSdkVersion: Int
    ) : this(
        appName,
        packageName,
        appType,
        WeakReference(appIcon),
        minSdkVersionText,
        targetSdkVersion
    )
    
    val appIcon: Drawable?
        get() = _appIcon.get()
}