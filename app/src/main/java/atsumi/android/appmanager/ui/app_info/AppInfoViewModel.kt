package atsumi.android.appmanager.ui.app_info

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadInstalledApps()
    }

    fun refreshApps() {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                _error.value = null
                
                val pm = getApplication<Application>().packageManager
                val currentPackageName = getApplication<Application>().packageName

                val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .mapNotNull { applicationInfo ->
                        try {
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
                                Log.w("AppInfoViewModel", "Package not found: ${applicationInfo.packageName}", e)
                                return@mapNotNull null
                            }

                            val appIcon = try {
                                pm.getApplicationIcon(applicationInfo)
                            } catch (e: Exception) {
                                Log.w("AppInfoViewModel", "Failed to get icon for ${applicationInfo.packageName}", e)
                                // デフォルトのアイコンを取得
                                pm.defaultActivityIcon
                            }

                            AppInfo(
                                appName = pm.getApplicationLabel(applicationInfo).toString(),
                                appIcon = appIcon,
                                packageName = packageInfo.packageName,
                                minSdkVersionText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    "${applicationInfo.minSdkVersion}"
                                } else {
                                    "-"
                                },
                                targetSdkVersion = applicationInfo.targetSdkVersion,
                                appType = appType
                            )
                        } catch (e: Exception) {
                            Log.w("AppInfoViewModel", "Error processing app: ${applicationInfo.packageName}", e)
                            null
                        }
                    }
                _appList.value = apps
            } catch (e: Exception) {
                Log.e("AppInfoViewModel", "Failed to load installed apps", e)
                _error.value = "アプリ一覧の読み込みに失敗しました: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
