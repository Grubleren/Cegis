#include "fftbfp.h"

#ifdef PLATFORM_NT
#include "intrin.h"
inline int CLZ(int r8)
{
	if (r8 == 0)
		return 32;

	_BitScanReverse((unsigned long*)&r8, (unsigned long)r8);

	r8 = 31 - r8;
	return r8;
}

#else

inline int CLZ(int r8)
{
	return __builtin_clz(r8);
}

#endif

DLLEXPORT void FFT(jint M, Complex* Y, Complex* W, jint* blockExponent)
{
	jint N = 1 << M;
	jint N8Y = (N << 3) + (jint)Y;
	jint l1;
	jint l2 = 8;

	jint phi;
	jint dphi =  N << 2;

	int r0;
	int r1;
	int r2;
	int r3;
	int r4;
	int r5;
	int r6;
	int r7;
	int r8;
	int r10;
	int r11;

	*blockExponent = 0;

	r8 = 0;
	for (jint i = 0; i < N; i++)
	{
		r1 = Y[i].re;
		r0 = r1 ^ (r1 >> 31);
		r0 = r0 - (r1 >> 31);
		r8 |= r0;

		r1 = Y[i].im;
		r0 = r1 ^ (r1 >> 31);
		r0 = r0 - (r1 >> 31);
		r8 |= r0;
	}

	for (jint l = 0; l < M; l++)
	{
		r8 = CLZ(r8);

		r0 = 0;

		if (l == 0 || l == 1)
			r0 = r8 - 2;
		else
			r0 = r8 - 3;

		*blockExponent -= r0;

		r1 = r0 ^ (r0 >> 31);
		r1 = r1 - (r0 >> 31);

		if (r0 < 0)
		{

			for (jint i = 0; i < N; i++)
			{
				Y[i].re >>= r1;
				Y[i].im >>= r1;
			}
		}
		else if (r0 > 0)
		{
			for (jint i = 0; i < N; i++)
			{
				Y[i].re <<= r1;
				Y[i].im <<= r1;
			}
		}

		r8 = 0;
		l1 = l2;
		l2 <<= 1;
		phi = (jint)W;

		for (jint j = (jint)Y; j < l1 + (jint)Y; j += 8)
		{
			r4 = (*(Complex*)phi).re;
			r5 = (*(Complex*)phi).im;
			for (r10 = j; r10 < N8Y; r10 += l2)
			{
				r11 = r10 + l1;

				r2 = (*(Complex*)r11).re;
				r3 = (*(Complex*)r11).im;

				r6 = (jint)(((jlong)r2 * r4 - (jlong)r3 * r5) >> 31);
				r7 = (jint)(((jlong)r2 * r5 + (jlong)r3 * r4) >> 31);

				r0 = (*(Complex*)r10).re;
				r1 = (*(Complex*)r10).im;

				r2 = r0 - r6;
				r3 = r1 - r7;
				
				(*(Complex*)r11).re = r2;
				(*(Complex*)r11).im = r3;

				r0 = r0 + r6;
				r1 = r1 + r7;

				(*(Complex*)r10).re = r0;
				(*(Complex*)r10).im = r1;

				r6 = r0 ^ (r0 >> 31);
				r6 = r6 - (r0 >> 31);
				r8 |= r6;

				r6 = r1 ^ (r1 >> 31);
				r6 = r6 - (r1 >> 31);
				r8 |= r6;

				r6 = r2 ^ (r2 >> 31);
				r6 = r6 - (r2 >> 31);
				r8 |= r6;

				r6 = r3 ^ (r3 >> 31);
				r6 = r6 - (r3 >> 31);
				r8 |= r6;
			}
			phi += dphi;
		}
		dphi >>= 1;
	}
} // FFT
