#include "Karaoke.h"
#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_superpowerednative_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++ Superpowered";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_superpowerednative_KaraokeService_Karaoke(JNIEnv *env, jobject thiz,
                                                           jint samplerate, jint buffersize) {
    karaoke = new Karaoke((unsigned int)samplerate, (unsigned int)buffersize);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_superpowerednative_KaraokeService_StopRecording(JNIEnv *env, jobject thiz) {
    if (karaoke)
    {
        karaoke->stopRecord();
        __android_log_print(ANDROID_LOG_DEBUG, "Recorder", "Finished recording.");
    }
}