package com.jguerrerope.tvchallenge.ui.adapter

import android.arch.lifecycle.Observer
import android.support.v7.util.DiffUtil
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
 * PagedListAdapter used to display Github repositories
 *
 * @param onRepoLongClick what has to be done when a RepositoryItemView gets clicked
 */
class TvShowPagedListAdapter(private val onRepoLongClick: (tvShow: TvShow) -> Unit) :
        PagedListAdapterBase<TvShow>(diffCallback) {

    private var hasExtraRow = false

    // We create a regular TvShowItemView or a ProgressBar that indicates that we are loading more repos
    override fun onCreateItemView(parent: ViewGroup, viewType: Int): View =
            if (viewType == 1) TvShowItemView(parent.context)
            else ProgressBar(parent.context)
                    .apply { layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT) }

    // We bind data to our view. In case of null items we can set the view as placeholder for the data that will arrive
    // at some point
    override fun onBindViewHolder(holder: ViewWrapper<View>, position: Int) {
        if (getItemViewType(position) == 1) {
            (holder.view as TvShowItemView).apply {
                val item = getItem(position)
                if (item == null) {
                    setOnLongClickListener(null)
                    placeholder()
                } else {
                    setOnLongClickListener { onRepoLongClick(item); true }
                    bind(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow && position == itemCount - 1) 2 else 1
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
        val diffCallback = object : DiffUtil.ItemCallback<TvShow>() {

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