package com.jguerrerope.tvchallenge.ui.item

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jguerrerope.tvchallenge.R
import com.jguerrerope.tvchallenge.data.TvShow
import com.jguerrerope.tvchallenge.ui.GlideApp
import com.jguerrerope.tvchallenge.ui.animator.BackgroundColorAnimator
import com.jguerrerope.tvchallenge.utils.TMDBImageUtils
import kotlinx.android.synthetic.main.view_item_tv_show.view.*

class TvShowItemView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_item_tv_show, this, true)
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
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
        GlideApp
                .with(context)
                .load(url)
                .apply(options)
                .into(tvShowImageView)
    }

    // We don't have any kind of data but we want to show to the user that we are fetching it so we animate the view.
    fun placeholder() {
        tvShowTitle.text = ""
        tvShowImageView.setImageResource(0)
        tvShowVoteAverage.text = ""
        getLoadingAnimator(context).addViews(tvShowTitle, tvShowImageView, tvShowVoteAverage)
        setCardBackgroundColor(Color.TRANSPARENT)
    }

    companion object {

        private var loadingAnimator: BackgroundColorAnimator? = null

        // We use this method to have a singleton animator where we add the views that we want to has a placeholder
        // background. And also to remove those views that now has data and do not need to be animated anymore.
        fun getLoadingAnimator(context: Context): BackgroundColorAnimator {
            if (loadingAnimator == null) {
                loadingAnimator = BackgroundColorAnimator(context,
                        android.R.color.darker_gray,
                        android.R.color.primary_text_dark)
            }

            return loadingAnimator!!
        }
    }
}