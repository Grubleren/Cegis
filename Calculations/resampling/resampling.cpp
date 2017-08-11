#include "resampling.h"
#include "stdlib.h"
#include "string.h"
#include "math.h"

DLLEXPORT void DLLCALL Init(jint downFactor, jint upFactor, jint coefLength, jint* coefPointer, jint& size, jint** state)
{
	jint subFilterLength = coefLength / upFactor;
	jint delayLineLength = subFilterLength;

	size = (7 + 2 * delayLineLength + delayLineLength + coefLength) * 4;
	*state = (jint*)malloc(size);
	jint* statePointer = *state;

	statePointer[0] = downFactor;
	statePointer[1] = upFactor;
	statePointer[2] = delayLineLength;
	statePointer[3] = coefLength;
	statePointer[4] = (jint)(log((double)upFactor) / log((double)2));
	statePointer[5] = 0;
	statePointer[6] = 0;

	jint* delayLine = statePointer + 7 + 2 * delayLineLength;
	jint* coef = delayLine + delayLineLength;

	for (jint i = 0; i < delayLineLength; i++){
		delayLine[i] = 0;
	}

	for (jint i = 0; i < coefLength; i++){
		coef[i] = coefPointer[i];
	}
}

DLLEXPORT void DLLCALL Resample(jint length, jint* inputPointer, jint* outputPointer, jint* statePointer)
{
	jint downFactor = statePointer[0];
	jint upFactor = statePointer[1];
	jint delayLineLength = statePointer[2];
	jint coefLength = statePointer[3];
	jint upLog2 = statePointer[4];

	jint* initialPointer = statePointer + 7;
	jint* delayLine = initialPointer + 2 * delayLineLength;
	jint* coef = delayLine + delayLineLength;

	memcpy(initialPointer, delayLine, delayLineLength * 4);
	memcpy(initialPointer + delayLineLength, inputPointer, delayLineLength * 4);
	jint* dataPointer = initialPointer;

	bool first = true;

	jint N = length * upFactor / downFactor;
	jint m = 0;
	jint k = 0;
	jint n = 0;
	jint dk = downFactor / upFactor + 1;
	jint dn = dk * upFactor - downFactor;

	do
	{
		if (first && k >= delayLineLength)
		{
			dataPointer = inputPointer;
			k -= delayLineLength;
			first = false;
		}

		jlong sum = 0;
		jint offset = 0;
		for (jint j = 0; j < delayLineLength; j++)
		{
			sum += ((jlong)coef[offset + n] * dataPointer[k + j]);
			offset += upFactor;
		}
		outputPointer[m] = (jint)(sum >> (30 - upLog2));

		m++;
		k += dk;
		n += dn;
		if (n >= upFactor)
		{
			n -= upFactor;
			k--;
		}
	} while (m < N);

	memcpy(delayLine, inputPointer + length - delayLineLength, delayLineLength * 4);
}

DLLEXPORT jobject DLLCALL Java_jh_slm_calculations_Resampling_Init
(JNIEnv *env, jclass obj, jint downFactor, jint upFactor, jintArray coefficients){

	jint coefLength = env->GetArrayLength(coefficients);
	jint* coefPointer = env->GetIntArrayElements(coefficients, NULL);

	jint* statePointer;
	jint size;

	Init(downFactor, upFactor, coefLength, coefPointer, size, &statePointer);

	jobject state = env->NewDirectByteBuffer(statePointer, size);

	env->ReleaseIntArrayElements(coefficients, coefPointer, 0);

	return state;
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Resampling_Resample
(JNIEnv *env, jclass obj, jint length, jobject input, jobject output, jobject state){


	jint* inputPointer = (jint*)env->GetDirectBufferAddress(input);
	jint* outputPointer = (jint*)env->GetDirectBufferAddress(output);

	jint* statePointer = (jint*)env->GetDirectBufferAddress(state);

	Resample(length, inputPointer, outputPointer, statePointer);

}

