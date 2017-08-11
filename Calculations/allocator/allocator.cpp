#include "allocator.h"
#include <stdlib.h>

DLLEXPORT jobject DLLCALL Java_jh_slm_calculations_Allocator_Allocate
(JNIEnv *env, jclass obj, jint length){

	int size = 4 * length;

	int* dataPointer = (int*)malloc(size);
	return env->NewDirectByteBuffer(dataPointer, size);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_Free
(JNIEnv *env, jclass obj, jobject data){

	int* dat = (int*)env->GetDirectBufferAddress(data);

	free(dat);
}

DLLEXPORT jintArray DLLCALL Java_jh_slm_calculations_Allocator_DeRefInt
(JNIEnv *env, jclass obj, jobject data){

	jlong size = env->GetDirectBufferCapacity(data);
	int length = (int)(size / 4);

	jint* dataPointer = (jint*)env->GetDirectBufferAddress(data);
	jintArray newData = env->NewIntArray(length);
	jint* newDataPointer = env->GetIntArrayElements(newData, NULL);

	for (int i = 0; i < length; i++){
		newDataPointer[i] = dataPointer[i];
	}
	env->ReleaseIntArrayElements(newData, newDataPointer, 0);

	return newData;
}

DLLEXPORT jlongArray DLLCALL Java_jh_slm_calculations_Allocator_DeRefLong
(JNIEnv *env, jclass obj, jobject data){

	jlong size = env->GetDirectBufferCapacity(data);
	int length = (int)(size / 8);

	jlong* dataPointer = (jlong*)env->GetDirectBufferAddress(data);
	jlongArray newData = env->NewLongArray(length);
	jlong* newDataPointer = env->GetLongArrayElements(newData, NULL);

	for (int i = 0; i < length; i++){
		newDataPointer[i] = dataPointer[i];
	}
	env->ReleaseLongArrayElements(newData, newDataPointer, 0);

	return newData;
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_Copy
(JNIEnv *env, jclass obj, jintArray dataIn, jobject dataOut){

	jlong size = env->GetDirectBufferCapacity(dataOut);
	int length = (int)(size / 4);

	jint* dataOutPointer = (jint*)env->GetDirectBufferAddress(dataOut);
	jint* dataInPointer = env->GetIntArrayElements(dataIn, NULL);

	for (int i = 0; i < length; i++){
		dataOutPointer[i] = dataInPointer[i];
	}

	env->ReleaseIntArrayElements(dataIn, dataInPointer, 0);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_CopyBytes
(JNIEnv *env, jclass obj, jbyteArray dataIn, jobject dataOut){

	jlong size = env->GetDirectBufferCapacity(dataOut);
	int length = (int)(size / 4);

	jint* dataOutPointer = (jint*)env->GetDirectBufferAddress(dataOut);
	jshort* dataInPointer = (jshort*)env->GetByteArrayElements(dataIn, NULL);

	for (int i = 0; i < length; i++){
		dataOutPointer[i] = dataInPointer[i];
	}

	env->ReleaseByteArrayElements(dataIn, (jbyte*)dataInPointer, 0);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_Copy16To32
(JNIEnv *env, jclass obj, jbyteArray dataIn, jobject dataOut){

	jlong size = env->GetDirectBufferCapacity(dataOut);
	int length = (int)(size / 4);

	jshort* dataInPointer = (jshort*)env->GetByteArrayElements(dataIn, NULL);

	jint* dataOutPointer = (jint*)env->GetDirectBufferAddress(dataOut);

	for (int i = 0; i < length; i++){
		dataOutPointer[i] = (jint)dataInPointer[i] << 8;
	}

	env->ReleaseByteArrayElements(dataIn, (jbyte*)dataInPointer, 0);
}

DLLEXPORT void DLLCALL Java_jh_slm_calculations_Allocator_Copy24To32
(JNIEnv *env, jclass obj, jbyteArray dataIn, jobject dataOut){

	jlong size = env->GetDirectBufferCapacity(dataOut);
	int length = (int)(size / 4);

	unsigned char* dataInPointer = (unsigned char*)env->GetByteArrayElements(dataIn, NULL);

	jint* dataOutPointer = (jint*)env->GetDirectBufferAddress(dataOut);

	for (int i = 0; i < length; i++){
		dataOutPointer[i] = (dataInPointer[3 * i] << 8 | dataInPointer[3 * i + 1] << 16 | dataInPointer[3 * i + 2] << 24) >> 8;
	}

	env->ReleaseByteArrayElements(dataIn, (jbyte*)dataInPointer, 0);
}

