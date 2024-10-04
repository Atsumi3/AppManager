package atsumi.android.appmanager.ui.app_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import atsumi.android.appmanager.R
import atsumi.android.appmanager.entity.AppInfo
import atsumi.android.appmanager.util.DisplayCondition

internal class AppInfoItemCallback : DiffUtil.ItemCallback<AppInfo>() {
    override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
        return oldItem == newItem
    }
}

class AppInfoListAdapter :
    ListAdapter<AppInfo, AppInfoListAdapter.ContentViewHolder>(AppInfoItemCallback()) {
    var displayCondition: DisplayCondition<AppInfo>? = null
        set(value) {
            field = value
            updateItems()
        }

    var data: List<AppInfo> = emptyList()
        set(value) {
            field = value
            updateItems()
        }

    var listener: Listener? = null

    private fun updateItems() {
        submitList(
            if (displayCondition != null) {
                data.filter { displayCondition!!.isDisplayable(it) }
            } else {
                data
            }
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_app_info_list_content,
                parent,
                false
            ),
            object : ContentViewHolder.Listener {
                override fun onAppUninstallClick(appInfo: AppInfo) {
                    listener?.onAppUninstallClick(appInfo)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onAppUninstallClick(appInfo: AppInfo)
    }

    class ContentViewHolder(
        root: View,
        listener: Listener
    ) : RecyclerView.ViewHolder(root) {

        private var appName: TextView = root.findViewById(R.id.app_name)
        private var appMinSdk: TextView = root.findViewById(R.id.app_min_sdk)
        private var appTargetSdk: TextView = root.findViewById(R.id.app_target_sdk)
        private var packageName: TextView = root.findViewById(R.id.package_name)
        private var appIcon: ImageView = root.findViewById(R.id.app_icon)
        private var uninstall: View = root.findViewById(R.id.uninstall)

        private val listener: AppInfoListContentViewModel.Listener =
            object : AppInfoListContentViewModel.Listener {
                override fun onAppUninstallClick(appInfo: AppInfo) {
                    listener.onAppUninstallClick(appInfo)
                }
            }

        fun bind(appInfo: AppInfo) {
            val viewModel = AppInfoListContentViewModel(appInfo)
            appName.text = viewModel.appName
            appMinSdk.text = viewModel.minSdkText
            appTargetSdk.text = viewModel.targetSdkText
            packageName.text = viewModel.packageName
            appIcon.setImageDrawable(viewModel.appIcon)
            uninstall.setOnClickListener { listener.onAppUninstallClick(appInfo) }
        }

        interface Listener {
            fun onAppUninstallClick(appInfo: AppInfo)
        }
    }
}
