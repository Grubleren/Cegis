#ifndef _fftbfp
#define _fftbfp

#include "jni.h"
#include "dll_export.h"
#include "fft-support.h"

#ifdef __cplusplus
extern "C" {
#endif

DLLEXPORT void FFT(jint, Complex*, Complex*, jint*);

#ifdef __cplusplus
}
#endif
#endif