package atsumi.android.appmanager.ui.app_info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import atsumi.android.appmanager.R
import atsumi.android.appmanager.entity.AppInfo
import atsumi.android.appmanager.entity.AppType
import atsumi.android.appmanager.util.DisplayCondition
import kotlinx.coroutines.launch

class AppInfoActivity : ComponentActivity() {

    private val viewModel: AppInfoViewModel by lazy {
        ViewModelProvider(this)[AppInfoViewModel::class.java]
    }
    
    private val adapter: AppInfoListAdapter by lazy {
        AppInfoListAdapter().also {
            it.listener = object : AppInfoListAdapter.Listener {
                override fun onAppUninstallClick(appInfo: AppInfo) {
                    showUnInstallConfirmDialog(appInfo)
                }
            }
        }
    }
    
    private var progressBar: ProgressBar? = null

    private val filterOver26 = object : DisplayCondition<AppInfo> {
        override fun isDisplayable(obj: AppInfo): Boolean {
            return obj.targetSdkVersion > 25
        }
    }

    private val filterManually = object : DisplayCondition<AppInfo> {
        override fun isDisplayable(obj: AppInfo): Boolean {
            return obj.appType == AppType.MANUALLY
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info_list)
        setupList(findViewById(R.id.list))
        setupSpinner(findViewById(R.id.spinner))
        progressBar = findViewById(R.id.progress_bar)
        observeViewModel()
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
        viewModel.refreshApps()
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
        // ViewModelから最新のデータを再取得
        adapter.data = viewModel.appList.value
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // アプリリストの監視
                launch {
                    viewModel.appList.collect { appList ->
                        adapter.data = appList
                    }
                }
                
                // ローディング状態の監視
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }
                
                // エラー状態の監視
                launch {
                    viewModel.error.collect { error ->
                        error?.let {
                            Toast.makeText(this@AppInfoActivity, it, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun showUnInstallConfirmDialog(appInfo: AppInfo) {
        val uri = Uri.fromParts("package", appInfo.packageName, null)
        startActivity(Intent(Intent.ACTION_DELETE, uri))
    }

}
