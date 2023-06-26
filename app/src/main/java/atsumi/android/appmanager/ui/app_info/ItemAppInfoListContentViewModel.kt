package atsumi.android.appmanager.ui.app_info

import android.graphics.drawable.Drawable
import atsumi.android.appmanager.entity.AppInfo

class ItemAppInfoListContentViewModel(appInfo: AppInfo) {
    var data: AppInfo = appInfo

    val packageName: String
        get() = data.packageName

    val appName: String
        get() = data.appName

    val appIcon: Drawable
        get() = data.appIcon

    val minSdkText: String
        get() = "minSdk: ${data.minSdkVersionText}"

    val targetSdkText: String
        get() = "targetSdk: ${data.targetSdkVersionText}"

    interface Listener {
        fun onAppUninstallClick(appInfo: AppInfo)
    }
}
