package atsumi.android.appmanager.ui.app_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import atsumi.android.appmanager.R
import atsumi.android.appmanager.entity.AppInfo
import atsumi.android.appmanager.util.DisplayCondition

class AppInfoListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

    private var displayData: List<AppInfo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private fun updateItems() {
        displayData = if (displayCondition != null) {
            data.filter { displayCondition!!.isDisplayable(it) }
        } else {
            data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ContentViewHolder) {
            holder.bind(displayData[position])
        }
    }

    override fun getItemCount(): Int = displayData.size

    interface Listener {
        fun onAppUninstallClick(appInfo: AppInfo)
    }

    class ContentViewHolder(
        root: View,
        listener: Listener
    ) : RecyclerView.ViewHolder(root) {

        private val listener: ItemAppInfoListContentViewModel.Listener

        init {
            this.listener = object : ItemAppInfoListContentViewModel.Listener {
                override fun onAppUninstallClick(appInfo: AppInfo) {
                    listener.onAppUninstallClick(appInfo)
                }
            }
        }

        fun bind(appInfo: AppInfo) {
            val viewModel = ItemAppInfoListContentViewModel(appInfo)
            itemView.findViewById<TextView>(R.id.app_name).text = viewModel.appName
            itemView.findViewById<TextView>(R.id.app_min_sdk).text = viewModel.minSdkText
            itemView.findViewById<TextView>(R.id.app_target_sdk).text = viewModel.targetSdkText
            itemView.findViewById<TextView>(R.id.package_name).text = viewModel.packageName
            itemView.findViewById<ImageView>(R.id.app_icon).setImageDrawable(viewModel.appIcon)
            itemView.findViewById<View>(R.id.uninstall)
                .setOnClickListener { listener.onAppUninstallClick(appInfo) }
        }

        interface Listener {
            fun onAppUninstallClick(appInfo: AppInfo)
        }
    }
}
