package jh.slm.calculations;

public class CWeighting {

	public Object Input;
	public Object Output;
	private BiQuad[] biQuads;
	Iiir iir;
	public CWeighting(int length){
		Output = Allocator.Allocate(length);
		iir = new IIRFactory().Create(3);
		biQuads = new BiQuad[3];
		for (int i =0; i< biQuads.length; i++){
			biQuads[i] = new BiQuad();
		}
		
        biQuads[0].a1 = -0.9973 * 2;
        biQuads[0].a2 =  0.99460729;
        biQuads[0].b0 =  1;
        biQuads[0].b1 = -2;
        biQuads[0].b2 =  1;

        biQuads[1].a1 = -0.33 * 2;
        biQuads[1].a2 =  0.1089;
        biQuads[1].b0 =  0.157130322;
        biQuads[1].b1 =  0.474533711;
        biQuads[1].b2 = -0.182625174;

        biQuads[2].a1 =  0;
        biQuads[2].a2 =  0;
        biQuads[2].b0 =  0.891798115;
        biQuads[2].b1 =  0.134875351;
        biQuads[2].b2 = -0.02647733;

		iir.Initialize(biQuads);
	}
	
	public void Free(){
		Allocator.Free(Output);
	}
	
	public void Connect(Object input) {
		Input = input;
	}

	public void Compute() {
		iir.Iir(Input, Output);
	}
}
