#include "fft.h"
#include "fft-support.h"
#include "fftbfp.h"
#include "stdlib.h"
#include "string.h"
//#include "stdio.h"

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Init
(JNIEnv *env, jclass obj, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "N","I");
	jint N = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "W","Ljava/lang/Object;");
	jobject W = env->GetObjectField(state, id);
	Complex* wPointer = (Complex*)env->GetDirectBufferAddress(W); 

	TwidleFactors(N, wPointer);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_FftForward
(JNIEnv *env, jclass obj, jobject input, jobject complexSpectrum, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "M","I");
	jint M = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "N","I");
	jint N = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "numberOfAverages","I");
	jint numberOfAverages = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "count","I");
	jint count = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "blockExponent","I");
	jint blockExponent = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "W","Ljava/lang/Object;");
	jobject W = env->GetObjectField(state, id);
	Complex* wPointer = (Complex*)env->GetDirectBufferAddress(W); 

	id = env->GetFieldID(stateClass, "delayLine","Ljava/lang/Object;");
	jobject delayLine = env->GetObjectField(state, id);
	jint* delayLinePointer = (jint*)env->GetDirectBufferAddress(delayLine); 

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 4);
	jint* inputPointer = (jint*)env->GetDirectBufferAddress(input); 

	Complex* complexSpectrumPointer = (Complex*)env->GetDirectBufferAddress(complexSpectrum); 

	Hanning(M, N, inputPointer, complexSpectrumPointer, wPointer);
	FFT(M, complexSpectrumPointer, wPointer, &blockExponent);
	count++;

	id = env->GetFieldID(stateClass, "blockExponent","I");
	env->SetIntField(state, id, blockExponent);

	id = env->GetFieldID(stateClass, "count","I");
	env->SetIntField(state, id, count);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_AutoSpectrum
(JNIEnv *env, jclass obj, jobject input, jobject autoSpectrum, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "running","Z");
	jboolean running = env->GetBooleanField(state, id);

	if(!running)
		return;

	id = env->GetFieldID(stateClass, "M","I");
	jint M = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "N","I");
	jint N = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "numberOfAverages","I");
	jint numberOfAverages = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "count","I");
	jint count = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "W","Ljava/lang/Object;");
	jobject W = env->GetObjectField(state, id);
	Complex* wPointer = (Complex*)env->GetDirectBufferAddress(W); 

	id = env->GetFieldID(stateClass, "delayLine","Ljava/lang/Object;");
	jobject delayLine = env->GetObjectField(state, id);
	jint* delayLinePointer = (jint*)env->GetDirectBufferAddress(delayLine); 

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 4);
	jint* inputPointer = (jint*)env->GetDirectBufferAddress(input); 

	size = env->GetDirectBufferCapacity(autoSpectrum);
	jint autoSpectrumLength = (jint)(size / 8);
	jlong* autoSpectrumPointer = (jlong*)env->GetDirectBufferAddress(autoSpectrum); 

	Complex* Y = (Complex*)malloc(2 * N * 4);
	jint blockExponent;

	if (count != 0)
	{
		memcpy(delayLinePointer + N / 2, inputPointer, N / 2 * 4);
		Hanning(M, N, delayLinePointer, Y, wPointer);
		FFT(M, Y, wPointer, &blockExponent);
		Averaging(M, autoSpectrumLength - 1, numberOfAverages, count, blockExponent, Y, autoSpectrumPointer);
		count++;
	}
	else
		autoSpectrumPointer[autoSpectrumLength - 1] = -1000000;
	memcpy(delayLinePointer, inputPointer + length - N / 2, N / 2 * 4);

	jint P = 2 * length / N - 1;

	for (jint i = 0; i < P; i++)
	{
		Hanning(M, N, inputPointer + N / 2 * i, Y, wPointer);
		FFT(M, Y, wPointer, &blockExponent);
		Averaging(M, autoSpectrumLength - 1, numberOfAverages, count, blockExponent, Y, autoSpectrumPointer);
		count++;
	}

	free(Y);

	id = env->GetFieldID(stateClass, "count","I");
	env->SetIntField(state, id, count);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Auto
