package com.jguerrerope.tvchallenge.api

import com.jguerrerope.tvchallenge.data.TvShow
import javax.inject.Inject

class TvShowResponseMapper @Inject constructor() {

    fun toEntity(value: TvShowResponse): TvShow {
        return TvShow(
                id = value.id,
                name = value.name,
                popularity = value.popularity,
                voteCount = value.voteCount,
                voteAverage = value.voteAverage,
                overview = value.overview,
                backdropPath = value.backdropPath,
                posterPath = value.posterPath
        )
    }

    fun toEntity(values: List<TvShowResponse>) = values.map { toEntity(it) }
}
