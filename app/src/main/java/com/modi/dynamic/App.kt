package com.modi.dynamic

import android.app.Application
import com.modi.dysolib.DynamicSoHelp

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicSoHelp.initDynamicSo(this)

    }
}