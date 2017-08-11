package jh.slm.calculations;

public class Complex {
	public double re;
	public double im;

	public Complex(double re, double im)
	{
		this.re = re;
		this.im = im;
	}

	public static Complex Conjugate(Complex c)
	{
		return new Complex(c.re, -c.im);
	}

	public static double AbsSqr(Complex c)
	{
		return c.re * c.re + c.im * c.im;
	}

	public static Complex Add(Complex c1, Complex c2)
	{
		return new Complex(c1.re + c2.re, c1.im + c2.im);
	}

	public static Complex Add(double d, Complex c)
	{
		return Complex.Add(new Complex(d, 0), c);
	}

	public static Complex Add(Complex c, double d)
	{
		return Complex.Add(d, c);
	}

	public static Complex Sub(Complex c1, Complex c2)
	{
		return new Complex(c1.re - c2.re, c1.im - c2.im);
	}

	public static Complex Sub(double d, Complex c)
	{
		return Complex.Sub(new Complex(d, 0), c);
	}

	public static Complex Sub(Complex c, double d)
	{
		return Complex.Sub(c, new Complex(d, 0));
	}

	public static Complex Chs(Complex c)
	{
		return new Complex(-c.re, -c.im);
	}

	public static Complex Mul(Complex c1, Complex c2)
	{
		return new Complex(c1.re * c2.re - c1.im * c2.im, c1.re * c2.im + c1.im * c2.re);
	}

	public static Complex Mul(double d, Complex c)
	{
		return new Complex(c.re * d, c.im * d);
	}

	public static Complex Mul(Complex c, double d)
	{
		return new Complex(c.re * d, c.im * d);
	}

	public static Complex Div(Complex c1, Complex c2)
	{
		Complex n = Complex.Mul(c1, Complex.Conjugate(c2));

		return Complex.Div(n, c2.re * c2.re + c2.im * c2.im);
	}

	public static Complex Div(double d, Complex c)
	{
		return Complex.Div(Complex.Mul(Complex.Conjugate(c), d), (c.re * c.re + c.im * c.im));
	}

	public static Complex Div(Complex c, double d)
	{
		return new Complex(c.re / d, c.im / d);
	}
}
