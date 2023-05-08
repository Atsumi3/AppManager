package atsumi.android.appmanager.ui.app_info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import atsumi.android.appmanager.databinding.ItemAppInfoListContentBinding
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
            ItemAppInfoListContentBinding.inflate(
                LayoutInflater.from(parent.context),
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
        private val binding: ItemAppInfoListContentBinding,
        listener: Listener
    ) :
        RecyclerView.ViewHolder(binding.root) {

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
            binding.appName.text = viewModel.appName
            binding.appIcon.setImageDrawable(viewModel.appIcon)
            binding.appMinSdk.text = viewModel.minSdkText
            binding.appTargetSdk.text = viewModel.targetSdkText
            binding.packageName.text = viewModel.packageName
            binding.uninstall.setOnClickListener {
                listener.onAppUninstallClick(appInfo)
            }
        }

        interface Listener {
            fun onAppUninstallClick(appInfo: AppInfo)
        }
    }
}
