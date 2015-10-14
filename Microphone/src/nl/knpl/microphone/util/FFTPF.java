package nl.knpl.microphone.util;

public class FFTPF {
	public static float[] twiddleFactors(int n, int sigma) {
		int h = n >> 1;
		if (!(sigma == 1 || sigma == -1))
			throw new IllegalArgumentException("sigma must be equal to 1 or -1.");
		float[] twiddles = new float[n];

		double omega = 2 * Math.PI / n;
		if (sigma == -1)
			omega = -omega;

		for (int i = 0; i < h; ++i) {
			twiddles[2*i]   = (float)Math.cos(i * omega);
			twiddles[2*i+1] = (float)Math.sin(i * omega);
		}

		return twiddles;
	}
	
	public static void fft(float[] a, float[] twiddles, int lgn) {
		final int n = a.length >> 1;
		float evenreal, evenimag,
				oddreal, oddimag,
				twidreal, twidimag,
				buf;
		int h, step, twididx, idx;
		
		bitrevShuffle(a);
		
		for (int k = 1; k <= lgn; k++) {
			step = 1 << k;
			h = step >> 1;
			for (int start = 0; start < n; start += step) {
				for (int i = 0; i < h; i++) {
					idx = i + start;
					evenreal = a[2*idx];
					evenimag = a[2*idx+1];
					oddreal  = a[2*(idx+h)];
					oddimag  = a[2*(idx+h)+1];

					twididx = i << (lgn-k);
					twidreal = twiddles[2*twididx];
					twidimag = twiddles[2*twididx+1];

					buf     = oddreal * twidreal - oddimag * twidimag;
					oddimag = oddreal * twidimag + oddimag * twidreal;
					oddreal = buf;

					a[2*idx]   = evenreal + oddreal;
					a[2*idx+1] = evenimag + oddimag;
					a[2*(idx+h)]   = evenreal - oddreal;
					a[2*(idx+h)+1] = evenimag - oddimag;
				}
			}
		}
	}
	
	public static void fft(float[] a, double[] twiddles, int lgn) {
		final int n = a.length >> 1;
		double evenreal, evenimag,
				oddreal, oddimag,
				twidreal, twidimag,
				buf;
		int h, step, twididx, idx;
		
		bitrevShuffle(a);
		
		for (int k = 1; k <= lgn; k++) {
			step = 1 << k;
			h = step >> 1;
			for (int start = 0; start < n; start += step) {
				for (int i = 0; i < h; i++) {
					idx = i + start;
					evenreal = a[2*idx];
					evenimag = a[2*idx+1];
					oddreal  = a[2*(idx+h)];
					oddimag  = a[2*(idx+h)+1];

					twididx = i << (lgn-k);
					twidreal = twiddles[2*twididx];
					twidimag = twiddles[2*twididx+1];

					buf     = oddreal * twidreal - oddimag * twidimag;
					oddimag = oddreal * twidimag + oddimag * twidreal;
					oddreal = buf;

					a[2*idx]   = (float) (evenreal + oddreal);
					a[2*idx+1] = (float) (evenimag + oddimag);
					a[2*(idx+h)]   = (float) (evenreal - oddreal);
					a[2*(idx+h)+1] = (float) (evenimag - oddimag);
				}
			}
		}
	}
	
	/* performs fft on real input data.
	 * in is an array of n floats, interpreted as n real numbers. in is not modified.
	 * out is an array of 2n floats, interpreted as n complex numbers in packed float format. out holds
	 * the result so is modified.
	 * twiddles is an array of n floats, interpreted as n/2 complex numbers in packed float format. 
	 * twiddles is not modified.
	 * lgn is the base two logarithm of n. (n = 2^lgn)
	 */
	public static void realfft(float[] in, float[] out, float[] twiddles, int lgn) {
		final int n = in.length;
		float evenreal, evenimag,
				oddreal, oddimag,
				twidreal, twidimag,
				buf;
		int h, step, twididx, idx;
		
		realToCompBitrevShuffle(in, out);
		
		for (int k = 1; k <= lgn; k++) {
			step = 1 << k;
			h = step >> 1;
			for (int start = 0; start < n; start += step) {
				for (int i = 0; i < h; i++) {
					idx = i + start;
					evenreal = out[2*idx];
					evenimag = out[2*idx+1];
					oddreal  = out[2*(idx+h)];
					oddimag  = out[2*(idx+h)+1];

					twididx = i << (lgn-k);
					twidreal = twiddles[2*twididx];
					twidimag = twiddles[2*twididx+1];

					buf     = oddreal * twidreal - oddimag * twidimag;
					oddimag = oddreal * twidimag + oddimag * twidreal;
					oddreal = buf;

					out[2*idx]   = evenreal + oddreal;
					out[2*idx+1] = evenimag + oddimag;
					out[2*(idx+h)]   = evenreal - oddreal;
					out[2*(idx+h)+1] = evenimag - oddimag;
				}
			}
		}
	}
	
