/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.modi.dysolib

import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

object LoadLibraryUtils {
    private const val TAG = "LoadLibrary"

    @Throws(Throwable::class)
    fun installNativeLibraryPath(classLoader: ClassLoader, folder: File?) {
        if (folder == null || !folder.exists()) {
            Log.e(TAG, "installNativeLibraryPath, folder is illegal")
            return
        }
        // android o sdk_int 26
        // for android o preview sdk_int 25
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1 && Build.VERSION.PREVIEW_SDK_INT != 0
            || Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1
        ) {
            try {
                V25.install(classLoader, folder)
            } catch (throwable: Throwable) {
                // install fail, try to treat it as v23
                // some preview N version may go here
                Log.e(
                    TAG, String.format(
                        "installNativeLibraryPath, v25 fail, sdk: %d, error: %s, try to fallback to V23",
                        Build.VERSION.SDK_INT, throwable.message
                    )
                )
                V23.install(classLoader, folder)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                V23.install(classLoader, folder)
            } catch (throwable: Throwable) {
                // install fail, try to treat it as v14
                Log.e(
                    TAG, String.format(
                        "installNativeLibraryPath, v23 fail, sdk: %d, error: %s, try to fallback to V14",
                        Build.VERSION.SDK_INT, throwable.message
                    )
                )
                V14.install(classLoader, folder)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            V14.install(classLoader, folder)
        } else {
            V4.install(classLoader, folder)
        }
    }

    private object V4 {
        @Throws(Throwable::class)
        fun install(classLoader: ClassLoader, folder: File) {
            val addPath = folder.path
            val pathField = ReflectUtil.findField(classLoader, "libPath")
            val origLibPaths = pathField[classLoader] as String
            val origLibPathSplit =
                origLibPaths.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val newLibPaths = StringBuilder(addPath)
            for (origLibPath in origLibPathSplit) {
                if (addPath == origLibPath) {
                    continue
                }
                newLibPaths.append(':').append(origLibPath)
            }
            pathField[classLoader] = newLibPaths.toString()
            val libraryPathElementsFiled =
                ReflectUtil.findField(classLoader, "libraryPathElements")
            val libraryPathElements = libraryPathElementsFiled[classLoader] as MutableList<String>
            val libPathElementIt = libraryPathElements.iterator()
            while (libPathElementIt.hasNext()) {
                val libPath = libPathElementIt.next()
                if (addPath == libPath) {
                    libPathElementIt.remove()
                    break
                }
            }
            libraryPathElements.add(0, addPath)
            libraryPathElementsFiled[classLoader] = libraryPathElements
        }
    }

    private object V14 {
        @Throws(Throwable::class)
        fun install(classLoader: ClassLoader, folder: File) {
            val pathListField = ReflectUtil.findField(classLoader, "pathList")
            val dexPathList = pathListField[classLoader]
            val nativeLibDirField =
                ReflectUtil.findField(dexPathList, "nativeLibraryDirectories")
            val origNativeLibDirs = nativeLibDirField[dexPathList] as Array<File>
            val newNativeLibDirList: MutableList<File> = ArrayList(origNativeLibDirs.size + 1)
            newNativeLibDirList.add(folder)
            for (origNativeLibDir in origNativeLibDirs) {
                if (folder != origNativeLibDir) {
                    newNativeLibDirList.add(origNativeLibDir)
                }
            }
            nativeLibDirField[dexPathList] = newNativeLibDirList.toTypedArray()
        }
    }

    private object V23 {
        @Throws(Throwable::class)
        fun install(classLoader: ClassLoader, folder: File) {
            val pathListField = ReflectUtil.findField(classLoader, "pathList")
            val dexPathList = pathListField[classLoader]
            val nativeLibraryDirectories =
                ReflectUtil.findField(dexPathList, "nativeLibraryDirectories")
            var origLibDirs = nativeLibraryDirectories[dexPathList] as MutableList<File>
            if (origLibDirs == null) {
                origLibDirs = ArrayList(2)
            }
            val libDirIt = origLibDirs.iterator()
            while (libDirIt.hasNext()) {
                val libDir = libDirIt.next()
                if (folder == libDir) {
                    libDirIt.remove()
                    break
                }
            }
            origLibDirs.add(0, folder)
            val systemNativeLibraryDirectories =
                ReflectUtil.findField(dexPathList, "systemNativeLibraryDirectories")
            var origSystemLibDirs = systemNativeLibraryDirectories[dexPathList] as List<File>
            if (origSystemLibDirs == null) {
                origSystemLibDirs = ArrayList(2)
            }
            val newLibDirs: MutableList<File> =
                ArrayList(origLibDirs.size + origSystemLibDirs.size + 1)
            newLibDirs.addAll(origLibDirs)
            newLibDirs.addAll(origSystemLibDirs)
            val makeElements = ReflectUtil.findMethod(
                dexPathList,
                "makePathElements",
                MutableList::class.java,
                File::class.java,
                MutableList::class.java
            )
            val suppressedExceptions = ArrayList<IOException>()
            val elements = makeElements.invoke(
                dexPathList,
                newLibDirs,
                null,
                suppressedExceptions
            ) as Array<Any>
            val nativeLibraryPathElements =
                ReflectUtil.findField(dexPathList, "nativeLibraryPathElements")
            nativeLibraryPathElements[dexPathList] = elements
        }
    }

    private object V25 {
        @Throws(Throwable::class)
        fun install(classLoader: ClassLoader, folder: File) {
            val pathListField = ReflectUtil.findField(classLoader, "pathList")
            val dexPathList = pathListField[classLoader]
            val nativeLibraryDirectories =
                ReflectUtil.findField(dexPathList, "nativeLibraryDirectories")
            var origLibDirs = nativeLibraryDirectories[dexPathList] as MutableList<File>
            if (origLibDirs == null) {
                origLibDirs = ArrayList(2)
            }
            val libDirIt = origLibDirs.iterator()
            while (libDirIt.hasNext()) {
                val libDir = libDirIt.next()
                if (folder == libDir) {
                    libDirIt.remove()
                    break
                }
            }
            origLibDirs.add(0, folder)
            val systemNativeLibraryDirectories =
                ReflectUtil.findField(dexPathList, "systemNativeLibraryDirectories")
            var origSystemLibDirs = systemNativeLibraryDirectories[dexPathList] as List<File>
            if (origSystemLibDirs == null) {
                origSystemLibDirs = ArrayList(2)
            }
            val newLibDirs: MutableList<File> =
                ArrayList(origLibDirs.size + origSystemLibDirs.size + 1)
            newLibDirs.addAll(origLibDirs)
            newLibDirs.addAll(origSystemLibDirs)
            val makeElements = ReflectUtil.findMethod(
                dexPathList,
                "makePathElements",
                MutableList::class.java
            )
            val elements = makeElements.invoke(dexPathList, newLibDirs) as Array<Any>
            val nativeLibraryPathElements =
                ReflectUtil.findField(dexPathList, "nativeLibraryPathElements")
            nativeLibraryPathElements[dexPathList] = elements
        }
    }
}