#ifndef _statistics
#define _statistics

#include <jni.h>
#include "dll_export.h"


#ifdef __cplusplus

extern "C" {
#endif

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Statistics_Distribution
		(JNIEnv *env, jclass obj, jobject input, jintArray distribution, jintArray lnValues, jfloatArray lnPercentages, jintArray range, jobject state);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Statistics_Reset
		(JNIEnv *env, jclass obj, jintArray distribution, jintArray range);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Statistics_PC
		(JNIEnv *env, jclass obj, jobject state);

	DLLEXPORT void UpdateDistribution(jlong* inputPointer, jint length, jint* distributionPointer, jint lower, jint upper, jint classWidth, jint nClasses);
	DLLEXPORT void CalculateLnValues(jint* distributionPointer, jint* lnValuesPointer, jfloat* lnPercentagesPointer, jint lower, jint upper, jint classWidth, jint nClasses);

#ifdef __cplusplus
}
#endif
#endif
