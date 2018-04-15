package com.jguerrerope.tvchallenge.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.jguerrerope.tvchallenge.api.TMDBService
import com.jguerrerope.tvchallenge.api.TvShowListResponse
import com.jguerrerope.tvchallenge.data.NetworkState
import com.jguerrerope.tvchallenge.data.TvShow
import com.jguerrerope.tvchallenge.db.TvShowDao
import com.jguerrerope.tvchallenge.extension.logd
import com.jguerrerope.tvchallenge.extension.loge
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 */
class TvShowPopularBoundaryCallback(
        private val itemsPerPage: Int,
        private val webservice: TMDBService,
        private val dao: TvShowDao,
        private val handleResponse: (TvShowListResponse?) -> Unit,
        private val backgroundScheduler: Scheduler
) : PagedList.BoundaryCallback<TvShow>() {

    private var retryCompletable: Completable? = null
    private var reachEnd: Boolean = false

    val networkState = MutableLiveData<NetworkState>()

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        networkState.postValue(NetworkState.INITIAL_LOADING)
        Single.fromCallable { (dao.getNextIndex() / itemsPerPage) + 1 }
                .subscribeOn(backgroundScheduler)
                .flatMap { webservice.getTvShowPopular(it) }
                .subscribeBy(
                        onSuccess = {
                            logd("onZeroItemsLoaded.onSuccess")
                            /**
                             * every time it gets new items, boundary callback simply inserts them into the database and
                             * paging library takes care of refreshing the list if necessary.
                             */
                            handleResponse.invoke(it)
                            networkState.postValue(NetworkState.LOADED)
                        },
                        onError = {
                            loge("onZeroItemsLoaded.onError", it)
                            retryCompletable = Completable.fromAction { onZeroItemsLoaded() }
                            networkState.postValue(NetworkState.error(it))
                        }
                )
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: TvShow) {
        if (!reachEnd) {
            networkState.postValue(NetworkState.NEXT_LOADING)
            Single.fromCallable { dao.getNextIndex() }
                    .subscribeOn(backgroundScheduler)
                    .map { (dao.getNextIndex() / itemsPerPage) + 1 }
                    .flatMap { webservice.getTvShowPopular(it) }
                    .subscribeBy(
                            onSuccess = {
                                logd("onZeroItemsLoaded.onSuccess")
                                handleResponse.invoke(it)
                                if (it.totalPages == it.page) reachEnd = true
                                networkState.postValue(NetworkState.LOADED)
                            },
                            onError = {
                                loge("onItemAtEndLoaded.onError", it)
                                retryCompletable = Completable.fromAction { onItemAtEndLoaded(itemAtEnd) }
                                networkState.postValue(NetworkState.error(it))
                            }
                    )
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: TvShow) {
        // ignored, since we only ever append to what's in the DB
    }

    fun retry() = retryCompletable?.observeOn(backgroundScheduler)?.subscribe { }
}