//
// Created by cinit on 2020/2/17.
//

#include <errno.h>
#include <dlfcn.h>
#include "jni.h"
#include "memory.h"
#include "malloc.h"
#include "unistd.h"
#include "sys/mman.h"
#include "natives_utils.h"
#include <android/log.h>

#include "Natives.h"

//jint JNI_OnLoad(JavaVM *jvm, void *resv) {
//}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    mwrite
 * Signature: (JI[BI)V
 */
EXPORT void Java_nil_nadph_qnotified_util_Natives_mwrite
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
EXPORT void Java_nil_nadph_qnotified_util_Natives_mread
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
EXPORT jlong Java_nil_nadph_qnotified_util_Natives_malloc
        (JNIEnv *env, jclass, jint len) {
    jlong ptr = (jlong) malloc(len);
    return ptr;
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    free
 * Signature: (J)V
 */
EXPORT void Java_nil_nadph_qnotified_util_Natives_free(JNIEnv *, jclass, jlong ptr) {
    free((void *) ptr);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    memcpy
 * Signature: (JJI)V
 */
EXPORT void
Java_nil_nadph_qnotified_util_Natives_memcpy(JNIEnv *, jclass, jlong dest, jlong src, jint n) {
    memcpy((void *) dest, (void *) src, n);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    memset
 * Signature: (JII)V
 */
EXPORT void Java_nil_nadph_qnotified_util_Natives_memset
        (JNIEnv *, jclass, jlong addr, jint c, jint num) {
    memset((void *) addr, c, num);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    mprotect
 * Signature: (JII)I
 */
EXPORT jint
Java_nil_nadph_qnotified_util_Natives_mprotect(JNIEnv *, jclass, jlong addr, jint len, jint prot) {
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
EXPORT jlong
Java_nil_nadph_qnotified_util_Natives_dlsym(JNIEnv *env, jclass, jlong h, jstring name) {
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
EXPORT jlong
Java_nil_nadph_qnotified_util_Natives_dlopen(JNIEnv *env, jclass, jstring name, jint flag) {
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
EXPORT jint Java_nil_nadph_qnotified_util_Natives_dlclose(JNIEnv *, jclass, jlong h) {
    return (jint) dlclose((void *) h);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    dlerror
 * Signature: ()Ljava/lang/String;
 */
EXPORT jstring Java_nil_nadph_qnotified_util_Natives_dlerror
        (JNIEnv *env, jclass) {
    const char *str = dlerror();
    if (str == nullptr) {
        return nullptr;
    }
    return env->NewStringUTF(str);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    sizeofptr
 * Signature: ()I
 */
EXPORT jint Java_nil_nadph_qnotified_util_Natives_sizeofptr(JNIEnv *, jclass) {
    return sizeof(void *);
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    getpagesize
 * Signature: ()I
 */
EXPORT jint Java_nil_nadph_qnotified_util_Natives_getpagesize(JNIEnv *, jclass) {
    return getpagesize();
}

/*
 * Class:     nil_nadph_qnotified_util_Natives
 * Method:    call
 * Signature: (J)J
 */
EXPORT jlong Java_nil_nadph_qnotified_util_Natives_call__J(JNIEnv *, jclass, jlong addr) {
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
EXPORT jlong Java_nil_nadph_qnotified_util_Natives_call__JJ
        (JNIEnv *, jclass, jlong addr, jlong arg) {
    void *(*fun)(void *);
    fun = (void *(*)(void *)) (addr);
    void *ret = fun((void *) arg);
    return (jlong) ret;
}

uint32_t update_adler32(unsigned adler, const uint8_t *data, uint32_t len) {
    unsigned s1 = adler & 0xffffu;
    unsigned s2 = (adler >> 16u) & 0xffffu;

    while (len > 0) {
        /*at least 5550 sums can be done before the sums overflow, saving a lot of module divisions*/
        unsigned amount = len > 5550 ? 5550 : len;
        len -= amount;
        while (amount > 0) {
            s1 += (*data++);
            s2 += s1;
            --amount;
        }
        s1 %= 65521;
        s2 %= 65521;
    }
    return (s2 << 16u) | s1;
}

uint8_t *extractPayload(uint8_t *dex, int dexLength, int *outLength) {
    int chunkROff = readLe32(dex, dexLength - 4);
    if (chunkROff > dexLength) {
        *outLength = 0;
        return nullptr;
    }
    int base = dexLength - chunkROff;
    int size = readLe32(dex, base);
    if (size > dexLength) {
        *outLength = 0;
        return nullptr;
    }
    uint32_t flags = readLe32(dex, base + 4);
    uint32_t a32_got = readLe32(dex, base + 8);
    uint32_t extra = readLe32(dex, base + 12);
    if (flags != 0) {
        *outLength = 0;
        return nullptr;
    }
    uint32_t key = extra & 0xFFu;
    uint8_t *dat = (uint8_t *) malloc(size);
    if (key == 0) {
        memcpy(dat, dex + base + 16, size);
    } else {
        for (int i = 0; i < size; i++) {
            dat[i] = (uint8_t) (key ^ dex[base + 16 + i]);
        }
    }
    uint32_t a32 = update_adler32(1, dat, size);
    if (a32 != a32_got) {
        free(dat);
        *outLength = 0;
        return nullptr;
    }
    return dat;
}

static int64_t sBuildTimestamp = -2;

static const int DEX_MAX_SIZE = 12 * 1024 * 1024;

jlong doGetBuildTimestamp(JNIEnv *env, jclass clazz) {
    if (sBuildTimestamp != -2)return sBuildTimestamp;
    __android_log_print(ANDROID_LOG_DEBUG, "QNdump", "ntGetBuildTimestamp invoked\n");
    jclass cl_Class = env->FindClass("java/lang/Class");
    jobject loader = env->CallObjectMethod(clazz,
                                           env->GetMethodID(cl_Class, "getClassLoader",
                                                            "()Ljava/lang/ClassLoader;"));
    jobject eu = env->CallObjectMethod(loader,
                                       env->GetMethodID(env->FindClass("java/lang/ClassLoader"),
                                                        "findResources",
                                                        "(Ljava/lang/String;)Ljava/util/Enumeration;"),
                                       env->NewStringUTF("classes.dex"));
    if (eu == nullptr) {
        return -2;
    }
    jclass cl_Enum = env->FindClass("java/util/Enumeration");
    jclass cl_Url = env->FindClass("java/net/URL");
    jmethodID hasMoreElements = env->GetMethodID(cl_Enum, "hasMoreElements", "()Z");
    jmethodID nextElement = env->GetMethodID(cl_Enum, "nextElement", "()Ljava/lang/Object;");
    jbyteArray buf = env->NewByteArray(2048);
    jmethodID openStream = env->GetMethodID(cl_Url, "openStream", "()Ljava/io/InputStream;");
    jclass cIs = env->FindClass("java/io/InputStream");
    jmethodID is_read = env->GetMethodID(cIs, "read", "([B)I");
    jmethodID is_close = env->GetMethodID(cIs, "close", "()V");
    jmethodID toString = env->GetMethodID(env->FindClass("java/lang/Object"), "toString",
                                          "()Ljava/lang/String;");
    if (env->ExceptionCheck()) {
        return -2;
    }
    uint8_t *dex = (uint8_t *) (malloc(DEX_MAX_SIZE));
    if (dex == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, "QNdump", "unable to allocate dex buffer\n");
        return -2;
    }
    int count = 0;
    while (env->CallBooleanMethod(eu, hasMoreElements)) {
        jobject url = env->CallObjectMethod(eu, nextElement);
        if (url == nullptr) {
            continue;
        }
        count++;
        jobject is = env->CallObjectMethod(url, openStream);
        if (is == nullptr) {
            jthrowable ex = env->ExceptionOccurred();
            if (ex != nullptr) {
                jstring jst = (jstring) env->CallObjectMethod(ex, toString);
                const char *errstr = env->GetStringUTFChars(jst, nullptr);
                __android_log_print(ANDROID_LOG_ERROR, "QNdump", "dex openStream error: %s\n",
                                    errstr);
                env->ReleaseStringUTFChars(jst, errstr);
            }
            env->ExceptionClear();
            continue;
        }
        int length = 0;
        int ri = 0;
        while (!env->ExceptionCheck() && (ri = env->CallIntMethod(is, is_read, buf)) > 0) {
            if (length + ri < DEX_MAX_SIZE) {
                env->GetByteArrayRegion(buf, 0, ri, (jbyte *) (dex + length));
            }
            length += ri;
        }
        if (env->ExceptionCheck()) {
            jthrowable ex = env->ExceptionOccurred();
            if (ex != nullptr) {
                jstring jst = (jstring) env->CallObjectMethod(ex, toString);
                const char *errstr = env->GetStringUTFChars(jst, nullptr);
                __android_log_print(ANDROID_LOG_ERROR, "QNdump", "dex read error: %s\n",
                                    errstr);
                env->ReleaseStringUTFChars(jst, errstr);
            }
            env->ExceptionClear();
            env->CallVoidMethod(is, is_close);
            env->ExceptionClear();
            continue;
        }
        {
            //parse [dex, dex+length]
            if (length < 128 * 1024) {
                continue;
            }
            int tailLength = 0;
            uint8_t *tail = extractPayload(dex, length, &tailLength);
            if (tail != nullptr) {
                uint64_t time = 0;
                for (int i = 0; i < 8; i++) {
                    time |= ((uint64_t) ((((uint64_t) tail[i]) & ((uint64_t) 0xFFLLu)))
                            << (8u * i));
                }
                sBuildTimestamp = time;
                free(tail);
                free(dex);
                return time;
            }
        }
    }
    free(dex);
    dex = nullptr;
    if (count == 0) {
        __android_log_print(ANDROID_LOG_ERROR, "QNdump", "getBuildTimestamp/E urls.size == 0\n");
        return -2;
    }
    sBuildTimestamp = -1;
    return sBuildTimestamp;
}

jboolean handleSendBatchMessages(JNIEnv *env, jclass clazz, jobject rt,
                                 jobject ctx, jstring msg, jintArray _type, jlongArray _uin) {
    if (rt == nullptr || ctx == nullptr) {
        env->ThrowNew(env->FindClass("java/lang/NullPointerException"), "appInterface/ctx == null");
        return false;
    }
    if (msg == nullptr || _type == nullptr || _uin == nullptr) {
        env->ThrowNew(env->FindClass("java/lang/NullPointerException"), "msg/uin == null");
        return false;
    }
    if (doGetBuildTimestamp(env, clazz) < 0)_exit(0);
    bool success = true;
    int len = min(env->GetArrayLength(_type), env->GetArrayLength(_uin));
    if (len == 0)return true;
    int *types = static_cast<int *>(malloc(4 * len));
    int64_t *uins = static_cast<int64_t *>(malloc(8 * len));
    env->GetIntArrayRegion(_type, 0, len, types);
    env->GetLongArrayRegion(_uin, 0, len, uins);
    jclass cl_SessionInfoImpl = env->FindClass("nil/nadph/qnotified/bridge/SessionInfoImpl");
    jmethodID createSessionInfo = env->GetStaticMethodID(cl_SessionInfoImpl, "createSessionInfo",
                                                         "(Ljava/lang/String;I)Landroid/os/Parcelable;");
    jclass cl_Str = env->FindClass("java/lang/String");
    jmethodID strValOf = env->GetStaticMethodID(cl_Str, "valueOf", "(J)Ljava/lang/String;");
    jclass cl_Facade = env->FindClass("nil/nadph/qnotified/bridge/ChatActivityFacade");
    jmethodID send = env->GetStaticMethodID(cl_Facade, "sendMessage",
                                            "(Lcom/tencent/mobileqq/app/QQAppInterface;Landroid/content/Context;Landroid/os/Parcelable;Ljava/lang/String;)[J");
    for (int i = 0; i < len; i++) {
        jstring struin = (jstring) (env->CallStaticObjectMethod(cl_Str, strValOf, uins[i]));
        jobject session = env->CallStaticObjectMethod(cl_SessionInfoImpl, createSessionInfo, struin,
                                                      types[i]);
        if (session == nullptr) {
            __android_log_print(ANDROID_LOG_ERROR, "QNdump",
                                "SessionInfoImpl/E createSessionInfo failed");
            success = false;
            break;
        }
        jlongArray msgUid = (jlongArray) env->CallStaticObjectMethod(cl_Facade, send, rt, ctx,
                                                                     session, msg);
        if (msgUid == nullptr) {
            __android_log_print(ANDROID_LOG_ERROR, "QNdump",
                                "handleSendBatchMessages/E sendMsg failed");
            success = false;
            break;
        }
    }
    free(types);
    free(uins);
    return success;
}

jboolean handleSendCardMsg(JNIEnv *env, jclass clazz, jobject rt, jobject session, jstring msg) {
    if (rt == nullptr) {
        env->ThrowNew(env->FindClass("java/lang/NullPointerException"), "appInterface== null");
        return false;
    }
    if (session == nullptr) {
        env->ThrowNew(env->FindClass("java/lang/NullPointerException"), "session == null");
        return false;
    }
    if (msg == nullptr) {
        env->ThrowNew(env->FindClass("java/lang/NullPointerException"), "msg == null");
        return false;
    }
    if (doGetBuildTimestamp(env, clazz) < 0)_exit(0);
    if (env->GetStringLength(msg) < 3)return false;

    jclass utilsClass = env->FindClass("nil/nadph/qnotified/util/Utils");
    jclass cardMsgListClass = env->FindClass("me/singleneuron/util/KotlinUtilsKt");
    jmethodID getInstance = env->GetStaticMethodID(cardMsgListClass,"checkCardMsg", "(Ljava/lang/String;)Lme/singleneuron/data/CardMsgCheckResult;");
    jobject result = env->CallStaticObjectMethod(cardMsgListClass,getInstance,msg);
    jclass cardMsgCheckResultClass = env->FindClass("me/singleneuron/data/CardMsgCheckResult");
    jmethodID toString = env->GetMethodID(cardMsgCheckResultClass,"toString", "()Ljava/lang/String;");
    jmethodID getAccepted = env->GetMethodID(cardMsgCheckResultClass,"getAccept", "()Z");
    auto resultString = (jstring) env->CallObjectMethod(result,toString);
    jmethodID logd = env->GetStaticMethodID(utilsClass,"logd", "(Ljava/lang/String;)V");
    env->CallStaticVoidMethod(utilsClass,logd,resultString);
    bool boolean = env->CallBooleanMethod(result,getAccepted);
    if (!boolean) {
        jmethodID getReason = env->GetMethodID(cardMsgCheckResultClass,"getReason", "()Ljava/lang/String;");
        auto reason = (jstring) env->CallObjectMethod(result,getReason);
        jmethodID showErrorToastAnywhere = env->GetStaticMethodID(utilsClass,"showErrorToastAnywhere","(Ljava/lang/String;)V");
        env->CallStaticVoidMethod(utilsClass,showErrorToastAnywhere,reason);
        return true;
    }

    jchar format;
    env->GetStringRegion(msg, 0, 1, &format);
    if (format == '<') {
        jclass AbsStructMsg = env->FindClass("com/tencent/mobileqq/structmsg/AbsStructMsg");
        if (!AbsStructMsg)return false;
        jclass DexKit = env->FindClass("nil/nadph/qnotified/util/DexKit");
        jmethodID cid = env->GetStaticMethodID(DexKit, "doFindClass", "(I)Ljava/lang/Class;");
        jclass TestStructMsg = (jclass) env->CallStaticObjectMethod(DexKit, cid, 18);
        if (TestStructMsg == nullptr) {
            env->ThrowNew(env->FindClass("java/lang/RuntimeException"), "404: TestStructMsg");
            return false;
        }
        jclass cl_Utils = env->FindClass("nil/nadph/qnotified/util/Utils");
        cid = env->GetStaticMethodID(cl_Utils, "invoke_static_any",
                                     "(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;");
        jobjectArray va = env->NewObjectArray(3, env->FindClass("java/lang/Object"), nullptr);
        env->SetObjectArrayElement(va, 0, msg);
        env->SetObjectArrayElement(va, 1, env->FindClass("java/lang/String"));
        env->SetObjectArrayElement(va, 2, AbsStructMsg);
        jobject structMsg = env->CallStaticObjectMethod(cl_Utils, cid, TestStructMsg, va);
        if (env->ExceptionCheck())return false;
        if (structMsg == nullptr)return false;
        jclass ChatActivityFacade = env->FindClass("nil/nadph/qnotified/bridge/ChatActivityFacade");
        jmethodID sendAbsStructMsg = env->GetStaticMethodID(ChatActivityFacade, "sendAbsStructMsg",
                                                            "(Lcom/tencent/mobileqq/app/QQAppInterface;Landroid/os/Parcelable;Ljava/io/Externalizable;)V");
        env->CallStaticVoidMethod(ChatActivityFacade, sendAbsStructMsg, rt, session, structMsg);
        return !env->ExceptionCheck();
    } else if (format == '{') {
        jclass c_ArkAppMessage = env->FindClass("com/tencent/mobileqq/data/ArkAppMessage");
        if (c_ArkAppMessage == nullptr)return false;
        jmethodID cid = env->GetMethodID(c_ArkAppMessage, "<init>", "()V");
        if (env->ExceptionCheck())return false;
        jobject arkMsg = env->NewObject(c_ArkAppMessage, cid);
        if (arkMsg == nullptr)return false;
        jmethodID fromAppXml = env->GetMethodID(c_ArkAppMessage, "fromAppXml",
                                                "(Ljava/lang/String;)Z");
        if (env->ExceptionCheck())return false;
        if (!env->CallBooleanMethod(arkMsg, fromAppXml, msg)) {
            return false;
        }
        jclass ChatActivityFacade = env->FindClass("nil/nadph/qnotified/bridge/ChatActivityFacade");
        jmethodID sendArkAppMessage = env->GetStaticMethodID(ChatActivityFacade,
                                                             "sendArkAppMessage",
                                                             "(Lcom/tencent/mobileqq/app/QQAppInterface;Landroid/os/Parcelable;Ljava/lang/Object;)Z");
        return env->CallStaticBooleanMethod(ChatActivityFacade, sendArkAppMessage,
                                            rt, session, arkMsg);
    } else {
        return false;
    }
}

EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    jclass clazz = env->FindClass("nil/nadph/qnotified/util/Utils");
    if (!clazz) {
        __android_log_print(ANDROID_LOG_ERROR, "QNdump",
                            "cannot get class: Utils");
        return -1;
    }
    JNINativeMethod lMethods[1];
    lMethods[0].name = "ntGetBuildTimestamp";
    lMethods[0].signature = "()J";
    lMethods[0].fnPtr = (void *) &doGetBuildTimestamp;
    if (env->RegisterNatives(clazz, lMethods, 1)) {
        __android_log_print(ANDROID_LOG_INFO, "QNdump", "register native method[0] failed!\n");
        return -1;
    }
    jclass appInterface = env->FindClass("com/tencent/mobileqq/app/QQAppInterface");
    if (appInterface == nullptr) {
        env->ExceptionClear();
        __android_log_print(ANDROID_LOG_WARN, "QNdump", "not seeming in host, skip native hooks");
    } else {
        clazz = env->FindClass("nil/nadph/qnotified/hook/CardMsgHook");
        lMethods[0].name = "ntSendCardMsg";
        lMethods[0].signature = "(Lcom/tencent/mobileqq/app/QQAppInterface;Landroid/os/Parcelable;Ljava/lang/String;)Z";
        lMethods[0].fnPtr = (void *) &handleSendCardMsg;
        if (env->RegisterNatives(clazz, lMethods, 1)) {
            __android_log_print(ANDROID_LOG_INFO, "QNdump", "register native method[1] failed!\n");
            return -1;
        }
        clazz = env->FindClass("nil/nadph/qnotified/util/SendBatchMsg");
        lMethods[0].name = "ntSendBatchMessages";
        lMethods[0].signature = "(Lcom/tencent/mobileqq/app/QQAppInterface;Landroid/content/Context;Ljava/lang/String;[I[J)Z";
        lMethods[0].fnPtr = (void *) &handleSendBatchMessages;
        if (env->RegisterNatives(clazz, lMethods, 1)) {
            __android_log_print(ANDROID_LOG_INFO, "QNdump", "register native method[2] failed!\n");
            return -1;
        }
    }
    return JNI_VERSION_1_4;
}