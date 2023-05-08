package atsumi.android.appmanager.ui.app_info

import android.graphics.drawable.Drawable
import android.view.View
import atsumi.android.appmanager.R
import atsumi.android.appmanager.entity.AppInfo

class ItemAppInfoListContentViewModel(appInfo: AppInfo) {
    var data: AppInfo = appInfo

    var listener: Listener? = null

    val packageNameBackgroundColor: Int
        get() {
            return if (data.appType == AppInfo.AppType.SYSTEM) {
                R.color.red
            } else {
                android.R.color.transparent
            }
        }

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

    fun onAppUninstallClick(@Suppress("UNUSED_PARAMETER") view: View) {
        listener?.onAppUninstallClick(data)
    }

    interface Listener {
        fun onAppUninstallClick(appInfo: AppInfo)
    }
}
