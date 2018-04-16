package com.jguerrerope.tvchallenge.ui.adapter

import android.arch.lifecycle.Observer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ProgressBar
import com.jguerrerope.tvchallenge.data.NetworkState
import com.jguerrerope.tvchallenge.data.TvShow
import com.jguerrerope.tvchallenge.ui.item.TvShowItemView

/**
 * PagedListAdapter used to display TvShow
 *
 * @param onItemClick what has to be done when a TvShowItemView gets clicked
 */
class TvShowPagedListAdapter(
        private val onItemClick: (tvShow: TvShow) -> Unit
) : PagedListAdapterBase<TvShow>(diffCallback) {
    private var hasExtraRow = false

    var itemParentWithPercentage = -1f
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.layoutManager.canScrollVertically()
    }

    // We create a regular TvShowItemView or a ProgressBar that indicates that we are loading more repos
    override fun onCreateItemView(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            VIEW_TYPE_PROGRESS -> ProgressBar(parent.context)
            VIEW_TYPE_TV_SHOW -> TvShowItemView(parent.context)
            else -> throw RuntimeException("bad type view")
        }.apply {
            layoutParams = if (itemParentWithPercentage in 0.0..1.0) {
                val newWidth = (parent.measuredWidth * itemParentWithPercentage).toInt()
                LayoutParams(newWidth, WRAP_CONTENT)
            }else  LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
    }

    // We bind data to our view. In case of null items we can set the view as placeholder for the data that will arrive
    // at some point
    override fun onBindViewHolder(holder: ViewWrapper<View>, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_TV_SHOW) {
            (holder.view as TvShowItemView).apply {
                val item = getItem(position)
                if (item == null) {
                    setOnClickListener(null)
                } else {
                    setOnClickListener { onItemClick(item) }
                    bind(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow && position == itemCount - 1) VIEW_TYPE_PROGRESS else VIEW_TYPE_TV_SHOW
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasExtraRow) 1 else 0

    // We observe changes in network state. So if it starts loading for more objects we set the extra row with the
    // progress bar. And when it finish we remove it from the bottom.
    val networkStateObserver = Observer<NetworkState> {
        it?.let { state ->
            hasExtraRow = when (state) {
                NetworkState.NEXT_LOADING -> {
                    notifyItemInserted(super.getItemCount()); true
                }
                else -> {
                    if (hasExtraRow) notifyItemRemoved(itemCount); false
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_PROGRESS = 1
        private const val VIEW_TYPE_TV_SHOW = 2

        private val diffCallback = object : DiffUtil.ItemCallback<TvShow>() {

            // Lets assume that the name is a unique identifier for a repo. Even if
            // item content change at some point, the name will define if it is the
            // same item or it is another one
            override fun areItemsTheSame(oldItem: TvShow, newItem: TvShow): Boolean =
                    oldItem.name == newItem.name

            // Repo is a data class so it has predefined equals method where each
            // field is used to define if two objects are equals
            override fun areContentsTheSame(oldItem: TvShow, newItem: TvShow): Boolean =
                    oldItem == newItem
        }
    }
}