	/* performs fft on real input data.
	 * in is an array of n floats, interpreted as n real numbers. in is not modified.
	 * out is an array of 2n floats, interpreted as n complex numbers in packed float format. out holds
	 * the result so is modified.
	 * twiddles is an array of n floats, interpreted as n/2 complex numbers in packed float format. 
	 * twiddles is not modified.
	 * lgn is the base two logarithm of n. (n = 2^lgn)
	 */
	public static void realfft(float[] in, float[] out, double[] twiddles, int lgn) {
		final int n = in.length;
		double evenreal, evenimag,
				oddreal, oddimag,
				twidreal, twidimag,
				buf;
		int h, step, twididx, idx;
		
		realToCompBitrevShuffle(in, out);
		
		for (int k = 1; k <= lgn; k++) {
			step = 1 << k;
			h = step >> 1;
			for (int start = 0; start < n; start += step) {
				for (int i = 0; i < h; i++) {
					idx = i + start;
					evenreal = out[2*idx];
					evenimag = out[2*idx+1];
					oddreal  = out[2*(idx+h)];
					oddimag  = out[2*(idx+h)+1];

					twididx = i << (lgn-k);
					twidreal = twiddles[2*twididx];
					twidimag = twiddles[2*twididx+1];

					buf     = oddreal * twidreal - oddimag * twidimag;
					oddimag = oddreal * twidimag + oddimag * twidreal;
					oddreal = buf;

					out[2*idx]   = (float) (evenreal + oddreal);
					out[2*idx+1] = (float) (evenimag + oddimag);
					out[2*(idx+h)]   = (float) (evenreal - oddreal);
					out[2*(idx+h)+1] = (float) (evenimag - oddimag);
				}
			}
		}
	}
	
	public static void realBitrevShuffle(float[] a) {
		final int n = a.length;
		final int h = n >> 1;
		
		float temp;
		
		int i = 0,
			j = 0;
		while (i < n) {
			/* At all times: j = bitrev(i).
			 * swap a[i] and a[j] only if i < j.
			 * This avoids double swaps and swaps where i is equal to j. */
			if (i < j) {
				/* swap */
				temp = a[i];
				a[i] = a[j];
				a[j] = temp;
			}
			
			/* increment */
			i += 1;
			j = BitReverse.revinc(j, h);
		}
	}
	
	public static void realToCompBitrevShuffle(float[] srcreal, float[] dstcmpl) {
		final int n = srcreal.length;
		final int h = n >> 1;
		
		int i = 0,
			j = 0;
		while (i < n) {
			/* At all times: j = bitrev(i). */
			dstcmpl[2*i] = srcreal[j];
			dstcmpl[2*i+1] = 0;
			
			/* increment */
			i += 1;
			j = BitReverse.revinc(j, h);
		}
	}
	
	public static void bitrevShuffle(float[] a) {
		final int n = a.length >> 1;
		final int h = n >> 1;
		
		float real, imag;
		
		int i = 0,
			j = 0;
		while (i < n) {
			/* At all times: j = bitrev(i).
			 * swap a[i] and a[j] only if i < j.
			 * This avoids double swaps and swaps where i is equal to j. */
			if (i < j) {
				/* swap */
				real     = a[2*i  ];
				imag     = a[2*i+1];
				a[2*i  ] = a[2*j  ];
				a[2*i+1] = a[2*j+1];
				a[2*j  ] = real;
				a[2*j+1] = imag;
			}
			
			/* increment */
			i += 1;
			j = BitReverse.revinc(j, h);
		}
	}
}
