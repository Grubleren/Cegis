package jh.slm.calculations;

public class AWeighting {

	public Object Input;
	public Object Output;
	private BiQuad[] biQuads;
	Iiir iir;

	public AWeighting(int length){
		Output = new Object[1];
		Output = Allocator.Allocate(length);
		iir = new IIRFactory().Create(3);
		biQuads = new BiQuad[3];
		for (int i =0; i< biQuads.length; i++){
			biQuads[i] = new BiQuad();
		}

        biQuads[0].a1 = -1.34730722798;
        biQuads[0].a2 = 0.349057529796;
        biQuads[0].b0 = 0.965250965250;
        biQuads[0].b1 = -1.34730163086;
        biQuads[0].b2 = 0.382050665614;

        biQuads[1].a1 = -1.89387049481;
        biQuads[1].a2 = 0.895159769170;
        biQuads[1].b0 = 0.946969696969;
        biQuads[1].b1 = -1.89393939393;
        biQuads[1].b2 = 0.946969696969;

        biQuads[2].a1 = -1.34730722798;
        biQuads[2].a2 = 0.349057529796;
        biQuads[2].b0 = 0.646665428100;
        biQuads[2].b1 = -0.38362237137;
        biQuads[2].b2 = -0.26304305672;

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
