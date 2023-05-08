package atsumi.android.appmanager.extensions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import atsumi.android.appmanager.entity.AppInfo

val Context.installedApplicationInfoList: List<AppInfo>
    @SuppressLint("ObsoleteSdkInt") get() {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .mapNotNull { applicationInfo ->
                // 自分自身は除外
                if (applicationInfo.packageName == packageName) {
                    return@mapNotNull null
                }

                val appType =
                    if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == ApplicationInfo.FLAG_SYSTEM) {
                        AppInfo.AppType.SYSTEM
                    } else AppInfo.AppType.MANUALLY

                // パッケージの情報取得
                val packageInfo: PackageInfo = try {
                    packageManager.getPackageInfo(applicationInfo.packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    return@mapNotNull null
                }

                AppInfo.from(
                    appName = packageManager.getApplicationLabel(applicationInfo).toString(),
                    appType = appType,
                    appIcon = packageManager.getApplicationIcon(applicationInfo),
                    packageName = packageInfo.packageName,
                    minSdkVersionText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        "${applicationInfo.minSdkVersion}"
                    } else {
                        "-"
                    },
                    targetSdkVersion = applicationInfo.targetSdkVersion
                )
            }
    }

fun Context.showUninstallConfirmDialog(appInfo: AppInfo) {
    AlertDialog.Builder(this)
        .setTitle("確認")
        .setMessage(String.format("本当に %s をアンインストールしますか?", appInfo.appName))
        .setPositiveButton("はい") { _, _ ->
            val uri = Uri.fromParts("package", appInfo.packageName, null)
            startActivity(Intent(Intent.ACTION_DELETE, uri))
        }
        .setNegativeButton("キャンセル", null)
        .create().show()
}
