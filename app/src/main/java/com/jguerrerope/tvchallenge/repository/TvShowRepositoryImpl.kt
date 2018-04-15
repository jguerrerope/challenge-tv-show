package com.jguerrerope.tvchallenge.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import com.jguerrerope.tvchallenge.api.TMDBService
import com.jguerrerope.tvchallenge.api.TvShowListResponse
import com.jguerrerope.tvchallenge.api.TvShowResponseMapper
import com.jguerrerope.tvchallenge.data.Listing
import com.jguerrerope.tvchallenge.data.NetworkState
import com.jguerrerope.tvchallenge.data.TvShow
import com.jguerrerope.tvchallenge.db.TvShowDatabase
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvShowRepositoryImpl @Inject constructor(
        private val api: TMDBService,
        private val database: TvShowDatabase,
        private val tvShowResponseMapper: TvShowResponseMapper
) : TvShowRepository {

    override fun getTvShowPopularListing(
            itemsPerPage: Int,
            backgroundScheduler: Scheduler): Listing<TvShow> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = TvShowPopularBoundaryCallback(
                webservice = api,
                dao = database.tvShowDao(),
                itemsPerPage = itemsPerPage,
                handleResponse = this::insertTvShowListIntoDb,
                backgroundScheduler = backgroundScheduler
        )
        // create a data source factory from Room
        val builder =
                LivePagedListBuilder(database.tvShowDao().tvShowDataFactory(), itemsPerPage)
                        .setBoundaryCallback(boundaryCallback)

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger, {
            refreshTvShowPopular(backgroundScheduler)
        })

        return Listing(
                pagedList = builder.build(),
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.retry()
                },
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState
        )
    }


    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private fun insertTvShowListIntoDb(response: TvShowListResponse?) {
        response?.let {
            val nextIndex = database.tvShowDao().getNextIndex()
            val items = tvShowResponseMapper.toEntity(it.results)
            items.forEachIndexed { index, item -> item.indexInResponse = nextIndex + index }
            database.tvShowDao().insertList(items)
        }
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives,
     * clear the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    private fun refreshTvShowPopular(backgroundScheduler: Scheduler): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        api.getTvShowPopular(1)
                .subscribeOn(backgroundScheduler)
                .subscribeBy(
                        onSuccess = {
                            database.runInTransaction {
                                database.tvShowDao().deleteAll()
                                insertTvShowListIntoDb(it)
                            }

                            // since we are in bg thread now, post the result.
                            networkState.postValue(NetworkState.LOADED)
                        },
                        onError = {
                            // retrofit calls this on main thread so safe to call set value
                            networkState.postValue(NetworkState.error(it))
                        }
                )
        return networkState
    }
}