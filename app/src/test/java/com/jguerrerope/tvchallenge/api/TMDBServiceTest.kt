package com.jguerrerope.tvchallenge.api

import com.jguerrerope.tvchallenge.Configuration
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

@RunWith(JUnit4::class)
class TMDBServiceTest {
    private lateinit var service: TMDBService
    private lateinit var mockWebServer: MockWebServer

    @Before
    @Throws(IOException::class)
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create<TMDBService>(TMDBService::class.java)
    }

    @After
    @Throws(IOException::class)
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun success() {
        enqueueResponse("tv-show-list.json")
        service.getTvShowPopular(page = 1)
                .test()
                .assertValue { it.body() != null }
                .assertValue { it.body()!!.page == 1 }
                .assertValue { it.body()!!.results.size == Configuration.NUMBER_OF_ITEMS_PER_PAGE }
    }

    @Test
    fun badRequest() {
        mockWebServer.enqueue(MockResponse().setBody("{error:\"bad request\"").setResponseCode(400))
        service.getTvShowPopular(page = -1)
                .test()
                .assertValue { it.body() == null }
                .assertValue { !it.isSuccessful}
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String) {
        val inputStream =
                javaClass.classLoader.getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        mockWebServer.enqueue(MockResponse().setBody(source.readString(StandardCharsets.UTF_8)))
    }
}