(JNIEnv *env, jclass obj, jobject input, jobject instSpectrum, jobject autoSpectrum, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "M","I");
	jint M = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "N","I");
	jint N = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "numberOfAverages","I");
	jint numberOfAverages = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "count","I");
	jint count = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "W","Ljava/lang/Object;");
	jobject W = env->GetObjectField(state, id);
	Complex* wPointer = (Complex*)env->GetDirectBufferAddress(W); 

	id = env->GetFieldID(stateClass, "delayLine","Ljava/lang/Object;");
	jobject delayLine = env->GetObjectField(state, id);
	jint* delayLinePointer = (jint*)env->GetDirectBufferAddress(delayLine); 

	jint* inputPointer = (jint*)env->GetDirectBufferAddress(input); 

	jlong size = env->GetDirectBufferCapacity(instSpectrum);
	jint spectrumLength = (jint)(size / 8);
	jint* instSpectrumPointer = (jint*)env->GetDirectBufferAddress(instSpectrum); 

	jlong* autoSpectrumPointer = (jlong*)env->GetDirectBufferAddress(autoSpectrum); 

	Complex* Y = (Complex*)malloc(2 * N * 4);
	jint blockExponent;

	Hanning(M, N, inputPointer, Y, wPointer);
	FFT(M, Y, wPointer, &blockExponent);
	Averaging(M, spectrumLength - 1, numberOfAverages, count, blockExponent, Y, autoSpectrumPointer);
	memcpy(instSpectrumPointer, Y, spectrumLength * 2 * 4);
	count++;

	free(Y);

	id = env->GetFieldID(stateClass, "blockExponent","I");
	env->SetIntField(state, id, blockExponent);

	id = env->GetFieldID(stateClass, "count","I");
	env->SetIntField(state, id, count);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Cross
(JNIEnv *env, jclass obj, jobject instSpectrum1,  jobject instSpectrum2, jobject autoSpectrum1, jobject autoSpectrum2, jobject crossSpectrum, jobject state1, jobject state2)
{
	//if (state1 == NULL)
	//	printf("state1 null\n");
	//if (state2 == NULL)
	//	printf("state2 null\n");

	//	printf("Trying to get state class\n");
	jclass stateClass = env->GetObjectClass(state1);

	//	printf("Got state class\n");

	jfieldID id = env->GetFieldID(stateClass, "M","I");
	jint M = env->GetIntField(state1, id);

	id = env->GetFieldID(stateClass, "N","I");
	jint N = env->GetIntField(state1, id);

	id = env->GetFieldID(stateClass, "numberOfAverages","I");
	jint numberOfAverages = env->GetIntField(state1, id);

	id = env->GetFieldID(stateClass, "count","I");
	jint count = env->GetIntField(state1, id);

	id = env->GetFieldID(stateClass, "blockExponent","I");
	jint blockExponent1 = env->GetIntField(state1, id);
	jint blockExponent2 = env->GetIntField(state2, id);

	Complex* instSpectrum1Pointer = (Complex*)env->GetDirectBufferAddress(instSpectrum1); 
	Complex* instSpectrum2Pointer = (Complex*)env->GetDirectBufferAddress(instSpectrum2); 

	jlong size = env->GetDirectBufferCapacity(autoSpectrum1);
	jint autoSpectrumLength = (jint)(size / 8);

	jlong* autoSpectrum1Pointer = (jlong*)env->GetDirectBufferAddress(autoSpectrum1); 
	jlong* autoSpectrum2Pointer = (jlong*)env->GetDirectBufferAddress(autoSpectrum2); 

	ComplexLong* crossSpectrumPointer = (ComplexLong*)env->GetDirectBufferAddress(crossSpectrum); 

	CrossAveraging(M, autoSpectrumLength, numberOfAverages, count, blockExponent1, instSpectrum1Pointer, blockExponent2, instSpectrum2Pointer, crossSpectrumPointer);

	id = env->GetFieldID(stateClass, "count","I");
	env->SetIntField(state1, id, count);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Reset
(JNIEnv *env, jclass obj, jobject autoSpectrum, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "count","I");
	jint count = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "saved","I");
	jint saved = env->GetIntField(state, id);

	jlong size = env->GetDirectBufferCapacity(autoSpectrum);
	jint autoSpectrumLength = (jint)(size / 8 - 1);
	jlong* autoSpectrumPointer = (jlong*)env->GetDirectBufferAddress(autoSpectrum); 

	id = env->GetFieldID(stateClass, "delayLine","Ljava/lang/Object;");
	jobject delayLine = env->GetObjectField(state, id);
	jint* delayLinePointer = (jint*)env->GetDirectBufferAddress(delayLine);
	jlong delayLineLength = env->GetDirectBufferCapacity(delayLine)/4;

	Reset(autoSpectrumLength, autoSpectrumPointer, delayLineLength, delayLinePointer);

	count = 0;
	saved = 0;
	id = env->GetFieldID(stateClass, "count","I");
	env->SetIntField(state, id, count);
	id = env->GetFieldID(stateClass, "saved","I");
	env->SetIntField(state, id, saved);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_ResetCross
