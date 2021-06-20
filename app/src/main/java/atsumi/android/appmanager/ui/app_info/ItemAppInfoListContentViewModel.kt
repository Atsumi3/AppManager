package atsumi.android.appmanager.ui.app_info

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import atsumi.android.appmanager.R
import atsumi.android.appmanager.entity.AppInfo

class ItemAppInfoListContentViewModel(appInfo: AppInfo) : BaseObservable() {
    var data: AppInfo = appInfo
        set(value) {
            field = value
            notifyChange()
        }

    var listener: Listener? = null

    @get:Bindable
    val backgroundColor: Int
        get() {
            return if (data.appType == AppInfo.AppType.SYSTEM) {
                R.color.red
            } else {
                android.R.color.transparent
            }
        }

    @get:Bindable
    val packageName: String
        get() = data.packageName

    @get:Bindable
    val appName: String
        get() = data.appName

    @get:Bindable
    val appIcon: Drawable
        get() = data.appIcon

    @get:Bindable
    val minSdkText: String
        get() = "minSdk: ${data.minSdkVersionText}"

    @get:Bindable
    val targetSdkText: String
        get() = "targetSdk: ${data.targetSdkVersionText}"

    fun onItemClick(@Suppress("UNUSED_PARAMETER") view: View) {
        listener?.onItemClick(data)
    }

    fun onAppUninstallClick(@Suppress("UNUSED_PARAMETER") view: View) {
        listener?.onAppUninstallClick(data)
    }

    interface Listener {
        fun onItemClick(appInfo: AppInfo)

        fun onAppUninstallClick(appInfo: AppInfo)
    }
}
