#ifndef _upsampling
#define _upsampling

#include <jni.h>
#include "dll_export.h"

#ifdef __cplusplus
extern "C" {
#endif

	DLLEXPORT jobject DLLCALL Java_jh_slm_calculations_Upsampling_Init
		(JNIEnv *env, jclass obj, jint downFactor, jint upFactor, jintArray coefficients);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Upsampling_Resample
		(JNIEnv *env, jclass obj, jobject input, jobject output, jobject state);

#ifdef __cplusplus
}
#endif
#endif
