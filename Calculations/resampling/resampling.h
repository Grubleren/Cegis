#ifndef _resampling
#define _resampling

#include <jni.h>
#include "dll_export.h"

#ifdef __cplusplus
extern "C" {
#endif

	DLLEXPORT void DLLCALL Init(jint downFactor, jint upFactor, jint coefLength, jint* coefPointer, jint& size, jint** state);
	DLLEXPORT void DLLCALL Resample(jint length, jint* inputPointer, jint* outputPointer, jint* statePointer);

	DLLEXPORT jobject DLLCALL Java_jh_slm_calculations_Resampling_Init
		(JNIEnv *env, jclass obj, jint downFactor, jint upFactor, jintArray coefficients);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Resampling_Resample
		(JNIEnv *env, jclass obj, jint length, jobject input, jobject output, jobject state);

#ifdef __cplusplus
}
#endif
#endif
