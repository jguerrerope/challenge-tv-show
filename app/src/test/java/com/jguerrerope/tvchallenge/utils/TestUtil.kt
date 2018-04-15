package com.jguerrerope.tvchallenge.utils

import com.jguerrerope.tvchallenge.api.TvShowListResponse
import com.jguerrerope.tvchallenge.api.TvShowResponse
import com.jguerrerope.tvchallenge.data.TvShow

object TestUtil {

    fun createTvShowList(size: Int, indexInit: Int = 1): List<TvShow> {
        return (0 until size).map {
            val index = indexInit + it
            TvShow(
                    id = index,
                    name = "name $index",
                    popularity = index.toFloat(),
                    voteCount = index,
                    voteAverage = index.toFloat(),
                    overview = "overview $index",
                    backdropPath = "backdropPath $index",
                    posterPath = "posterPath $index"
            )
        }
    }

    fun createTvShowResponseList(size: Int, indexInit: Int = 1): List<TvShowResponse> {
        return (0 until size).map {
            val index = indexInit + it
            TvShowResponse(
                    id = index,
                    name = "name $index",
                    originalName = "originalName $index",
                    genreIds = arrayListOf(),
                    originCountry = arrayListOf(),
                    originalLanguage = "originalLanguage $index",
                    popularity = index.toFloat(),
                    voteCount = index,
                    voteAverage = index.toFloat(),
                    overview = "overview $it",
                    firstAirDate = "firstAirDate $index",
                    backdropPath = "backdropPath $index",
                    posterPath = "posterPath $index"
            )
        }
    }


    fun createTvShowListResponse(page: Int, size: Int, totalPage: Int): TvShowListResponse {
        return TvShowListResponse(
                page = page,
                totalResults = totalPage * size,
                totalPages = totalPage,
                results = createTvShowResponseList(size)
        )
    }
}