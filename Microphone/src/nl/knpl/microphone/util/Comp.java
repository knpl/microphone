package nl.knpl.microphone.util;

public class Comp {

	private double real, imag;
	
	public Comp(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}
	
	public Comp(double real) {
		this(real, 0);
	}
	
	public Comp() {
		this(0, 0);
	}
	
	public Comp(Comp that) {
		this(that.real, that.imag);
	}
	
	public static Comp fromPolar(double r, double the) {
		return new Comp(r * Math.cos(the), r * Math.sin(the));
	}
	
	public double real() {
		return real;
	}
	
	public double imag() {
		return imag;
	}
	
	public double mod() {
		return Math.sqrt(real*real + imag*imag);
	}
	
	public double arg() {
		return Math.atan2(imag, real);
	}
	
	public Comp add(Comp z) {
		real += z.real;
		imag += z.imag;
		return this;
	}
	
	public Comp add(double x) {
		real += x;
		return this;
	}
	
	public Comp sub(Comp z) {
		real -= z.real;
		imag -= z.imag;
		return this;
	}
	
	public Comp sub(double x) {
		real -= x;
		return this;
	}
	
	public Comp neg() {
		real = -real;
		imag = -imag;
		return this;
	}
	
	public Comp mul(Comp z) {
		double temp;
		temp = real*z.real - imag*z.imag;
		imag = real*z.imag + imag*z.real;
		real = temp;
		return this;
	}
	
	public Comp mul(double x) {
		real *= x;
		imag *= x;
		return this;
	}
	
	public Comp div(Comp z) {
		double rrinv, temp;
		rrinv = 1 / (z.real*z.real + z.imag*z.imag);
		temp = (real*z.real + imag*z.imag) * rrinv;
		imag = (imag*z.real - real*z.imag) * rrinv;
		real = temp;
		return this;
	}
	
	public Comp div(double x) {
		real /= x;
		imag /= x;
		return this;
	}
	
	public Comp inv() {
		double rrinv;
		rrinv = 1 / (real*real + imag*imag);
		real *=  rrinv;
		imag *= -rrinv;
		return this;
	}
	
	public Comp exp() {
		double exp;
		exp = Math.exp(real);
		real = exp * Math.cos(imag);
		imag = exp * Math.sin(imag);
		return this;
	}
	
	public Comp log() {
		double temp;
		temp = 0.5 * Math.log(real * real + imag * imag);
		imag = arg();
		real = temp;
		return this;
	}
	
	public String toString() {
		return String.format("%7.3f + %7.3f*i", real, imag);
	}
	
	public static Comp add(Comp w, Comp z) {
		return new Comp(w.real + z.real, w.imag + z.imag); 
	}
	
	public static Comp sub(Comp w, Comp z) {
		return new Comp(w.real - z.real, w.imag - z.imag);
	}
	
	public static Comp neg(Comp z) {
		return new Comp(-z.real, -z.imag);
	}
	
	public static Comp mul(Comp w, Comp z) {
		return new Comp(w.real*z.real - w.imag*z.imag,
						   w.real*z.imag + w.imag*z.real);
	}

	public static Comp div(Comp w, Comp z) {
		double rrinv = 1 / (z.real*z.real + z.imag*z.imag);
		return new Comp((w.real*z.real + w.imag*z.imag) * rrinv, 
						   (w.imag*z.real - w.real*z.imag) * rrinv);
	}
	
	public static Comp inv(Comp z) {
		double invrr = 1 / (z.real*z.real + z.imag*z.imag);
		return new Comp(z.real * invrr, -z.imag * invrr);
	}
	
	public static Comp exp(Comp z) {
		double exp = Math.exp(z.real);
		return new Comp(exp * Math.cos(z.imag), exp * Math.sin(z.imag));
	}
	
	public static Comp log(Comp z) {
		return new Comp(0.5 * Math.log(z.real*z.real + z.imag*z.imag), z.arg());
	}
}