(JNIEnv *env, jclass obj, jobject crossSpectrum, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "count","I");
	jint count = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "saved","I");
	jint saved = env->GetIntField(state, id);

	jlong size = env->GetDirectBufferCapacity(crossSpectrum);
	jint crossSpectrumLength = (jint)((size / 8 - 1) / 2);

	ComplexLong* crossSpectrumPointer = (ComplexLong*)env->GetDirectBufferAddress(crossSpectrum); 

	id = env->GetFieldID(stateClass, "delayLine", "Ljava/lang/Object;");
	jobject delayLine = env->GetObjectField(state, id);
	jint* delayLinePointer = (jint*)env->GetDirectBufferAddress(delayLine);
	jlong delayLineLength = env->GetDirectBufferCapacity(delayLine)/4;

	ResetCross(crossSpectrumLength, crossSpectrumPointer, delayLineLength, delayLinePointer);

	count = 0;
	saved = 0;
	id = env->GetFieldID(stateClass, "count","I");
	env->SetIntField(state, id, count);
	id = env->GetFieldID(stateClass, "saved","I");
	env->SetIntField(state, id, saved);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_PC
(JNIEnv *env, jclass obj, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "running","Z");
	jboolean running = env->GetBooleanField(state, id);

	running = !running;

	env->SetBooleanField(state, id, running);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_Intensity
