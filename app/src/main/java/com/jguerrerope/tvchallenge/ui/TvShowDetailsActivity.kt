package com.jguerrerope.tvchallenge.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.jguerrerope.tvchallenge.R
import com.jguerrerope.tvchallenge.data.TvShow
import com.jguerrerope.tvchallenge.di.Injectable
import com.jguerrerope.tvchallenge.extension.observe
import com.jguerrerope.tvchallenge.ui.adapter.TvShowPagedListAdapter
import com.jguerrerope.tvchallenge.ui.viewmodel.TvShowDetailsViewModel
import com.jguerrerope.tvchallenge.utils.TMDBImageUtils
import kotlinx.android.synthetic.main.activity_tv_show_details.*
import kotlinx.android.synthetic.main.activity_tv_show_details_content.*
import javax.inject.Inject

class TvShowDetailsActivity : AppCompatActivity(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var tvShow: TvShow
    private lateinit var viewModel: TvShowDetailsViewModel
    private lateinit var tvShowPagedListAdapter: TvShowPagedListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.transition_enter_right, R.anim.transition_no_animation)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_show_details)
        tvShow = intent?.extras?.getSerializable(EXTRA_TV_SHOW) as TvShow?
                ?: throw RuntimeException("bad initialization. not found some extras")
        setUpViews()
        setUpViewModels()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.transition_no_animation, R.anim.transition_exit_right)
    }

    private fun setUpViews() {
        setUpToolbar()
        bindTvShow(tvShow)
        tvShowPagedListAdapter = TvShowPagedListAdapter {}
        tvShowPagedListAdapter.itemParentWithPercentage = 0.70f
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TvShowDetailsActivity,
                    LinearLayoutManager.HORIZONTAL, false)
            adapter = tvShowPagedListAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setUpViewModels() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvShowDetailsViewModel::class.java)
        viewModel.finSimilarTvShow(tvShow.id)
        viewModel.tvShowSimilar.observe(this) {
            it ?: return@observe
            tvShowPagedListAdapter.submitList(it)
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = tvShow.name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        collapsingToolbar.title = tvShow.name
    }

    private fun bindTvShow(tvShow: TvShow) {
        tvShowOverview.text = tvShow.overview ?: ""
        tvShowVoteAverage.text = tvShow.voteAverage.toString()
        tvShowVoteCount.text = tvShow.voteCount.toString()
        tvShowPopularity.text = getString(R.string.popularity_format, tvShow.popularity)

        val options = RequestOptions()
                .placeholder(R.drawable.tv_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)

        val url = TMDBImageUtils.formatUrlImageWithW342(tvShow.backdropPath ?: tvShow.posterPath
        ?: "")
        Glide
                .with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .apply(options)
                .into(tvShowImageView)
    }

    companion object {
        const val EXTRA_TV_SHOW = "EXTRA_TV_SHOW"
    }
}
