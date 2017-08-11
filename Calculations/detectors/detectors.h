#ifndef _detectors
#define _detectors

#include <jni.h>
#include "dll_export.h"


#ifdef __cplusplus

extern "C" {
#endif

	DLLEXPORT jint DLLCALL Java_jh_slm_calculations_Detectors_Peak
		(JNIEnv *, jclass, jobject, jint);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Detectors_Square
		(JNIEnv *, jclass, jobject, jobject);

	DLLEXPORT jlong DLLCALL Java_jh_slm_calculations_Detectors_Leq
		(JNIEnv *, jclass, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Detectors_Rms
		(JNIEnv *, jclass, jobject, jobject, jlongArray, jlongArray, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