(JNIEnv *env, jclass obj, jobject input1, jobject input2, jobject intensity, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "running", "Z");
	jboolean running = env->GetBooleanField(state, id);

	if (!running)
		return;

	id = env->GetFieldID(stateClass, "M", "I");
	jint M = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "N", "I");
	jint N = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "numberOfAverages", "I");
	jint numberOfAverages = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "count", "I");
	jint count = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "W", "Ljava/lang/Object;");
	jobject W = env->GetObjectField(state, id);
	Complex* wPointer = (Complex*)env->GetDirectBufferAddress(W);

	id = env->GetFieldID(stateClass, "delayLine", "Ljava/lang/Object;");
	jobject delayLine = env->GetObjectField(state, id);
	jint* delayLinePointer = (jint*)env->GetDirectBufferAddress(delayLine);

	jlong size = env->GetDirectBufferCapacity(input1);
	jint length = (jint)(size / 4);
	jint* input1Pointer = (jint*)env->GetDirectBufferAddress(input1);
	jint* input2Pointer = (jint*)env->GetDirectBufferAddress(input2);

	size = env->GetDirectBufferCapacity(intensity);
	jint span = (jint)(size / 8 - 1) / 2;
	jlong* intensityPointer = (jlong*)env->GetDirectBufferAddress(intensity);

	Complex* Y1 = (Complex*)malloc(2 * N * 4);
	Complex* Y2 = (Complex*)malloc(2 * N * 4);
	jint blockExponent1;
	jint blockExponent2;

	if (count != 0)
	{
		memcpy(delayLinePointer + N / 2, input1Pointer, N / 2 * 4);
		Hanning(M, N, delayLinePointer, Y1, wPointer);
		FFT(M, Y1, wPointer, &blockExponent1);
		memcpy(delayLinePointer + N / 2 + N, input2Pointer, N / 2 * 4);
		Hanning(M, N, delayLinePointer + N, Y2, wPointer);
		FFT(M, Y2, wPointer, &blockExponent2);
		AveragingIntensity(M, span, numberOfAverages, count, blockExponent1, blockExponent2, Y1, Y2, intensityPointer);
		count++;
	}
	else
		intensityPointer[2*span] = -1000000;

	memcpy(delayLinePointer, input1Pointer + length - N / 2, N / 2 * 4);
	memcpy(delayLinePointer + N, input2Pointer + length - N / 2, N / 2 * 4);

	jint P = 2 * length / N - 1;

	for (jint i = 0; i < P; i++)
	{
		Hanning(M, N, input1Pointer + N / 2 * i, Y1, wPointer);
		FFT(M, Y1, wPointer, &blockExponent1);
		Hanning(M, N, input2Pointer + N / 2 * i, Y2, wPointer);
		FFT(M, Y2, wPointer, &blockExponent2);
		AveragingIntensity(M, span, numberOfAverages, count, blockExponent1, blockExponent2, Y1, Y2, intensityPointer);
		count++;
	}

	free(Y1);
	free(Y2);

	id = env->GetFieldID(stateClass, "count", "I");
	env->SetIntField(state, id, count);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_ResetIntensity
(JNIEnv *env, jclass obj, jobject intensity, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "count", "I");
	jint count = env->GetIntField(state, id);

	jlong size = env->GetDirectBufferCapacity(intensity);
	jint span = (jint)(size / 8 - 1)/2;
	jlong* intensityPointer = (jlong*)env->GetDirectBufferAddress(intensity);

	ResetIntensity(span, intensityPointer);

	count = 0;
	id = env->GetFieldID(stateClass, "count", "I");
	env->SetIntField(state, id, count);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_AutoSpectrumOverlap
(JNIEnv *env, jclass obj, jobject input, jobject autoSpectrum, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "running","Z");
	jboolean running = env->GetBooleanField(state, id);

	if(!running)
		return;

	id = env->GetFieldID(stateClass, "M","I");
	jint M = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "N","I");
	jint N = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "overlap","I");
	jint overlap = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "step","I");
	jint step = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "saved","I");
	jint saved = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "numberOfAverages","I");
	jint numberOfAverages = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "count","I");
	jint count = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "W","Ljava/lang/Object;");
	jobject W = env->GetObjectField(state, id);
	Complex* wPointer = (Complex*)env->GetDirectBufferAddress(W);

	id = env->GetFieldID(stateClass, "delayLine","Ljava/lang/Object;");
	jobject delayLine = env->GetObjectField(state, id);
	jint* delayLinePointer = (jint*)env->GetDirectBufferAddress(delayLine);
	jlong siz = env->GetDirectBufferCapacity(delayLine)/4;

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 4);
	jint* inputPointer = (jint*)env->GetDirectBufferAddress(input);

	size = env->GetDirectBufferCapacity(autoSpectrum);
	jint span = (jint)(size / 8 - 1);
	jlong* autoSpectrumPointer = (jlong*)env->GetDirectBufferAddress(autoSpectrum);

	Complex* Y = (Complex*)malloc(2 * N * 4);
	jint blockExponent;

	memcpy(delayLinePointer + saved, inputPointer, length * 4);

	int P = (saved + length - N + step) / step;

	for (int i = 0; i < P; i++)
	{
		Hanning(M, N, delayLinePointer + step * i, Y, wPointer);
		FFT(M, Y, wPointer, &blockExponent);
		Averaging(M, span, numberOfAverages, count, blockExponent, Y, autoSpectrumPointer);
		count++;
	}

	if (P > 0)
	{
		saved += length - step * P;
		memcpy(delayLinePointer, delayLinePointer + step * P, saved * 4);
	}
	else
		saved += length;

	free(Y);

	id = env->GetFieldID(stateClass, "saved", "I");
	env->SetIntField(state, id, saved);

	id = env->GetFieldID(stateClass, "count", "I");
	env->SetIntField(state, id, count);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_CrossSpectrumOverlap
