package com.moviles.appexamen.network



import com.moviles.appexamen.App
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.moviles.appexamen.common.Constants.API_BASE_URL
import okhttp3.Cache
import okhttp3.OkHttpClient

object RetrofitInstance {
    private const val CACHE_SIZE = (5 * 1024 * 1024).toLong() // 5 MB

    private val cache by lazy {
        Cache(App.instance.cacheDir, CACHE_SIZE)
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (App.hasInternet()) {
                    request.newBuilder()
                        .header("Cache-Control", "public, max-age=" + 5)
                        .build()
                } else {
                    request.newBuilder()
                        .header(
                            "Cache-Control",
                            "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                        )
                        .build()
                }
                chain.proceed(request)
            }
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}