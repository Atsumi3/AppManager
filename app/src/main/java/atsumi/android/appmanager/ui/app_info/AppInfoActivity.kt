package atsumi.android.appmanager.ui.app_info

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import atsumi.android.appmanager.R
import atsumi.android.appmanager.databinding.ActivityAppInfoListBinding
import atsumi.android.appmanager.entity.AppInfo
import atsumi.android.appmanager.util.DisplayCondition

class AppInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppInfoListBinding

    private val adapter: AppInfoListAdapter by lazy {
        AppInfoListAdapter().also {
            it.listener = object : AppInfoListAdapter.Listener {
                override fun onItemClick(appInfo: AppInfo) {

                }

                override fun onAppUninstallClick(appInfo: AppInfo) {
                    showUnInstallConfirmDialog(appInfo)
                }
            }
        }
    }

    private val filterOver26 = object : DisplayCondition<AppInfo> {
        override fun isDisplayable(obj: AppInfo): Boolean {
            return obj.targetSdkVersion > 25
        }
    }

    private val filterManually = object : DisplayCondition<AppInfo> {
        override fun isDisplayable(obj: AppInfo): Boolean {
            return obj.appType == AppInfo.AppType.MANUALLY
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_info_list)
        setupList(binding.list)
        setupSpinner(binding.spinner)
    }

    private fun setupList(list: RecyclerView) {
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSpinner(spinner: Spinner) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedText = parent.getItemAtPosition(position) as String
                onFilterClicked(selectedText)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    private fun onFilterClicked(filterText: String) {
        when (filterText) {
            getString(R.string.spinner_over_26) -> {
                adapter.displayCondition = filterOver26
            }
            getString(R.string.spinner_manually) -> {
                adapter.displayCondition = filterManually
            }
            else -> {
                adapter.displayCondition = null
            }
        }
        updateData()
    }

    private fun updateData() {
        adapter.data = installedApplicationInfoList
    }

    private fun showUnInstallConfirmDialog(appInfo: AppInfo) {
        showUninstallConfirmDialog(appInfo)
    }

    private fun showUninstallConfirmDialog(appInfo: AppInfo) {
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

    private val installedApplicationInfoList: List<AppInfo>
        @SuppressLint("ObsoleteSdkInt")
        get() {
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
}
