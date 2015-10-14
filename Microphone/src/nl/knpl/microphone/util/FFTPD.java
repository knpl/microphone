package nl.knpl.microphone.util;

public class FFTPD {
	public static double[] twiddleFactors(int n, int sigma) {
		int h = n >> 1;
		if (!(sigma == 1 || sigma == -1))
			throw new IllegalArgumentException("sigma must be equal to 1 or -1.");
		double[] twiddles = new double[n];

		double omega = 2 * Math.PI / n;
		if (sigma == -1)
			omega = -omega;

		for (int i = 0; i < h; ++i) {
			twiddles[2*i]   = Math.cos(i * omega);
			twiddles[2*i+1] = Math.sin(i * omega);
		}

		return twiddles;
	}
	
	public static void fft(double[] a, double[] twiddles, int lgn) {
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
				for (int i = 0; i <  h; i++) {
					idx = start + i;
					
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
	
	private static void bitrevShuffle(double[] a) {
		final int n = a.length >> 1;
		final int h = n >> 1;

		double real, imag;

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
