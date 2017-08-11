package jh.slm.calculations;

public class ButterworthFilter{

	private Object Input;
	public Object Output;
	private BiQuad[] biQuads;
	private Iiir iir;
	
	public ButterworthFilter(FilterType filterType, int order, double f0, double fs) throws Exception{
		double omega0 = Math.tan(Math.PI * f0 / fs);

		switch(filterType){
		case LP:
			CalculateFilterCoefficientsLP(order, omega0);
			break;
		case HP:
			CalculateFilterCoefficientsHP(order, omega0);
			break;
		case BP:
			break;
		default:
			throw new Exception("Filter type not supported");
		}
	}

	public ButterworthFilter(FilterType filterType, int order, double f0, double f1, double fs) throws Exception{
		double omega0 = Math.tan(Math.PI * f0 / fs);
		double omega1 = Math.tan(Math.PI * f1 / fs);

		switch(filterType){
		case LP:
			break;
		case HP:
			break;
		case BP:
			CalculateFilterCoefficientsBP(order, omega0, omega1);
			break;
			default:
				throw new Exception("Filter type not supported");
		}
	}

	public void Initialize(int type, int length){
		switch (type)
		{
		case 1:
			Output = new double[length];
			break;
		case 2:
			Output = new int[length];
			break;
		case 3:
			Output = Allocator.Allocate(length);
		}

		iir = new IIRFactory().Create(type);
		iir.Initialize(biQuads);
	}

	public void Connect(Object input) {
		Input = input;
	}
	
	public void Compute(){
		iir.Iir(Input, Output);
	}

	public void Free(){

		iir.Free();
		Allocator.Free(Output);
	}

	private void CalculateFilterCoefficientsLP(int order, double omega0){
		Complex[] poles = GetComplexPolesLP(order, omega0);

		BilinearTransform(poles);

		biQuads = new BiQuad[(order + 1) / 2];

		for (int i = 0; i < poles.length; i++)
			biQuads[i] = new BiQuad(2, 1, -2 * poles[i].re, Complex.AbsSqr(poles[i]));

		if (order % 2 != 0){
			double pole = BilinearTransform(-omega0);
			biQuads[poles.length] = new BiQuad(1, 0, -pole, 0);
		}

		NormalizeBiQuads(biQuads, 0);
	}

	private void CalculateFilterCoefficientsHP(int order, double omega0){
		Complex[] poles = GetComplexPolesHP(order, omega0);

		BilinearTransform(poles);

		biQuads = new BiQuad[(order + 1) / 2];

		for (int i = 0; i < poles.length; i++)
			biQuads[i] = new BiQuad(-2, 1, -2 * poles[i].re, Complex.AbsSqr(poles[i]));

		if (order % 2 != 0){
			double pole = BilinearTransform(-omega0);
			biQuads[poles.length] = new BiQuad(-1, 0, -pole, 0);
		}

		NormalizeBiQuads(biQuads, Math.PI);
	}

	private void CalculateFilterCoefficientsBP(int order, double omega0, double omega1){
		Complex[] poles = GetComplexPolesBP(order, omega0, omega1);

		BilinearTransform(poles);

		biQuads = new BiQuad[order];

		for (int i = 0; i < poles.length; i++)
			biQuads[i] = new BiQuad(0, -1, -2 * poles[i].re, Complex.AbsSqr(poles[i]));

		if (order % 2 != 0){
			double omega2 = Math.sqrt(omega0 * omega1);
			double p = -(omega1 - omega0) / omega2;
			double u;
			if (4 - Math.pow(p, 2) > 0){
				u = Math.sqrt(4 - Math.pow(p, 2));
				Complex pole = Complex.Mul(0.5, Complex.Mul(omega2, new Complex(p, u)));
				pole = BilinearTransform(pole);
				biQuads[poles.length] = new BiQuad(0, -1, -2 * pole.re, Complex.AbsSqr(pole));
			}
			else{
				u = Math.sqrt(-4 + Math.pow(p, 2));
				Complex pole1 = Complex.Mul(0.5, Complex.Mul(omega2, new Complex(p + u, 0)));
				Complex pole2 = Complex.Mul(0.5, Complex.Mul(omega2, new Complex(p - u, 0)));
				pole1 = BilinearTransform(pole1);
				pole2 = BilinearTransform(pole2);
				biQuads[poles.length] = new BiQuad(0, -1, -(pole1.re + pole2.re), pole1.re * pole2.re);
			}
		}

		double omegaRef = 2 * Math.atan(Math.sqrt(omega0 * omega1));

		NormalizeBiQuads(biQuads, omegaRef);
	}

