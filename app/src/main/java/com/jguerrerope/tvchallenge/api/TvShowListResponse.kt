package com.jguerrerope.tvchallenge.api

import com.google.gson.annotations.SerializedName

data class TvShowListResponse(
        @SerializedName("page")
        val page: Int,

        @SerializedName("total_results")
        val totalResults: Int,

        @SerializedName("total_pages")
        val totalPages: Int,

        @SerializedName("results")
        val results: List<TvShowResponse>
)