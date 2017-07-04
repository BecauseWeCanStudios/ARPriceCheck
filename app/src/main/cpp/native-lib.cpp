#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_because_1we_1can_1studios_arpricechecker_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
