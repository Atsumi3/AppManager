package atsumi.android.appmanager.ui.app_info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import atsumi.android.appmanager.R
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
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_app_info_list_content,
                parent,
                false
            ),
            object : ContentViewHolder.Listener {
                override fun onItemClick(appInfo: AppInfo) {
                    listener?.onItemClick(appInfo)
                }

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
        fun onItemClick(appInfo: AppInfo)

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
                override fun onItemClick(appInfo: AppInfo) {
                    listener.onItemClick(appInfo)
                }

                override fun onAppUninstallClick(appInfo: AppInfo) {
                    listener.onAppUninstallClick(appInfo)
                }
            }
        }

        fun bind(appInfo: AppInfo) {
            if (binding.viewModel == null) {
                binding.viewModel = ItemAppInfoListContentViewModel(appInfo).also {
                    it.listener = listener
                }
            } else {
                binding.viewModel!!.data = appInfo
            }
        }

        interface Listener {
            fun onItemClick(appInfo: AppInfo)

            fun onAppUninstallClick(appInfo: AppInfo)
        }
    }
}
