package com.jguerrerope.tvchallenge.ui.viewmodel

import android.arch.lifecycle.ViewModel
import com.jguerrerope.tvchallenge.Configuration
import com.jguerrerope.tvchallenge.extension.switchMap
import com.jguerrerope.tvchallenge.repository.TvShowRepositoryImpl
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TvShowPopularViewModel @Inject constructor(
        repository: TvShowRepositoryImpl
) : ViewModel() {

    private val repoResult = AbsentLiveData.create(
            repository.getTvShowPopularListing(
                    Configuration.NUMBER_OF_ITEMS_PER_PAGE, Schedulers.io())
    )

    val tvShowPopular = repoResult.switchMap { it.pagedList }
    val networkState = repoResult.switchMap { it.networkState }
    val refreshState = repoResult.switchMap { it.refreshState }

    fun retry() = repoResult.value?.retry?.invoke()

    fun refresh() = repoResult.value?.refresh?.invoke()
}