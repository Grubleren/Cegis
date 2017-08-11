#ifndef _allocator
#define _allocator

#include <jni.h>
#include "dll_export.h"

#ifdef __cplusplus
extern "C" {
#endif

	DLLEXPORT jobject DLLCALL Java_jh_slm_calculations_Allocator_Allocate
		(JNIEnv *, jclass, jint);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_Free
		(JNIEnv *, jclass, jobject);

	DLLEXPORT jintArray DLLCALL Java_jh_slm_calculations_Allocator_DeRefInt
		(JNIEnv *, jclass, jobject);

	DLLEXPORT jlongArray DLLCALL Java_jh_slm_calculations_Allocator_DeRefLong
		(JNIEnv *, jclass, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_Copy
		(JNIEnv *env, jclass obj, jintArray, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_CopyBytes
		(JNIEnv *env, jclass obj, jbyteArray, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_Copy16To32
		(JNIEnv *env, jclass obj, jbyteArray, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_Copy24To32
		(JNIEnv *env, jclass obj, jbyteArray, jobject);

#ifdef __cplusplus
}
#endif
#endif