(JNIEnv *env, jclass obj, jobject input1, jobject input2, jobject intensity, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "running", "Z");
	jboolean running = env->GetBooleanField(state, id);

	if (!running)
		return;

	id = env->GetFieldID(stateClass, "M", "I");
	jint M = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "N", "I");
	jint N = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "overlap","I");
	jint overlap = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "step","I");
	jint step = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "saved","I");
	jint saved = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "numberOfAverages", "I");
	jint numberOfAverages = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "count", "I");
	jint count = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "W", "Ljava/lang/Object;");
	jobject W = env->GetObjectField(state, id);
	Complex* wPointer = (Complex*)env->GetDirectBufferAddress(W);

	id = env->GetFieldID(stateClass, "delayLine", "Ljava/lang/Object;");
	jobject delayLine = env->GetObjectField(state, id);
	jint* delayLinePointer = (jint*)env->GetDirectBufferAddress(delayLine);

	jlong size = env->GetDirectBufferCapacity(input1);
	jint length = (jint)(size / 4);
	jint* input1Pointer = (jint*)env->GetDirectBufferAddress(input1);
	jint* input2Pointer = (jint*)env->GetDirectBufferAddress(input2);

	size = env->GetDirectBufferCapacity(intensity);
	jint span = (jint)(size / 8 - 1) / 2;
	jlong* intensityPointer = (jlong*)env->GetDirectBufferAddress(intensity);

	Complex* Y1 = (Complex*)malloc(2 * N * 4);
	Complex* Y2 = (Complex*)malloc(2 * N * 4);
	jint blockExponent1;
	jint blockExponent2;

	memcpy(delayLinePointer + saved, input1Pointer, length * 4);
	memcpy(delayLinePointer + N + length + saved, input2Pointer, length * 4);

	int P = (saved + length - N + step) / step;

	for (int i = 0; i < P; i++)
	{
		Hanning(M, N, delayLinePointer + step * i, Y1, wPointer);
		FFT(M, Y1, wPointer, &blockExponent1);
		Hanning(M, N, delayLinePointer + N + length + step * i, Y2, wPointer);
		FFT(M, Y2, wPointer, &blockExponent2);
		AveragingIntensity(M, span, numberOfAverages, count, blockExponent1, blockExponent2, Y1, Y2, intensityPointer);
		count++;
	}

	if (P > 0)
	{
		saved += length - step * P;
		memcpy(delayLinePointer, delayLinePointer + step * P, saved * 4);
		memcpy(delayLinePointer + N + length, delayLinePointer + N + length + step * P, saved * 4);
	}
	else
		saved += length;

	free(Y1);
	free(Y2);

	id = env->GetFieldID(stateClass, "saved", "I");
	env->SetIntField(state, id, saved);

	id = env->GetFieldID(stateClass, "count", "I");
	env->SetIntField(state, id, count);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Fft_FrfImpCoh
