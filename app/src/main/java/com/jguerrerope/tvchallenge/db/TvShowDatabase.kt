package com.jguerrerope.tvchallenge.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.jguerrerope.tvchallenge.data.TvShow

/**
 * Main database description.
 */
@Database(entities = [(TvShow::class)], version = 1)
abstract class TvShowDatabase : RoomDatabase() {
    abstract fun tvShowDao(): TvShowDao
}
