#include "upsampling.h"
#include "stdlib.h"
#include "string.h"

DLLEXPORT jobject DLLCALL Java_jh_slm_calculations_Upsampling_Init
(JNIEnv *env, jclass obj, jint downFactor, jint upFactor, jintArray coefficients){

	jint coefLength = env->GetArrayLength(coefficients);
	jint* coefPointer = env->GetIntArrayElements(coefficients, NULL);

	jint subFilterLength = coefLength / upFactor;
	jint delayLineLength = subFilterLength;
	jint size = (4 + 2 * delayLineLength + delayLineLength + coefLength) * 4;
	jint* statePointer = (jint*)malloc(size);
	jobject state = env->NewDirectByteBuffer(statePointer, size);

	statePointer[0] = downFactor;
	statePointer[1] = upFactor;
	statePointer[2] = subFilterLength;
	statePointer[3] = coefLength;

	jint* delayLine = statePointer + 4 + 2 * delayLineLength;
	jint* coef = delayLine + delayLineLength;

	for (jint i = 0; i < subFilterLength; i++){
		delayLine[i] = 0;
	}

	for (jint i = 0; i < coefLength; i++){
		coef[i] = coefPointer[i];
	}

	env->ReleaseIntArrayElements(coefficients, coefPointer, 0);

	return state;
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Upsampling_Resample
(JNIEnv *env, jclass obj, jobject input, jobject output, jobject state){

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 4);

	jint* inputPointer = (jint*)env->GetDirectBufferAddress(input);
	jint* outputPointer = (jint*)env->GetDirectBufferAddress(output);

	jint* statePointer = (jint*)env->GetDirectBufferAddress(state);
	jint downFactor = statePointer[0];
	jint upFactor = statePointer[1];
	jint subFilterLength = statePointer[2];
	jint coefLength = statePointer[3];
	jint delayLineLength = subFilterLength;

	jint* initialPointer = statePointer + 4;
	jint* delayLine = initialPointer + 2 * delayLineLength;
	jint* coef = delayLine + delayLineLength;

	memcpy(initialPointer, delayLine, delayLineLength * 4);
	memcpy(initialPointer + delayLineLength, inputPointer, delayLineLength * 4);
	jint* dataPointer = initialPointer;

	bool first = true;

	jint N = length * upFactor / downFactor;
	jint n = 0;
	jint m = 0;
	jint k = 0;
	jint c = 0;

	while (m < N)
	{
		if (first && k >= delayLineLength)
		{
			dataPointer = inputPointer;
			c = delayLineLength;
			first = false;
		}

		jlong sum = 0;
		jint offset = 0;
		for (jint j = 0; j < delayLineLength; j++)
		{
			sum += ((jlong)coef[offset + n] * dataPointer[k - c + j]);
			offset += upFactor;
		}
		outputPointer[m] = (jint)(sum >> 30);

		m++;
		jint q = m * downFactor;
		jint p = q / upFactor;
		k = p * upFactor == q ? p : p + 1;
		n = k * upFactor - q;
	}

	memcpy(delayLine, inputPointer + length - delayLineLength, delayLineLength * 4);

}