(JNIEnv *env, jclass obj, jobject input1, jobject input2, jobject input3, jobject frf, jobject imp, jobject coh, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "M", "I");
	jint M = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "N", "I");
	jint N = env->GetIntField(state, id);

	id = env->GetFieldID(stateClass, "W", "Ljava/lang/Object;");
	jobject W = env->GetObjectField(state, id);
	Complex* wPointer = (Complex*)env->GetDirectBufferAddress(W);

	jlong* input1Pointer = (jlong*)env->GetDirectBufferAddress(input1);
	jlong* input2Pointer = (jlong*)env->GetDirectBufferAddress(input2);
	jlong* input3Pointer = (jlong*)env->GetDirectBufferAddress(input3);

	jint size = env->GetDirectBufferCapacity(frf);
	jint span = (size / 8 - 1) / 2;
	jlong* frfPointer = (jlong*)env->GetDirectBufferAddress(frf);

	jlong* impPointer = (jlong*)env->GetDirectBufferAddress(imp);

	jlong* cohPointer = (jlong*)env->GetDirectBufferAddress(coh);

	jint blockExponent1 = (jint)input1Pointer[span];
	jint blockExponent2 = (jint)input2Pointer[span];
	jint blockExponent3 = (jint)input3Pointer[2*span];

	for ( int i = 0; i < span; i++)
	{
		if((input1Pointer[i] >> 16) != 0)
		{
			frfPointer[2*i] = input3Pointer[2*i] / (input1Pointer[i] >> 16);
			frfPointer[2*i+1] = input3Pointer[2*i+1] / (input1Pointer[i] >> 16);
		}
		else
		{
			frfPointer[2*i] = 0;
			frfPointer[2*i+1] = 0;
		}
	}
	jint frfBlockExponent = blockExponent3 - blockExponent1 - 32 - 16;
	frfPointer[2*span] = frfBlockExponent;

	Complex* X = (Complex*)malloc(2 * N * 4);
	Complex* Y = (Complex*)malloc(2 * N * 4);
	memset(X + span, 0, 2 * N * 4 - span * 2 * 4);
	for(int i=0;i<span;i++)
	{
		X[i].re = (jint)(frfPointer[2*i]);
		X[i].im = (jint)(frfPointer[2*i + 1]);
	}
	for(int i=span;i<N/2;i++)
	{
		X[i].re = (jint)(frfPointer[2*span - 2]);
		X[i].im = (jint)(frfPointer[2*span - 1]);
	}
	for ( int i = 0; i < N; i++)
	{
		Y[BitReverse(M, i)].re = X[i].re;
		Y[BitReverse(M, i)].im = -X[i].im;
	}

	jint blockExponent;
	FFT(M, Y, wPointer, &blockExponent);

	for ( int i = 0; i < N/2; i++)
	{
		impPointer[2*i] = Y[i].re;
		impPointer[2*i+1] = -Y[i].im;
	}
	impPointer[N] = blockExponent + frfBlockExponent - M;

	for ( int i = 0; i < span; i++)
	{
		if((input1Pointer[i] >> 15) != 0 && (input2Pointer[i] >> 15) != 0)
		{
			cohPointer[i] = (jint)((jlong)(jint)(input3Pointer[2*i]/(input1Pointer[i] >> 15)) * (jint)(input3Pointer[2*i]/(input2Pointer[i] >> 15)) +
					             (jlong)(jint)(input3Pointer[2*i+1]/(input1Pointer[i] >> 15)) * (jint)(input3Pointer[2*i+1]/(input2Pointer[i] >> 15)));
		}
		else
		{
			cohPointer[i] = 0;
		}
	}
	jint cohBlockExponent = 2*blockExponent3 - blockExponent1 - blockExponent2 - 30;
	cohPointer[span] = cohBlockExponent;

	delete(X);
	delete(Y);
}
