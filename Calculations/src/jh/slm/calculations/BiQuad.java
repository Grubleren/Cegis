package jh.slm.calculations;

public class BiQuad{
	public BiQuad(double b1, double b2, double a1, double a2)
	{
		this.b0 = 1;
		this.b1 = b1;
		this.b2 = b2;
		this.a0 = 1;
		this.a1 = a1;
		this.a2 = a2;
	}

	public BiQuad() {
		// TODO Auto-generated constructor stub
	}

	public double b0;
	public double b1;
	public double b2;
	public double a0;
	public double a1;
	public double a2;
}

