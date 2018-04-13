package com.jguerrerope.tvchallenge.api

import com.jguerrerope.tvchallenge.BuildConfig
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This interface allows to define the calls that will be used with Retrofit
 */
interface TMDBService {
    @GET("tv/popular?api_key=${BuildConfig.TMDB_ACCESS_TOKEN}")
    fun getTvShowPopular(@Query("page") page: Int): Single<Response<TvShowListResponse>>


    @GET("tv/{tv_id}/similar?api_key=${BuildConfig.TMDB_ACCESS_TOKEN}")
    fun getTvShowSimilar(@Query("tv_id") tvShowId: Int,
                         @Query("page") page: Int): Single<Response<TvShowListResponse>>
}