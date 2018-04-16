package com.jguerrerope.tvchallenge.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.jguerrerope.tvchallenge.api.TMDBService
import com.jguerrerope.tvchallenge.api.TvShowResponseMapper
import com.jguerrerope.tvchallenge.data.TvShow
import io.reactivex.Scheduler

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class TvShowSimilarDataSourceFactory(
        private val webservice: TMDBService,
        private val tvShowId: Int,
        private val itemsPerPage: Int,
        private val tvShowResponseMapper: TvShowResponseMapper,
        private val backgroundScheduler: Scheduler
) : DataSource.Factory<Int, TvShow>() {
    val sourceLiveData = MutableLiveData<TvShowSimilarDataSource>()
    override fun create(): DataSource<Int, TvShow> {
        val source = TvShowSimilarDataSource(
                webservice, tvShowId, itemsPerPage,tvShowResponseMapper,backgroundScheduler)
        sourceLiveData.postValue(source)
        return source
    }
}
