
#ifndef _dll_export
#define _dll_export

#ifdef PLATFORM_NT
#define DLLEXPORT __declspec(dllexport)
#define DLLCALL _stdcall
#else
#define DLLEXPORT JNIEXPORT
#define DLLCALL JNICALL
#endif
#endif
