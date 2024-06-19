#include <jni.h>
#include <android/log.h>
#include <string>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_modi_dynamic_MainActivity_stringBFromJNI(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_ERROR, "hello", "%s", "lib2");
       std::string hello = "Hello from libB C++";
    return env->NewStringUTF(hello.c_str());
}