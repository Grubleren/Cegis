#include "fft-support.h"
#include "math.h"

DLLEXPORT void TwidleFactors(jint N, Complex* W)
{
	const double pi = 4.0 * atan(1.0);

	for (jint i = 0; i < N / 2; i++)
	{
		W[i].re = (jint)floor(0x7fffffff * cos(2 * pi / N * i) + 0.5);
		W[i].im = -(jint)floor(0x7fffffff * sin(2 * pi / N * i) + 0.5);
	}
} // TwidleFactors

DLLEXPORT unsigned int BitReverse(jint M, unsigned int num)
{
	unsigned int result = 0;

	for (jint i = 0; i < M; i++)
	{
		result = (result << 1) + (num & 1);
		num = num >> 1;
	}
	return result;
} // BitReverse

DLLEXPORT void Hanning(jint M, jint N, jint* X, Complex* Y, Complex* W)
{
	for (jint i = 0; i < N / 2; i++)
	{
		Y[BitReverse(M, i)].re = ((jlong)X[i] * (0x40000000 - (W[i].re >> 1))) >> 31;
		Y[i].im = 0;
		Y[N - 1 - i].im = 0;
	}

	for (jint i = 0; i < N / 2 - 1; i++)
	{
		Y[BitReverse(M, N - 1 - i)].re = ((jlong)X[N - 1 - i] * (0x40000000 - (W[i].re >> 1))) >> 31;
	}

	Y[BitReverse(M, N / 2)].re = X[N / 2];
} // Hanning

DLLEXPORT void Averaging(jint M, jint span, jint numberOfAverages, jint averagingCount, jint blockExponent, Complex* instSpectrum, jlong* average)
{
	if (numberOfAverages < 1)
		numberOfAverages = 1;
	float beta;
	if (averagingCount < numberOfAverages)
		beta = (float)(1.0 / (averagingCount + 1));
	else
		beta = (float)(2.0 / (numberOfAverages + 1));
	jint b = (jint)floor(0x10000 * beta + 0.5);
	jint a = 0x10000 - b;

	//	printf("Single %d      %d     %d\n", blockExponent, instSpectrum[10].re, instSpectrum[10].im);

	jint averageBlockExponent = (jint)average[span];
	jint shift1 = 0;
	jint shift2 = 0;

	if (averageBlockExponent >= 2 * blockExponent)
	{
		shift1 = 16;
		shift2 = averageBlockExponent - 2 * blockExponent + 18;
	}
	else if (averageBlockExponent < 2 * blockExponent)
	{
		shift1 = 2 * blockExponent - averageBlockExponent + 16;
		shift2 = 18;
		averageBlockExponent = 2 * blockExponent;
	}

	for (jint i = 0; i < span; i++)
	{
		jlong power = ((jlong)instSpectrum[i].re * instSpectrum[i].re + (jlong)instSpectrum[i].im * instSpectrum[i].im);
		if (i == 0)
			power >>= 1;

		average[i] >>= shift1;
		power >>= shift2;
		jlong prod1 = a * average[i];
		jlong prod2 = b * power;

		average[i] = prod1 + prod2;
	}

	average[span] = averageBlockExponent;

} //Averaging

DLLEXPORT void CrossAveraging(jint M, jint span, jint numberOfAverages, jint averagingCount, jint blockExponent1, Complex* instSpectrum1, jint blockExponent2, Complex* instSpectrum2, ComplexLong* average)
{
	//	printf("Cross averging %d   %d\n", span, averagingCount);

	if (numberOfAverages < 1)
		numberOfAverages = 1;
	float beta;
	if (averagingCount < numberOfAverages)
		beta = (float)(1.0 / (averagingCount + 1));
	else
		beta = (float)(2.0 / (numberOfAverages + 1));
	jint b = (jint)floor(0x40000 * beta + 0.5);
	jint a = 7;//0x40000 - b;
	jint shift = -blockExponent1 - blockExponent2 - 2 * M + 10;

	//	printf("block exponents %d   %d   %d    %d   %d    %d   %d\n", shift, blockExponent1, blockExponent2,instSpectrum1[10].re, instSpectrum1[10].im,instSpectrum2[10].re, instSpectrum2[10].im);

	for (jint i = 0; i < span; i++)
	{
		jlong powerRe = (jlong)instSpectrum1[i].re * instSpectrum2[i].re + (jlong)instSpectrum1[i].im * instSpectrum2[i].im;
		jlong powerIm = -(jlong)instSpectrum1[i].re * instSpectrum2[i].im + (jlong)instSpectrum1[i].im * instSpectrum2[i].re;

		if (i == 0)
		{
			powerRe >>= shift;
			powerIm >>= shift;
		}
		else
		{
			powerRe >>= shift - 1;
			powerIm >>= shift - 1;
		}


		average[i].re = (a * average[i].re + powerRe) >> 3;
		average[i].im = (a * average[i].im + powerIm) >> 3;
	}
} //Averaging

