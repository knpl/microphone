package nl.knpl.microphone.util;

public class Complex {

	private double real, imag;
	
	public Complex(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}
	
	public Complex(double real) {
		this(real, 0);
	}
	
	public Complex() {
		this(0, 0);
	}
	
	public Complex(Complex that) {
		this(that.real, that.imag);
	}
	
	public static Complex fromPolar(double r, double the) {
		return new Complex(r * Math.cos(the), r * Math.sin(the));
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
	
	public Complex add(Complex z) {
		real += z.real;
		imag += z.imag;
		return this;
	}
	
	public Complex add(double x) {
		real += x;
		return this;
	}
	
	public Complex sub(Complex z) {
		real -= z.real;
		imag -= z.imag;
		return this;
	}
	
	public Complex sub(double x) {
		real -= x;
		return this;
	}
	
	public Complex neg() {
		real = -real;
		imag = -imag;
		return this;
	}
	
	public Complex mul(Complex z) {
		double temp;
		temp = real*z.real - imag*z.imag;
		imag = real*z.imag + imag*z.real;
		real = temp;
		return this;
	}
	
	public Complex mul(double x) {
		real *= x;
		imag *= x;
		return this;
	}
	
	public Complex div(Complex z) {
		double rrinv, temp;
		rrinv = 1 / (z.real*z.real + z.imag*z.imag);
		temp = (real*z.real + imag*z.imag) * rrinv;
		imag = (imag*z.real - real*z.imag) * rrinv;
		real = temp;
		return this;
	}
	
	public Complex div(double x) {
		real /= x;
		imag /= x;
		return this;
	}
	
	public Complex inv() {
		double rrinv;
		rrinv = 1 / (real*real + imag*imag);
		real *=  rrinv;
		imag *= -rrinv;
		return this;
	}
	
	public Complex exp() {
		double exp;
		exp = Math.exp(real);
		real = exp * Math.cos(imag);
		imag = exp * Math.sin(imag);
		return this;
	}
	
	public Complex log() {
		double temp;
		temp = 0.5 * Math.log(real * real + imag * imag);
		imag = arg();
		real = temp;
		return this;
	}
	
	public String toString() {
		return String.format("%7.3f + %7.3f*i", real, imag);
	}
	
	public static Complex add(Complex w, Complex z) {
		return new Complex(w.real + z.real, w.imag + z.imag); 
	}
	
	public static Complex sub(Complex w, Complex z) {
		return new Complex(w.real - z.real, w.imag - z.imag);
	}
	
	public static Complex neg(Complex z) {
		return new Complex(-z.real, -z.imag);
	}
	
	public static Complex mul(Complex w, Complex z) {
		return new Complex(w.real*z.real - w.imag*z.imag,
						   w.real*z.imag + w.imag*z.real);
	}

	public static Complex div(Complex w, Complex z) {
		double rrinv = 1 / (z.real*z.real + z.imag*z.imag);
		return new Complex((w.real*z.real + w.imag*z.imag) * rrinv, 
						   (w.imag*z.real - w.real*z.imag) * rrinv);
	}
	
	public static Complex inv(Complex z) {
		double invrr = 1 / (z.real*z.real + z.imag*z.imag);
		return new Complex(z.real * invrr, -z.imag * invrr);
	}
	
	public static Complex exp(Complex z) {
		double exp = Math.exp(z.real);
		return new Complex(exp * Math.cos(z.imag), exp * Math.sin(z.imag));
	}
	
	public static Complex log(Complex z) {
		return new Complex(0.5 * Math.log(z.real*z.real + z.imag*z.imag), z.arg());
	}
}
