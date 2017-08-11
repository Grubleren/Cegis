#include "iirfilter.h"
#include <stdlib.h>

DLLEXPORT jobject DLLCALL Java_jh_slm_calculations_Iirfilter_BiQuads
(JNIEnv *env, jclass obj, jintArray biQuads){

	jint length = env->GetArrayLength(biQuads);
	jint* biQuadsPointer = env->GetIntArrayElements(biQuads, NULL);

	jint size = length * 4;
	jint* statePointer = (jint*)malloc(size);
	jobject state = env->NewDirectByteBuffer(statePointer, size);

	for (jint i = 0; i < length; i++){
		statePointer[i] = biQuadsPointer[i];
	}

	env->ReleaseIntArrayElements(biQuads, biQuadsPointer, 0);

	return state;
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Iirfilter_Filter
(JNIEnv *env, jclass obj, jobject input, jobject output, jobject state){

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 4);

	jint* inputPointer = (jint*)env->GetDirectBufferAddress(input);
	jint* outputPointer = (jint*)env->GetDirectBufferAddress(output);
	jint* dataPointer = inputPointer;

	jint* statePointer = (jint*)env->GetDirectBufferAddress(state);
	jint noBiQuads = statePointer[0];
	jint* biQuads = statePointer + 1;
	jint* delayLines = biQuads + 5 * noBiQuads;

	jint w_1 = 0;
	jint w_2 = 0;

	for (jint j = 0; j < noBiQuads; j++){
		jint k = 5 * j;
		jint l = 2 * j;

		jint* bq = biQuads + k;
		jint a1 = *bq;
		jint a2 = *(bq + 1);
		jint b0 = *(bq + 2);
		jint b1 = *(bq + 3);
		jint b2 = *(bq + 4);

		jint w_1 = *(delayLines + l);
		jint w_2 = *(delayLines + l + 1);

		int w;
		for (int i = 0; i < length; i++){
			w = dataPointer[i] - (((jlong)a1 * w_1) >> 32) - (((jlong)a2 * w_2) >> 32);
			w <<= 2;
			outputPointer[i] = (((jlong)b0 * w) >> 32) + (((jlong)b1 * w_1) >> 32) + (((jlong)b2 * w_2) >> 32);

			w_2 = w_1;
			w_1 = w;
		}

		*(delayLines + l) = w_1;
		*(delayLines + l + 1) = w_2;
		dataPointer = outputPointer;
	}
}
