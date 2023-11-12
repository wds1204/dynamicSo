#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_modi_dynamic_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {

    __android_log_print(ANDROID_LOG_ERROR, "hello", "%s", "native");

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}