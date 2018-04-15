package com.jguerrerope.tvchallenge.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.PositionalDataSource
import com.jguerrerope.tvchallenge.data.TvShow

/**
 * Fake class to simulate Room expected behaviour
 */
class FakeRoomDataSourceFactory(var items: List<TvShow>) : DataSource.Factory<Int, TvShow>() {
    val sourceLiveData = MutableLiveData<FakeDataSource>()

    override fun create(): DataSource<Int, TvShow> {
        val source = FakeDataSource(items)
        sourceLiveData.postValue(source)
        return source
    }

    class FakeDataSource(var items: List<TvShow>) : PositionalDataSource<TvShow>() {
        override fun loadInitial(params: PositionalDataSource.LoadInitialParams,
                                 callback: PositionalDataSource.LoadInitialCallback<TvShow>) {
            val totalCount = items.size

            val position = PositionalDataSource.computeInitialLoadPosition(params, totalCount)
            val loadSize = PositionalDataSource.computeInitialLoadSize(params, position, totalCount)

            // for simplicity, we could return everything immediately,
            // but we tile here since it's expected behavior
            val sublist = items.subList(position, position + loadSize)
            callback.onResult(sublist, position, totalCount)
        }

        override fun loadRange(params: PositionalDataSource.LoadRangeParams,
                               callback: PositionalDataSource.LoadRangeCallback<TvShow>) {
            callback.onResult(items.subList(params.startPosition,
                    params.startPosition + params.loadSize))
        }
    }
}