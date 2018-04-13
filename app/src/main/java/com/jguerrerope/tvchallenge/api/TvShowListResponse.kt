package com.jguerrerope.tvchallenge.api

import com.google.gson.annotations.SerializedName

data class TvShowListResponse(
        @SerializedName("page")
        val page: Int,

        @SerializedName("total_results")
        val total_results: Int,

        @SerializedName("total_pages")
        val total_pages: Int,

        @SerializedName("results")
        val results: List<TvShowResponse>
)