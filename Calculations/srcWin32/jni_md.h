
#ifndef _JAVASOFT_JNI_MD_H_
#define _JAVASOFT_JNI_MD_H_

#ifdef PLATFORM_NT
#define JNIEXPORT __declspec(dllexport)
#define JNIIMPORT __declspec(dllimport)
#define JNICALL __stdcall


#else
#define JNIEXPORT
#define JNIIMPORT
#define JNICALL
#endif

typedef long long jlong;
typedef long jint;
typedef signed char jbyte;

#endif /* !_JAVASOFT_JNI_MD_H_ */
