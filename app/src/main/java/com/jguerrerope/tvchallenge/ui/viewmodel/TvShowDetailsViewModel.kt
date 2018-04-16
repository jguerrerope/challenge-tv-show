package com.jguerrerope.tvchallenge.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.jguerrerope.tvchallenge.Configuration
import com.jguerrerope.tvchallenge.data.Listing
import com.jguerrerope.tvchallenge.data.TvShow
import com.jguerrerope.tvchallenge.extension.switchMap
import com.jguerrerope.tvchallenge.repository.TvShowRepositoryImpl
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TvShowDetailsViewModel @Inject constructor(
        private val repository: TvShowRepositoryImpl
) : ViewModel() {
    private var repoResult = MutableLiveData<Listing<TvShow>>()
    val tvShowSimilar = repoResult.switchMap { it.pagedList }

    fun finSimilarTvShow(tvShowId: Int) {
        repoResult.value = repository.getTvShowSimilarListing(tvShowId,
                Configuration.NUMBER_OF_ITEMS_PER_PAGE,
                Configuration.PREFETCH_DISTANCE, Schedulers.io())
    }
}