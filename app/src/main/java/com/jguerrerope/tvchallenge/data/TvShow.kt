package com.jguerrerope.tvchallenge.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Class that represents a Tv Show in the app.
 */
@Entity(tableName = "tv_show")
data class TvShow(
        @PrimaryKey
        val id: Int,
        val name: String,
        val popularity: Float,
        val voteCount: Int,
        val voteAverage: Float,
        val overview: String,
        val backdropPath: String,
        val posterPath: String
) {
    // To be consistent with changing backend order, we need to keep data like this
    var indexInResponse: Int = -1
}