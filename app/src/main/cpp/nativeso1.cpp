#include <jni.h>
#include <android/log.h>
#include <string>


extern "C"
JNIEXPORT void JNICALL
Java_com_modi_dynamic_MainActivity_clickNative1(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_ERROR, "hello", "%s", "native1");
}