DLLEXPORT void Reset(jint span, jlong* average, jint delayLineLength, jint* delayLine)
{
	for (int i = 0;i < delayLineLength; i++)
		delayLine[i] = 0;

	for (jint i = 0; i < span; i++)
		average[i] = 0;

	average[span] = 0x80000000;

} //Reset

DLLEXPORT void ResetCross(jint span, ComplexLong* crossSpectrum, jint delayLineLength, jint* delayLine)
{
	//printf("Reset cross %d\n", span);

	for (int i = 0;i < delayLineLength; i++)
		delayLine[i] = 0;

	for (jint i = 0; i < span; i++)
	{
		crossSpectrum[i].re = 0;
		crossSpectrum[i].im = 0;
	}

	((jlong*)crossSpectrum)[2 * span] = 0x80000000;
} //Reset

DLLEXPORT void AveragingIntensity(jint M, jint span, jint numberOfAverages, jint averagingCount, jint blockExponent1, jint blockExponent2, Complex* instSpectrum1, Complex* instSpectrum2, jlong* intensity)
{
	if (numberOfAverages < 1)
		numberOfAverages = 1;
	float beta;
	if (averagingCount < numberOfAverages)
		beta = (float)(1.0 / (averagingCount + 1));
	else
		beta = (float)(2.0 / (numberOfAverages + 1));
	jint b = (jint)floor(0x10000 * beta + 0.5);
	jint a = 0x10000 - b;

	//	printf("Single %d      %d     %d\n", blockExponent, instSpectrum[10].re, instSpectrum[10].im);

	jint averageBlockExponent = (jint)intensity[2*span];
	jint shift1 = 0;
	jint shift2 = 0;

	if (averageBlockExponent >= blockExponent1 + blockExponent2)
	{
		shift1 = 16;
		shift2 = averageBlockExponent - blockExponent1 - blockExponent2 + 18;
	}
	else if (averageBlockExponent < blockExponent1 + blockExponent2)
	{
		shift1 = blockExponent1 + blockExponent2 - averageBlockExponent + 16;
		shift2 = 18;
		averageBlockExponent = blockExponent1 + blockExponent2;
	}

	for (jint i = 0; i < span; i++)
	{
		jlong re = ((jlong)instSpectrum1[i].re * instSpectrum2[i].re + (jlong)instSpectrum1[i].im * instSpectrum2[i].im);
		jlong im = ((jlong)instSpectrum1[i].re * instSpectrum2[i].im - (jlong)instSpectrum1[i].im * instSpectrum2[i].re);
		if (i == 0)
		{
			re >>= 1;
			im >>= 1;
		}
		intensity[2*i] >>= shift1;
		intensity[2*i+1] >>= shift1;
		re >>= shift2;
		im >>= shift2;
		jlong prod1re = a * intensity[2*i];
		jlong prod1im = a * intensity[2*i+1];
		jlong prod2re = b * re;
		jlong prod2im = b * im;

		intensity[2*i] = prod1re + prod2re;
		intensity[2*i+1] = prod1im + prod2im;
	}

	intensity[2*span] = averageBlockExponent;

} //Averaging

DLLEXPORT void ResetIntensity(jint span, jlong* intensity)
{
	for (jint i = 0; i < 2*span; i++)
	{
		intensity[i] = 0;
	}

	intensity[2*span] = 0x80000000;

} //Reset

