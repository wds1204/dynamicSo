package com.modi.dysolib.utils

import android.os.Build
import java.util.Locale.Builder

object AbiUtils {

    fun getSupportABIS():Array<String>?{
       return Build.SUPPORTED_ABIS
    }

}