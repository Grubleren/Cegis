package jh.slm.calculations;

import java.util.Date;

public class Random
{
	public Random()
	{
		Date date = new Date();
		r = (int)date.getTime();
	} // Random

	public Random(int seed)
	{
		r = seed;
	} // Random

	private void rnd()
	{
		r = a * r + b;
	} // rnd

	public double RandomDouble()
	{
		rnd();
		return r * c;
	} // RandomDouble

	public double RandomGauss()
	{
		double sum = 0;
		for (int i = 0; i < 12; i++)
		{
			rnd();
			sum += r;
		}
		return (sum - 6) * c;
	}

	private int a = 0x55555555;
	private int b = 0x55555555;
	private double c = 0.25 / 0x40000000;
	private int r;
} // Random
