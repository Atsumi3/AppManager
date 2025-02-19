package atsumi.android.appmanager.ui.app_info

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import atsumi.android.appmanager.entity.AppInfo
import atsumi.android.appmanager.entity.AppType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val _appList = MutableStateFlow<List<AppInfo>>(emptyList())
    val appList: StateFlow<List<AppInfo>> = _appList

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val currentPackageName = getApplication<Application>().packageName

            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .mapNotNull { applicationInfo ->
                    // 自分自身は除外
                    if (applicationInfo.packageName == currentPackageName) return@mapNotNull null

                    val appType =
                        if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                            AppType.SYSTEM
                        } else {
                            AppType.MANUALLY
                        }

                    val packageInfo: PackageInfo = try {
                        pm.getPackageInfo(applicationInfo.packageName, 0)
                    } catch (e: PackageManager.NameNotFoundException) {
                        return@mapNotNull null
                    }

                    AppInfo(
                        appName = pm.getApplicationLabel(applicationInfo).toString(),
                        appIcon = pm.getApplicationIcon(applicationInfo),
                        packageName = packageInfo.packageName,
                        minSdkVersionText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            "${applicationInfo.minSdkVersion}"
                        } else {
                            "-"
                        },
                        targetSdkVersion = applicationInfo.targetSdkVersion,
                        appType = appType
                    )
                }
            _appList.value = apps
        }
    }
}
