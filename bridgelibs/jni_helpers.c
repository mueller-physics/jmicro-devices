#include "jni_helpers.h"
#include <stdlib.h>

// check for null pointer
void nullcheck( JNIEnv * env, void *p ) {
    if (p==NULL) {
	(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "null pointer (mem alloc failed?)");
    }
}

// create java array from ints
jintArray ints_to_jintArray( JNIEnv * env, int n, ...) {

    va_list ap;
    va_start(ap, n);

    jintArray res = (*env)->NewIntArray(env, n);
    nullcheck(env, res);
    
    jint  *array = malloc(n*sizeof(jint));
    nullcheck(env, array);

    for (int i=0; i<n; i++) {
	array[i] = va_arg(ap, int);
    }

    (*env)->SetIntArrayRegion(env, res, 0, n, array);
    free(array);

    return res;

} 


// create java array from int pointer
jintArray intp_to_jintArray( JNIEnv * env, int n, int *array) {

    jintArray res = (*env)->NewIntArray(env, n);
    nullcheck(env, res);

    (*env)->SetIntArrayRegion(env, res, 0, n, array);
    
    return res;

} 


