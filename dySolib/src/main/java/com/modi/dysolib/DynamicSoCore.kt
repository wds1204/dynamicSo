package com.modi.dysolib

import android.content.Context
import com.modi.dysolib.elf.ElfParser
import java.io.File

object DynamicSoCore {

    private fun createFile(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdir()
        }
    }

    fun injectSoPath(context: Context, path: String) {
        createFile(path)
        LoadLibraryUtils.installNativeLibraryPath(context.classLoader, File(path))
    }


    fun loadSoLibrary(soFile: File, path: String) {
        val parser = ElfParser(soFile)
        val dependencies = parser.parseNeededDependencies()
        println("dependencies:$dependencies")

        dependencies.forEach {
            val file = File(path + it)
            if (file.exists()) {
                loadSoLibrary(file,path)
            }
        }
        System.loadLibrary(soFile.name.substring(3, soFile.name.length - 3))
    }


}