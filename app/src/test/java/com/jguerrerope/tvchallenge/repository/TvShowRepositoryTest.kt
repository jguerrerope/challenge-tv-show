package com.jguerrerope.tvchallenge.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import com.jguerrerope.tvchallenge.api.TMDBService
import com.jguerrerope.tvchallenge.api.TvShowResponseMapper
import com.jguerrerope.tvchallenge.data.Listing
import com.jguerrerope.tvchallenge.data.NetworkState
import com.jguerrerope.tvchallenge.data.TvShow
import com.jguerrerope.tvchallenge.db.TvShowDao
import com.jguerrerope.tvchallenge.utils.TestUtil
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.stub
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class TvShowRepositoryTest {
    @Suppress("unused")
    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()
    private lateinit var repository: TvShowRepository
    private val tvShowResponseMapper = TvShowResponseMapper()

    /**
     * asserts that empty list works fine
     */
    @Test
    fun emptyList() {
        val mockTvShowDao = mock<TvShowDao> {
            on { tvShowDataFactory() } doReturn FakeRoomDataSourceFactory(arrayListOf())
            on { getNextIndex() } doReturn 1
        }
        repository = TvShowRepositoryImpl(mock {}, mockTvShowDao, tvShowResponseMapper)

        val listing = repository.getTvShowPopularListing(
                5, Schedulers.trampoline())
        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
    }

    /**
     * asserts loading a full list in multiple pages
     */
    @Test
    fun verifyCompleteList() {
        val itemsOnePage = TestUtil.createTvShowList(5)
        val mockTvShowDao = mock<TvShowDao> {
            on { tvShowDataFactory() } doReturn  FakeRoomDataSourceFactory(itemsOnePage)
            on { getNextIndex() } doReturn 1
        }
        repository = TvShowRepositoryImpl(mock {}, mockTvShowDao, tvShowResponseMapper)

        val listing = repository.getTvShowPopularListing(5, Schedulers.trampoline())
        val pagedList = getPagedList(listing)

        // trigger loading of the whole list
        pagedList.loadAround(itemsOnePage.size - 1)
        assertThat(pagedList, CoreMatchers.`is`(itemsOnePage))

        val itemsTwoPage = TestUtil.createTvShowList(10)
        mockTvShowDao.stub {
            on { tvShowDataFactory() } doReturn FakeRoomDataSourceFactory(itemsTwoPage)
        }

        // trigger loading of the whole list
        pagedList.loadAround(itemsTwoPage.size - 1)
        assertThat(pagedList, CoreMatchers.`is`(itemsOnePage))

    }

    /**
     * asserts the retry logic when initial load request fails
     */
    @Test
    fun retryInInitialLoad() {
        val fakeRoomDataSource = FakeRoomDataSourceFactory(arrayListOf())
        val mockApi = mock<TMDBService> {
            on { getTvShowPopular(1) } doReturn Single.error(RuntimeException("error"))
        }
        val mockTvShowDao = mock<TvShowDao> {
            on { tvShowDataFactory() } doReturn fakeRoomDataSource
            on { getNextIndex() } doReturn 1
        }

        repository = TvShowRepositoryImpl(mockApi, mockTvShowDao, tvShowResponseMapper)

        val listing = repository.getTvShowPopularListing(5, Schedulers.trampoline())

        assertThat( getPagedList(listing).size, CoreMatchers.`is`(0))

        @Suppress("UNCHECKED_CAST")
        val networkObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.networkState.observeForever(networkObserver)

        mockApi.stub {
            on { getTvShowPopular(1) } doReturn
                    Single.just(TestUtil.createTvShowListResponse(page = 1, size = 5, totalPage = 3))
        }

        listing.retry()
        fakeRoomDataSource.items = TestUtil.createTvShowList(5)
        fakeRoomDataSource.sourceLiveData.value?.invalidate()

        assertThat(getPagedList(listing).size, CoreMatchers.`is`(5))
        assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))

        val inOrder = Mockito.inOrder(networkObserver)
        inOrder.verify(networkObserver).onChanged(NetworkState.error("error"))
        inOrder.verify(networkObserver).onChanged(NetworkState.INITIAL_LOADING)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
        inOrder.verifyNoMoreInteractions()
    }

    /**
     * extract the latest paged list from the listing
     */
    private fun getPagedList(listing: Listing<TvShow>): PagedList<TvShow> {
        val observer = LoggingObserver<PagedList<TvShow>>()
        listing.pagedList.observeForever(observer)
        MatcherAssert.assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        return observer.value!!
    }

    /**
     * extract the latest network state from the listing
     */
    private fun getNetworkState(listing: Listing<TvShow>): NetworkState? {
        val networkObserver = LoggingObserver<NetworkState>()
        listing.networkState.observeForever(networkObserver)
        return networkObserver.value
    }

    /**
     * simple observer that logs the latest value it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        var value: T? = null
        override fun onChanged(t: T?) {
            this.value = t
        }
    }
}