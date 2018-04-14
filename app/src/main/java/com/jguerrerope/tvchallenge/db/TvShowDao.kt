package com.jguerrerope.tvchallenge.db

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.jguerrerope.tvchallenge.data.TvShow

/**
 * Data access object to operate over TvShowEntity
 */
@Dao
interface TvShowDao {

    /**
     * Inserts a list of TvShow in the database. If some of those conflict, we assume that new info has just
     * arrived, so we replace it.
     *
     * @param tvShowList The list of TvShow
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(tvShowList: List<TvShow>)

    /**
     * Gets a [DataSource.Factory] of [TvShow] that it automatically handles how to provide specific items to the UI.
     *
     * @return A reactive [TvShow] data source factory
     */
    @Query("SELECT * FROM tv_show ORDER BY indexInResponse ASC")
    fun tvShowDataFactory(): DataSource.Factory<Int, TvShow>

    /**
     * Gets the next index that can be used to insert in the [TvShow] table in the database
     *
     * @return The next index to use in [TvShow] table
     */
    @Query("SELECT MAX(indexInResponse) + 1 FROM tv_show")
    fun getNextIndex(): Int
}