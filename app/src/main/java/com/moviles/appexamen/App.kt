package com.moviles.appexamen

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        fun hasInternet(): Boolean {
            val connectivityManager = instance.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}