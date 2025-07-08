package jp.bizen.app.minimalist.ui.app_info

import android.graphics.drawable.Drawable
import jp.bizen.app.minimalist.entity.AppInfo

class AppInfoListContentViewModel(appInfo: AppInfo) {
    private var data: AppInfo = appInfo

    val packageName: String
        get() = data.packageName

    val appName: String
        get() = data.appName

    val appIcon: Drawable?
        get() = data.appIcon

    val minSdkText: String
        get() = "minSdk: ${data.minSdkVersionText}"

    val targetSdkText: String
        get() = "targetSdk: ${data.targetSdkVersion}"

    interface Listener {
        fun onAppUninstallClick(appInfo: AppInfo)
    }
}
