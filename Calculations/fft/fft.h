#ifndef _Fft
#define _Fft

#include <jni.h>
#include "dll_export.h"

#ifdef __cplusplus
extern "C" {
#endif

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Init
		(JNIEnv *, jclass, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_FftForward
		(JNIEnv *, jclass, jobject, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_AutoSpectrum
		(JNIEnv *, jclass, jobject, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Cross
		(JNIEnv *, jclass, jobject,  jobject, jobject, jobject, jobject, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Auto
		(JNIEnv *, jclass, jobject, jobject, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Reset
		(JNIEnv *, jclass, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_ResetCross
		(JNIEnv *, jclass, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_PC
		(JNIEnv *, jclass, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Intensity
		(JNIEnv *, jclass, jobject, jobject, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_ResetIntensity
		(JNIEnv *, jclass, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_AutoSpectrumOverlap
		(JNIEnv *, jclass, jobject, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_CrossSpectrumOverlap
		(JNIEnv *, jclass, jobject, jobject, jobject, jobject);

	DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_FrfImpCoh
		(JNIEnv *, jclass, jobject, jobject, jobject, jobject, jobject, jobject, jobject);


#ifdef __cplusplus
}
#endif
#endif
