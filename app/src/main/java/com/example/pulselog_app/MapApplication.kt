package com.example.pulselog_app

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class MapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoMapSdk.init(this,"b9eceb58e6f148906adf85e28c4b578e")
    }
}