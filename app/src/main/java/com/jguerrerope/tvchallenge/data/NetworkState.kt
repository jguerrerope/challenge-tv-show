package com.jguerrerope.tvchallenge.data

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
        val status: Status, val msg: String? = null
) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)

        // to use on PagedList.BoundaryCallback
        val INITIAL_LOADING = NetworkState(Status.RUNNING)
        val NEXT_LOADING = NetworkState(Status.RUNNING)

        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
        fun error(throwable: Throwable) = NetworkState(Status.FAILED, throwable.message)
    }
}