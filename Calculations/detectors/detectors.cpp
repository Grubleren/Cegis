#include "detectors.h"
#include "stdlib.h"

DLLEXPORT jint DLLCALL Java_jh_slm_calculations_Detectors_Peak
(JNIEnv *env, jclass obj, jobject input, jint max){

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 4);

	jint* inputPointer = (jint*)env->GetDirectBufferAddress(input);

	for (int i = 0; i < length; i++){
		int value = abs(inputPointer[i] << 8);
		if (value > max)
			max = value;
	}

	return max;
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Detectors_Square
(JNIEnv *env, jclass obj, jobject input, jobject output){

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 4);

	jlong noSquareSize = env->GetDirectBufferCapacity(output);
	jint noSquare = (jint)(noSquareSize / 8);
	jint interval = length / noSquare;

	jint* inputPointer = (jint*)env->GetDirectBufferAddress(input);
	jlong* outputPointer = (jlong*)env->GetDirectBufferAddress(output);

	for (int i = 0; i < noSquare; i++){
		jlong dose = 0;
		for (int j = 0; j < interval; j++){
			dose += (jlong)inputPointer[j + i * interval] * (jlong)inputPointer[j + i * interval];
		}
		outputPointer[i] = dose / interval;
	}
}

DLLEXPORT jlong DLLCALL Java_jh_slm_calculations_Detectors_Leq
(JNIEnv *env, jclass obj, jobject input){

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 8);

	jlong* inputPointer = (jlong*)env->GetDirectBufferAddress(input);

	jlong dose = 0;
	for (int i = 0; i < length; i++){
		dose += inputPointer[i] << 16;
	}

	return dose / length;
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Detectors_Rms
(JNIEnv *env, jclass obj, jobject input, jobject rmsOutput, jlongArray dose, jlongArray doseMax, jobject state, jint noOutputs){

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 8);

	jlong* inputPointer = (jlong*)env->GetDirectBufferAddress(input);
	jlong* rmsOutputPointer = (jlong*)env->GetDirectBufferAddress(rmsOutput);
	jint* statePointer = (jint*)env->GetDirectBufferAddress(state);

	jlong* dosePointer = env->GetLongArrayElements(dose, NULL);
	jlong* doseMaxPointer = env->GetLongArrayElements(doseMax, NULL);

	jint noBiQuads = statePointer[0];
	jint* biQuads = statePointer + 1;
	jint* delayLines = biQuads + 5 * noBiQuads;

	jint* bq = biQuads;
	jint a1 = *bq;
	jint a2 = *(bq + 1);
	jint b0 = *(bq + 2);
	jint b1 = *(bq + 3);
	jint b2 = *(bq + 4);
	jlong w_1 = *((jlong*)delayLines);

	for (int i = 0; i < length; i++){
		rmsOutputPointer[i] = (((jlong)-a1 * w_1)>> 16) + (jlong)b0 * inputPointer[i];
		w_1 = rmsOutputPointer[i];
	}

	*((jlong*)delayLines) = w_1;

	int interval = length / noOutputs;

	for (jint j = 0; j < noOutputs; j++)
	{
		jlong max = 0;
		for (jint i = 0; i < interval; i++){
			if (rmsOutputPointer[i + interval * j] > max)
				max = rmsOutputPointer[i + interval * j];
		}
		dosePointer[j] = rmsOutputPointer[j * interval];
		doseMaxPointer[j] = max;
	}


	env->ReleaseLongArrayElements(dose, dosePointer, 0);
	env->ReleaseLongArrayElements(doseMax, doseMaxPointer, 0);

}
