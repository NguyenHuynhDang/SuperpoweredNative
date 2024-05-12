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
                                                           jint samplerate, jint buffersize, jint destinationfd) {
    karaoke = new Karaoke((unsigned int)samplerate, (unsigned int)buffersize, destinationfd);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_superpowerednative_KaraokeService_OpenFileFromAPK(JNIEnv *env, jobject thiz,
                                                                   jstring path, jint offset,
                                                                   jint length) {
    const char *str = env->GetStringUTFChars(path, 0);
    if (karaoke) karaoke->openFile(str, offset, length);
    env->ReleaseStringUTFChars(path, str);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_superpowerednative_KaraokeService_TogglePlayBack(JNIEnv *env, jobject thiz) {
    if (karaoke) karaoke->playBack();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_superpowerednative_KaraokeService_CleanUp(JNIEnv *env, jobject thiz) {
    if (karaoke) karaoke->clean();
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