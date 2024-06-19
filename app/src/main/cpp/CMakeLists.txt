# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("dynamic")
# 当前工作空间和CMakeList.txt文件位置一致
set(WORKSPACE_PATH &{CMAKE_SOURCE_DIR})

#设置可执行文件的输出为位置
set(EXECUTABLE_OUTPUT_PATH ${WORKSPACE_PATH})
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.


add_library(A SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        native-lib-A.cpp)

add_library(B SHARED
        native-lib-B.cpp)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.


target_link_libraries(B
        # List libraries link to the target library
        android
        log)
# nativeLib 依赖 native1
target_link_libraries(A
        # List libraries link to the target library
        B
        android
        log
        )