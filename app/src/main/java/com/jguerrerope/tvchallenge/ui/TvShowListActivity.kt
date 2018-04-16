package com.jguerrerope.tvchallenge.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jguerrerope.tvchallenge.R
import com.jguerrerope.tvchallenge.data.NetworkState
import com.jguerrerope.tvchallenge.data.Status
import com.jguerrerope.tvchallenge.di.Injectable
import com.jguerrerope.tvchallenge.extension.observe
import com.jguerrerope.tvchallenge.extension.visibleOrGone
import com.jguerrerope.tvchallenge.ui.adapter.TvShowPagedListAdapter
import com.jguerrerope.tvchallenge.ui.viewmodel.TvShowPopularViewModel
import kotlinx.android.synthetic.main.activity_tv_show_list.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class TvShowListActivity : AppCompatActivity(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: TvShowPopularViewModel
    private lateinit var tvShowPagedListAdapter: TvShowPagedListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.transition_fade_in, R.anim.transition_no_animation)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_show_list)
        setUpViews()
        setUpViewModels()
    }

    private fun setUpViews() {
        tvShowPagedListAdapter = TvShowPagedListAdapter{
            startActivity<TvShowDetailsActivity>(Pair(TvShowDetailsActivity.EXTRA_TV_SHOW, it))
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TvShowListActivity)
            adapter = tvShowPagedListAdapter
        }
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setUpViewModels() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvShowPopularViewModel::class.java)
        viewModel.tvShowPopular.observe(this) {
            tvShowPagedListAdapter.submitList(it)
        }
        viewModel.refreshState.observe(this) {
            swipeRefreshLayout.isRefreshing = it == NetworkState.LOADING
            if (it?.status == Status.FAILED) {
                Snackbar.make(recyclerView, R.string.network_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry) { viewModel.retry() }
                        .show()
            }
        }
        viewModel.networkState.observe(this, tvShowPagedListAdapter.networkStateObserver)
        viewModel.networkState.observe(this) {
            it ?: return@observe
            progress.visibleOrGone(it == NetworkState.INITIAL_LOADING)

            if (it.status == Status.FAILED) {
                Snackbar.make(recyclerView, R.string.network_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry) { viewModel.retry() }
                        .show()
            }
        }
    }
}
