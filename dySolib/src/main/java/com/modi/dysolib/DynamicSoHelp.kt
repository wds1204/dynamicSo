package com.modi.dysolib

import android.content.Context
import java.io.File

object DynamicSoHelp {
    private var path: String? = null

    /**
     * /data/user/0/com.modi.dynamic/files/dynamic_so/
     */
    fun initDynamicSo(
        context: Context,
        path: String = context.filesDir.absolutePath + "/dynamic_so/"
    ) {
        println("path=$path")
        this.path = path
        DynamicSoCore.injectSoPath(context, path)
    }




    fun loadSoLibrary(libName: String) {
        checkNotNull(path)
        val fileName = System.mapLibraryName(libName)
        DynamicSoCore.loadSoLibrary(File(path + fileName), path!!)
    }

    fun loadSoLibrary(file: File) {
        DynamicSoCore.loadSoLibrary(file, path!!)
    }

}