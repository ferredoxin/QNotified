//
// Created by cinit on 2020/2/17.
//

#include <errno.h>
#include <dlfcn.h>
#include "Natives.h"
#include "jni.h"
#include "memory.h"
#include "malloc.h"
#include "unistd.h"
#include "sys/mman.h"

typedef unsigned char uchar;

//jint JNI_OnLoad(JavaVM *jvm, void *resv) {
//}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    mwrite
 * Signature: (JI[BI)V
 */
void Java_nil_nadph_qnotified_util_Natives_mwrite
        (JNIEnv *env, jclass clz, jlong ptr, jint len, jbyteArray arr, jint offset) {
    jbyte *bufptr = (jbyte *) ptr;
    int blen = env->GetArrayLength(arr);
    if (offset < 0) {
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "offset < 0");
        return;
    }
    if (len < 0) {
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "len < 0");
        return;
    }
    if (blen - len < 0) {
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "length < offset");
        return;
    }
    env->GetByteArrayRegion(arr, offset, len, bufptr);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    mread
 * Signature: (JI[BI)V
 */
void Java_nil_nadph_qnotified_util_Natives_mread
        (JNIEnv *env, jclass, jlong ptr, jint len, jbyteArray arr, jint offset) {
    jbyte *bufptr = (jbyte *) ptr;
    int blen = env->GetArrayLength(arr);
    if (offset < 0) {
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "offset < 0");
        return;
    }
    if (len < 0) {
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "len < 0");
        return;
    }
    if (blen - len < 0) {
        env->ThrowNew(env->FindClass("java/lang/IndexOutOfBoundsException"), "length < offset");
        return;
    }
    env->SetByteArrayRegion(arr, offset, len, bufptr);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    malloc
 * Signature: (I)J
 */
jlong Java_nil_nadph_qnotified_util_Natives_malloc
        (JNIEnv *env, jclass, jint len) {
    jlong ptr = (jlong) malloc(len);
    return ptr;
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    free
 * Signature: (J)V
 */
void Java_nil_nadph_qnotified_util_Natives_free(JNIEnv *, jclass, jlong ptr) {
    free((void *) ptr);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    memcpy
 * Signature: (JJI)V
 */
void Java_nil_nadph_qnotified_util_Natives_memcpy(JNIEnv *, jclass, jlong dest, jlong src, jint n) {
    memcpy((void *) dest, (void *) src, n);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    memset
 * Signature: (JII)V
 */
void Java_nil_nadph_qnotified_util_Natives_memset
        (JNIEnv *, jclass, jlong addr, jint c, jint num) {
    memset((void *) addr, c, num);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    mprotect
 * Signature: (JII)I
 */
jint Java_nil_nadph_qnotified_util_Natives_mprotect(JNIEnv *, jclass, jlong addr, jint len, jint prot) {
    if (mprotect((void *) addr, len, prot)) {
        return errno;
    } else {
        return 0;
    }
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    dlsym
 * Signature: (JLjava/lang/String;)J
 */
jlong Java_nil_nadph_qnotified_util_Natives_dlsym(JNIEnv *env, jclass, jlong h, jstring name) {
    const char *p;
    jboolean copy;
    p = env->GetStringUTFChars(name, &copy);
    if (!p)return 0;
    void *ret = dlsym((void *) h, p);
    env->ReleaseStringUTFChars(name, p);
    return (jlong) ret;
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    dlopen
 * Signature: (Ljava/lang/String;I)J
 */
jlong Java_nil_nadph_qnotified_util_Natives_dlopen(JNIEnv *env, jclass, jstring name, jint flag) {
    const char *p;
    jboolean copy;
    p = env->GetStringUTFChars(name, &copy);
    if (!p)return 0;
    void *ret = dlopen(p, flag);
    env->ReleaseStringUTFChars(name, p);
    return (jlong) ret;
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    dlclose
 * Signature: (J)I
 */
jint Java_nil_nadph_qnotified_util_Natives_dlclose(JNIEnv *, jclass, jlong h) {
    return (jint) dlclose((void *) h);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    sizeofptr
 * Signature: ()I
 */
jint Java_nil_nadph_qnotified_util_Natives_sizeofptr(JNIEnv *, jclass) {
    return sizeof(void *);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    getpagesize
 * Signature: ()I
 */
JNIEXPORT jint
JNICALL Java_nil_nadph_qnotified_util_Natives_getpagesize(JNIEnv *, jclass) {
    return getpagesize();
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    call
 * Signature: (J)J
 */
jlong Java_nil_nadph_qnotified_util_Natives_call__J(JNIEnv *, jclass, jlong addr) {
    void *(*fun)();
    fun = (void *(*)()) (addr);
    void *ret = fun();
    return (jlong) ret;
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    call
 * Signature: (JJ)J
 */
jlong Java_nil_nadph_qnotified_util_Natives_call__JJ(JNIEnv *, jclass, jlong addr, jlong arg) {
    void *(*fun)(void *);
    fun = (void *(*)(void *)) (addr);
    void *ret = fun((void *) arg);
    return (jlong) ret;
}