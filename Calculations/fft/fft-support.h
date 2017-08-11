#ifndef _fftbfpsupport
#define _fftbfpsupport

#include "jni.h"
#include "dll_export.h"

typedef struct Complex
{
	jint re;
	jint im;
} Complex;

typedef struct ComplexLong
{
	jlong re;
	jlong im;
} ComplexLong;

typedef struct Complexd
{
	jdouble re;
	jdouble im;
} Complexd;

#ifdef __cplusplus
extern "C" {
#endif

	DLLEXPORT void TwidleFactors(jint, Complex*);
	DLLEXPORT unsigned int BitReverse(jint M, unsigned int num);
	DLLEXPORT void Hanning(jint, jint, jint*, Complex*, Complex*);
	DLLEXPORT void Averaging(jint, jint, jint, jint, jint, Complex*, jlong*);
	DLLEXPORT void CrossAveraging(jint, jint, jint, jint, jint, Complex*, jint, Complex*, ComplexLong*);
	DLLEXPORT void Reset(jint, jlong*, jint, jint*);
	DLLEXPORT void ResetCross(jint, ComplexLong*, jint, jint*);
	DLLEXPORT void AveragingIntensity(jint, jint, jint, jint, jint, jint, Complex*, Complex*, jlong*);
	DLLEXPORT void ResetIntensity(jint, jlong*);

#ifdef __cplusplus
}
#endif
#endif