	private Complex[] GetComplexPolesLP(int order, double omega0){
		Complex[] poles = new Complex[order / 2];

		for (int i = 0; i < poles.length; i++){
			double angle = (double)(2 * i + 1) / order * Math.PI / 2;
			poles[i] = Complex.Mul(omega0, new Complex(-Math.sin(angle), Math.cos(angle)));
		}

		return poles;
	}

	private Complex[] GetComplexPolesHP(int order, double omega0){
		Complex[] poles = new Complex[order / 2];

		for (int i = 0; i < poles.length; i++){
			double angle = (double)(2 * i + 1) / order * Math.PI / 2;
			poles[i] = Complex.Div(omega0, new Complex(-Math.sin(angle), Math.cos(angle)));
		}

		return poles;
	}

	private Complex[] GetComplexPolesBP(int order, double omega0, double omega1){
		Complex[] poles = new Complex[2 *(order / 2)];

		double omega2 = Math.sqrt(omega0 * omega1);

		for (int i = 0; i < poles.length / 2; i++){
			double angle = (double)(2 * i + 1) / order * Math.PI / 2;
			Complex p = Complex.Mul((omega1 - omega0) / omega2, new Complex(-Math.sin(angle), Math.cos(angle)));

			double q = 0.5 * (4 - Math.pow(p.re, 2) + Math.pow(p.im, 2));
			double u = Math.sqrt(q + Math.sqrt(Math.pow(q, 2) + Math.pow(p.re * p.im, 2)));
			double v = p.re * p.im / u;

			poles[i] = Complex.Mul(0.5 * omega2, new Complex(p.re + v, p.im + u));
			poles[i + poles.length / 2] = Complex.Mul(0.5 * omega2, new Complex(p.re - v, -p.im + u));
		}

		return poles;
	}

	private void BilinearTransform(Complex[] poles){
		for (int i = 0; i < poles.length; i++)
		{
			poles[i] = BilinearTransform(poles[i]);
		}
	}

	private Complex BilinearTransform(Complex pole){
		return Complex.Div(Complex.Add(1, pole), Complex.Sub(1, pole));
	}

	private double BilinearTransform(double pole){
		return (1 + pole) / (1 - pole);
	}

	private void NormalizeBiQuads(BiQuad[] biQuads, double omegaRef){
		Complex z_1 = new Complex(Math.cos(omegaRef), -Math.sin(omegaRef));
		double scale;

		for (int i = 0; i < biQuads.length; i++){
			Complex numerator = Complex.Add(biQuads[i].b0, Complex.Add(Complex.Mul(biQuads[i].b1, z_1), Complex.Mul(Complex.Mul(biQuads[i].b2, z_1), z_1)));
			Complex denominator = Complex.Add(biQuads[i].a0, Complex.Add(Complex.Mul(biQuads[i].a1, z_1), Complex.Mul(Complex.Mul(biQuads[i].a2, z_1), z_1)));
			scale = 1 / Math.sqrt(Complex.AbsSqr(Complex.Div(numerator, denominator)));
			biQuads[i].b0 *= scale;
			biQuads[i].b1 *= scale;
			biQuads[i].b2 *= scale;
		}
	}

	public double FrequencyResponse(double f, double fs){
		double omega = 2 * Math.PI * f / fs;
		Complex z_1 = new Complex(Math.cos(omega), -Math.sin(omega));
		double frequencyResponse = 1;

		for (int i = 0; i < biQuads.length; i++){
			Complex numerator = Complex.Add(biQuads[i].b0, Complex.Add(Complex.Mul(biQuads[i].b1, z_1), Complex.Mul(Complex.Mul(biQuads[i].b2, z_1), z_1)));
			Complex denominator = Complex.Add(biQuads[i].a0, Complex.Add(Complex.Mul(biQuads[i].a1, z_1), Complex.Mul(Complex.Mul(biQuads[i].a2, z_1), z_1)));

			frequencyResponse *= Math.sqrt(Complex.AbsSqr(Complex.Div(numerator, denominator)));
		}

		return frequencyResponse;
	}
}
