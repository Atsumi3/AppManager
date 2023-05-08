package atsumi.android.appmanager.ui.app_info

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import atsumi.android.appmanager.R
import atsumi.android.appmanager.databinding.ActivityAppInfoListBinding
import atsumi.android.appmanager.entity.AppInfo
import atsumi.android.appmanager.extensions.installedApplicationInfoList
import atsumi.android.appmanager.extensions.showUninstallConfirmDialog
import atsumi.android.appmanager.util.DisplayCondition

class AppInfoActivity : ComponentActivity() {
    private lateinit var binding: ActivityAppInfoListBinding

    private val adapter: AppInfoListAdapter by lazy {
        AppInfoListAdapter().also {
            it.listener = object : AppInfoListAdapter.Listener {
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
        binding = ActivityAppInfoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

}
