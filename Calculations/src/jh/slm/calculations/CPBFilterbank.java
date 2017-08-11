package jh.slm.calculations;

public class CPBFilterbank {

	public static double[] octaves = new double[] { 1, 2, 4, 8, 16, 31.5, 63, 125, 250, 500, 1000, 2000, 4000, 8000, 16000 };
	public static double[] thirdOctaves = new double[] { 1, 1.2, 1.6, 2, 2.5, 3.1, 4, 5, 6.3, 8, 10, 12.5, 16, 20, 25, 31.5, 40, 50, 63, 80, 100, 125, 160, 200, 250, 315, 400, 500, 630, 800, 1000, 1250, 1600, 2000, 2500, 3150, 4000, 5000, 6300, 8000, 10000, 12500, 16000, 20000 };

	public OctaveFilterType filterType;

	public int lowIndex;
	public int highIndex;
	public int noFilters;

	public ButterworthFilter[] filters;
	public Object[] Output;
	
	public CPBFilterbank(OctaveFilterType filterType, double lowFc, double highFc, int fs)
	{
		this.filterType = filterType;
		int bw;

		switch (filterType)
		{
		case Octave:
			bw = 1;
			break;
		case ThirdOctave:
			bw = 3;
			break;
		case TwelfthOctave:
			bw = 12;
			break;
		case TwentyfourthOctave:
			bw = 24;
			break;
		default: bw = 0;
		break;
		}
		lowIndex = (int)Math.round((Math.log10(lowFc / 1000) / Math.log10(2) + 10) * bw);
		highIndex = (int)Math.round((Math.log10(highFc / 1000) / Math.log10(2) + 10) * bw);
		noFilters = highIndex - lowIndex + 1;
		filters = new ButterworthFilter[noFilters];
		Output = new Object[noFilters];
		double bandwidthFactor = Math.pow(2, 0.5 / bw);
		double rel = (bandwidthFactor - 1 / bandwidthFactor) / (Math.PI / 3) / 2;
		bandwidthFactor = rel + Math.sqrt(Math.pow(rel, 2) + 1);

		for (int i = 0; i < noFilters; i++)
		{
			double fCenter = 1000 * Math.pow(Math.pow(2, 1.0 / bw), i + lowIndex - 10 * bw);
			double fLower = fCenter / bandwidthFactor;
			double fUpper = fCenter * bandwidthFactor;
			try{
				filters[i] = new ButterworthFilter(FilterType.BP, 3, fLower, fUpper, fs);
			}
			catch(Exception e){
			}
		}
	}

	public void Initialize(int type, int length)
	{
		for (int i = 0; i < filters.length; i++){
			filters[i].Initialize(type, length);
			Output[i] = filters[i].Output;
		}
	}

	public void Connect(Object input) {
		for (int i = 0; i < filters.length; i++){
			filters[i].Connect(input);
		}
	}

	public void Compute()
	{
		for (int i = 0; i < filters.length; i++)
			filters[i].Compute();
	}

	public void Free()
	{
		for (int i = 0; i < filters.length; i++)
			filters[i].Free();
	}

	public double CenterFrequency(int index)
	{
		switch (filterType)
		{
		case Octave:
			return octaves[index + lowIndex];
		case ThirdOctave:
			return thirdOctaves[index + lowIndex];
		case TwelfthOctave:
			return Math.pow(2, (index + lowIndex) / 12.0);
		case TwentyfourthOctave:
			return Math.pow(2, (index + lowIndex) / 24.0);
		default: return 0;
		}
	}
}
