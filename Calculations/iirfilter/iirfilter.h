
#ifndef _iirfilter
#define _iirfilter

#include <jni.h>
#include "dll_export.h"

#ifdef __cplusplus
extern "C" {
#endif

	DLLEXPORT jobject DLLCALL  Java_jh_slm_calculations_Iirfilter_BiQuads
		(JNIEnv *, jclass, jintArray);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Iirfilter_Filter
		(JNIEnv *, jclass, jobject, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif
