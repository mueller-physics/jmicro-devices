#ifndef INC_DEVCONNECT_JNI_HELPER
#define INC_DEVCONNECT_JNI_HELPER

#include <jni.h>

// throws an exception if p==null
void nullcheck( JNIEnv * env, void *p );

// return a jarray filled with n ints
jintArray ints_to_jintArray( JNIEnv * env, int n, ...);

// return a jarray filled with the first n ints from 'array'
jintArray intp_to_jintArray( JNIEnv * env, int n, int *array);

#endif
