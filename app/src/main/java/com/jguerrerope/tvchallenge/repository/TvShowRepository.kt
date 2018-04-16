package com.jguerrerope.tvchallenge.repository

import com.jguerrerope.tvchallenge.data.Listing
import com.jguerrerope.tvchallenge.data.TvShow
import io.reactivex.Scheduler

/**
 * Repository to handle which data source must be used to provide all repo related information
 */
interface TvShowRepository {
    /**
     * Gets a [Listing] of TvShow Popular
     *
     * @param itemsPerPage The number of items that we want to retrieve
     * @param backgroundScheduler The scheduler of background processing
     * @return [Listing]  a Listing for the given TvShow popular.
     */
    fun getTvShowPopularListing(itemsPerPage: Int, backgroundScheduler: Scheduler): Listing<TvShow>


    /**
     * Gets a [Listing] of TvShow Similar
     *
     * @param tvShowId The id of TvShow for search similar
     * @param itemsPerPage The number of items that we want to retrieve
     * @param backgroundScheduler The scheduler of background processing
     * @return [Listing]  a Listing for the given TvShow popular.
     */
    fun getTvShowSimilarListing(
            tvShowId: Int,
            itemsPerPage: Int,
            prefetchDistance: Int,
            backgroundScheduler: Scheduler): Listing<TvShow>

}