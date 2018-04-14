package com.jguerrerope.tvchallenge.db

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.jguerrerope.tvchallenge.Configuration
import com.jguerrerope.tvchallenge.data.TvShow
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TvShowDatabaseTest {
    companion object {
        private val testTvShowList = (1..Configuration.NUMBER_OF_ITEMS_PER_PAGE).map {
            TvShow(
                    id = it,
                    name = "name$it",
                    popularity = it.toFloat(),
                    voteCount = it,
                    voteAverage = it.toFloat(),
                    overview = "The Big Bang Theory ",
                    backdropPath = "backdropPath $it",
                    posterPath = "posterPath $it"
            ).apply {
                indexInResponse = it
            }
        }
    }

    private lateinit var db: TvShowDatabase

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                TvShowDatabase::class.java).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertAndRead() {
        db.tvShowDao().insertList(testTvShowList)

        val index = db.tvShowDao().getNextIndex()
        assertThat(index, CoreMatchers.`is`(Configuration.NUMBER_OF_ITEMS_PER_PAGE + 1))
    }
}