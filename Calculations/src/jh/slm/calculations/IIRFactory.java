package jh.slm.calculations;

public class IIRFactory{
	public Iiir Create(int type){
		switch (type){
		case 1:
			return new IIRFloat();
		case 2:
			return new IIRInt();
		case 3:
			return new IIRCInt();
		}
		return null;
	}

	public class IIRFloat implements Iiir{
		public void Initialize(BiQuad[] biQuads){
			this.biQuads = biQuads;
			delayLines = new double[biQuads.length][];
			for (int i = 0; i < delayLines.length; i++){
				delayLines[i] = new double[2];
			}
		}

		public void Free(){
		}

		public void Iir(Object input, Object output){
			double[] dataIn = (double[])input;
			double[] dataOut = (double[])output;
			double[] data = dataIn;
			int Length = dataIn.length;

			double w;
			double w_1;
			double w_2;

			for (int j = 0; j < biQuads.length; j++){
				w_1 = delayLines[j][0];
				w_2 = delayLines[j][1];

				for (int i = 0; i < Length; i++){
					w = data[i] - biQuads[j].a1 * w_1 - biQuads[j].a2 * w_2;
					dataOut[i] = biQuads[j].b0 * w + biQuads[j].b1 * w_1 + biQuads[j].b2 * w_2;

					w_2 = w_1;
					w_1 = w;
				}

				delayLines[j][0] = w_1;
				delayLines[j][1] = w_2;
				data = dataOut;
			}
		}
		BiQuad[] biQuads;
		double[][] delayLines;
	}

	public class IIRInt implements Iiir{
		public void Initialize(BiQuad[] biQuads){
			int noBiQuads = biQuads.length;
			biQuadsInt = new BiQuadInt[noBiQuads];
			delayLines = new int[noBiQuads][];
			int noBits = 30;
			for (int i = 0; i < noBiQuads; i++){
				BiQuad biQuad = biQuads[i];
				biQuadsInt[i] = new BiQuadInt();
				delayLines[i] = new int[2];

				biQuadsInt[i].a1 = (int)(biQuad.a1 * (1 << noBits)); 
				biQuadsInt[i].a2 = (int)(biQuad.a2 * (1 << noBits)); 
				biQuadsInt[i].b0 = (int)(biQuad.b0 * (1 << noBits)); 
				biQuadsInt[i].b1 = (int)(biQuad.b1 * (1 << noBits));
				biQuadsInt[i].b2 = (int)(biQuad.b2 * (1 << noBits));

				delayLines[i][0] = 0;
				delayLines[i][1] = 0;
			}
		}

		public void Free(){
		}

		public void Iir(Object input, Object output){
			int[] dataIn = (int[])input;
			int[] dataOut = (int[])output;
			int[] data = dataIn;
			int Length = dataIn.length;

			int w;
			int w_1;
			int w_2;
			int shift = 32;

			for (int j = 0; j < biQuadsInt.length; j++){

				w_1 = delayLines[j][0];
				w_2 = delayLines[j][1];

				for (int i = 0; i < Length; i++){
					w = data[i] - (int)((biQuadsInt[j].a1 * (long)w_1) >> shift) - (int)((biQuadsInt[j].a2 * (long)w_2) >> shift);
					w <<= 2;
					dataOut[i] = (int)((biQuadsInt[j].b0 * (long)w) >> shift) + (int)((biQuadsInt[j].b1 * (long)w_1) >> shift) + (int)((biQuadsInt[j].b2 * (long)w_2) >> shift);

					w_2 = w_1;
					w_1 = w;
				}

				delayLines[j][0] = w_1;
				delayLines[j][1] = w_2;
				data = dataOut;
			}
		}

		BiQuadInt[] biQuadsInt;
		int[][] delayLines;

		public class BiQuadInt {
			public int a1;
			public int a2;
			public int b0;
			public int b1;
			public int b2;
		}
	}

	public class IIRCInt implements Iiir{
		public void Initialize(BiQuad[] biQuads){
			int noBiQuads = biQuads.length;
			int noBits = 30;
			int size = 1 + (5 + 2) * noBiQuads;
			int[] stateInt = new int[size];

			stateInt[0] = noBiQuads;
			for (int i = 0; i < noBiQuads; i++){
				BiQuad biQuad = biQuads[i];

				int index = 1 + 5 * i;
				stateInt[index]     = (int)(biQuad.a1 * (1 << noBits)); 
				stateInt[index + 1] = (int)(biQuad.a2 * (1 << noBits)); 
				stateInt[index + 2] = (int)(biQuad.b0 * (1 << noBits)); 
				stateInt[index + 3] = (int)(biQuad.b1 * (1 << noBits));
				stateInt[index + 4] = (int)(biQuad.b2 * (1 << noBits));

				index = 1 + 5 * noBiQuads + 2 * i;
				stateInt[index]     = 0;
				stateInt[index + 1] = 0;
			}

			state = Iirfilter.BiQuads(stateInt);
		}

		public void Free(){
			Allocator.Free(state);
		}

		public void Iir(Object input, Object output){
			Iirfilter.Filter(input, output, state);
		}

		Object state;
	}
}

interface Iiir{
	void Initialize(BiQuad[] biQuads);
	void Free();
	void Iir(Object input, Object output);
}
