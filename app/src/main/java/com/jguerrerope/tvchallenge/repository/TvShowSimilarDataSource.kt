package com.jguerrerope.tvchallenge.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource
import com.jguerrerope.tvchallenge.api.TMDBService
import com.jguerrerope.tvchallenge.api.TvShowResponseMapper
import com.jguerrerope.tvchallenge.data.NetworkState
import com.jguerrerope.tvchallenge.data.TvShow
import com.jguerrerope.tvchallenge.extension.logd
import com.jguerrerope.tvchallenge.extension.loge
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy

class TvShowSimilarDataSource(
        private val webservice: TMDBService,
        private val tvShowId: Int,
        private val itemsPerPage: Int,
        private val tvShowResponseMapper: TvShowResponseMapper,
        private val backgroundScheduler: Scheduler
) : PositionalDataSource<TvShow>() {

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()
    private var reachEnd: Boolean = false

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TvShow>) {
        networkState.postValue(NetworkState.LOADING)
        webservice.getTvShowSimilar(tvShowId, 1)
                .subscribeOn(backgroundScheduler)
                .subscribeBy(
                        onSuccess = {
                            logd("loadInitial.onSuccess")
                            if (it.totalPages == it.page) reachEnd = true
                            networkState.postValue(NetworkState.LOADED)
                            callback.onResult(tvShowResponseMapper.toEntity(it.results), 0, it.results.size)
                        },
                        onError = {
                            loge("loadInitial.onError", it)
                            networkState.postValue(NetworkState.error(it))
                        }
                )
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TvShow>) {
        val nextPage = ( params.startPosition / itemsPerPage) + 1
        networkState.postValue(NetworkState.LOADING)
        webservice.getTvShowSimilar(tvShowId, nextPage)
                .subscribeOn(backgroundScheduler)
                .subscribeBy(
                        onSuccess = {
                            logd("loadInitial.onSuccess")
                            if (it.totalPages == it.page) reachEnd = true
                            networkState.postValue(NetworkState.LOADED)
                            callback.onResult(tvShowResponseMapper.toEntity(it.results))
                        },
                        onError = {
                            loge("loadInitial.onError", it)
                            networkState.postValue(NetworkState.error(it))
                        }
                )
    }
}