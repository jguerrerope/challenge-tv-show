package com.jguerrerope.tvchallenge.ui.item

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

import com.bumptech.glide.request.RequestOptions
import com.jguerrerope.tvchallenge.R
import com.jguerrerope.tvchallenge.data.TvShow
import com.jguerrerope.tvchallenge.utils.TMDBImageUtils
import kotlinx.android.synthetic.main.view_item_tv_show.view.*

class TvShowItemView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_item_tv_show, this, true)
        useCompatPadding = true
        radius = 10f
        cardElevation = 4f
    }

    /**
     * Bind a TvShow to our view. So if we were placeholding this items we stop that behaviour and set the view
     * with proper information.
     *
     * @param tvShow The repository with the information to be shown
     */
    fun bind(tvShow: TvShow) {
        tvShowTitle.text = tvShow.name
        tvShowVoteAverage.text = tvShow.voteAverage.toString()

        val options = RequestOptions()
                .placeholder(R.drawable.tv_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)

        val url = TMDBImageUtils.formatUrlImageWithW342(tvShow.backdropPath ?: tvShow.posterPath?: "")
        Glide
                .with(context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .apply(options)
                .into(tvShowImageView)
    }
}