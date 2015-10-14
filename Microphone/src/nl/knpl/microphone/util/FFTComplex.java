package nl.knpl.microphone.util;

public class FFTComplex {

	/* Returns n / 2 twiddle factors required to compute the fft */
	public static Complex[] twiddleFactors(int n, int sigma) {
		int h = n / 2;
		if (!(sigma == 1 || sigma == -1))
			throw new IllegalArgumentException("sigma must be equal to 1 or -1.");
		Complex[] twiddles = new Complex[h];

		double omega = 2 * Math.PI / n;
		if (sigma == -1)
			omega = -omega;

		for (int i = 0; i < h; ++i)
			twiddles[i] = Complex.fromPolar(1, i * omega);

		return twiddles;
	}

	public static void fft(Complex[] a, Complex[] twiddles, int lgn) {
		final int n = a.length;
		int h, step, idx;
		bitrevShuffle(a);
		
		for (int k = 1; k <= lgn; k++) {
			step = 1 << k;
			h = step >> 1;
			for (int start = 0; start < n; start += step) {
				for (int i = 0; i < h; i++) {
					idx = start + i;
					Complex even = a[idx];
					Complex odd  = Complex.mul(a[idx+h], twiddles[i << (lgn-k)]);
					a[idx]   = Complex.add(even, odd);
					a[idx+h] = Complex.sub(even, odd);
				}
			}
		}
	}
	
	public static void fftRecursive(Complex[] a) {
		bitrevShuffle(a);
		fftr(a, 0, a.length, -1);
	}
	
	public static void ifftRecursive(Complex[] a) {
		bitrevShuffle(a);
		
		final int n = a.length;
		fftr(a, 0, n, 1);
		
		final double ninv = 1.0 / (double)n;
		for (int i = 0; i < n; ++i) {
			a[i].mul(ninv);
		}
	}
	
	private static void fftr(Complex[] a, int start, int n, int sign) {
		Complex even, odd;
		if (n == 2) {
			even = a[start];
			odd  = a[start+1];
			
			a[start]   = Complex.add(even, odd);
			a[start+1] = Complex.sub(even, odd);
			return;
		}
		
		final int h = n / 2;
		fftr(a, start, h, sign);
		fftr(a, start + h, h, sign);
		
		final Complex root = Complex.fromPolar(1, sign * 2 * Math.PI / n);
		Complex twiddle = new Complex(1, 0);
		
		for (int i = start; i < start + h; ++i) {
			even = a[i];
			odd  = Complex.mul(twiddle, a[i+h]);
			
			a[i]   = Complex.add(even, odd);
			a[i+h] = Complex.sub(even, odd);
			
			twiddle.mul(root);
		}
	}
	
	private static void bitrevShuffle(Complex[] a) {
		final int n = a.length;
		final int h = n >> 1;
		
		Complex tmp;
		
		int i = 0,
			j = 0;
		while (i < n) {
			/* At all times: j = bitrev(i).
			 * swap a[i] and a[j] only if i < j.
			 * This avoids double swaps and swaps where i is equal to j. */
			if (i < j) {
				/* swap */
				tmp = a[i];
				a[i] = a[j];
				a[j] = tmp;
			}
			
			/* increment */
			i += 1;
			j = BitReverse.revinc(j, h);
		}
	}
}
