#include "statistics.h"
#include "stdlib.h"
#include "math.h"

DLLEXPORT void UpdateDistribution(jlong* inputPointer, jint length, jint* distributionPointer, jint lower, jint upper, jint classWidth, jint nClasses)
{

	for (jint i = 0; i < length; i++)
	{
		jint level = (jint)((100 * 10 * log10((jfloat)(inputPointer[i])) - lower) / classWidth);
		if (level < 0)
			distributionPointer[nClasses]++;
		else if (level < nClasses)
			distributionPointer[level]++;
		else
			distributionPointer[nClasses + 1]++;

		distributionPointer[nClasses + 2]++;
	}
}

DLLEXPORT void CalculateLnValues(jint* distributionPointer, jint* lnValuesPointer, jfloat* lnPercentagesPointer, jint lower, jint upper, jint classWidth, jint nClasses)
{
	jint nLn = 3;
	for (int i = 0; i < nLn; i++)
		lnValuesPointer[i] = lower;

	jint sum = distributionPointer[nClasses + 1];
	jint j = 0;
	jfloat limit = lnPercentagesPointer[j] * distributionPointer[nClasses + 2];
	for (jint i = nClasses - 1; i >= 0; i--)
	{
		sum += distributionPointer[i];
		if (sum > limit)
		{
			lnValuesPointer[j] = (jint)((i + (sum - limit) / distributionPointer[i]) * classWidth + lower);
			if (lnValuesPointer[j] > upper)
				lnValuesPointer[j] = 0x80000000;
			j++;
			if (j == nLn)
				break;
			limit = lnPercentagesPointer[j] * distributionPointer[nClasses + 2];
		}
	}
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Statistics_Distribution
(JNIEnv *env, jclass obj, jobject input, jintArray distribution, jintArray lnValues, jfloatArray lnPercentages, jintArray range, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "running","Z");
	jboolean running = env->GetBooleanField(state, id);

	if (!running)
		return;

	jlong size = env->GetDirectBufferCapacity(input);
	jint length = (jint)(size / 8);

	jlong* inputPointer = (jlong*)env->GetDirectBufferAddress(input);
	jint* distributionPointer = (jint*)env->GetIntArrayElements(distribution, NULL);
	jint* lnValuesPointer = (jint*)env->GetIntArrayElements(lnValues, NULL);
	jfloat* lnPercentagesPointer = (jfloat*)env->GetFloatArrayElements(lnPercentages, NULL);
	jint* rangePointer = (jint*)env->GetIntArrayElements(range, NULL);

	jint lower = rangePointer[0];
	jint upper = rangePointer[1];
	jint classWidth = rangePointer[2];
	jint nClasses = (upper - lower) / classWidth;

	UpdateDistribution(inputPointer, length, distributionPointer, lower, upper, classWidth, nClasses);

	CalculateLnValues(distributionPointer, lnValuesPointer, lnPercentagesPointer, lower, upper, classWidth, nClasses);


	env->ReleaseIntArrayElements(distribution, distributionPointer, 0);
	env->ReleaseIntArrayElements(lnValues, lnValuesPointer, 0);
	env->ReleaseFloatArrayElements(lnPercentages, lnPercentagesPointer, 0);
	env->ReleaseIntArrayElements(range, rangePointer, 0);

}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Statistics_Reset
(JNIEnv *env, jclass obj, jintArray distribution, jintArray range)
{
	jint* distributionPointer = (jint*)env->GetIntArrayElements(distribution, NULL);
	jint* rangePointer = (jint*)env->GetIntArrayElements(range, NULL);

	jint lower = rangePointer[0];
	jint upper = rangePointer[1];
	jint classWidth = rangePointer[2];
	jint nClasses = (upper - lower) / classWidth;

	for (int i = 0; i < nClasses + 3; i++)
		distributionPointer[i] = 0;

	env->ReleaseIntArrayElements(distribution, distributionPointer, 0);
	env->ReleaseIntArrayElements(range, rangePointer, 0);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Statistics_PC
(JNIEnv *env, jclass obj, jobject state)
{
	jclass stateClass = env->GetObjectClass(state);

	jfieldID id = env->GetFieldID(stateClass, "running","Z");
	jboolean running = env->GetBooleanField(state, id);

	running = !running;

	env->SetBooleanField(state, id, running);
}
