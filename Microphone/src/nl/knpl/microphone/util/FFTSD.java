package nl.knpl.microphone.util;

public class FFTSD {
	public static void twiddleFactors(double[] twidre, double[] twidim, int n, int sigma) {
		int h = n >> 1;
		if (!(sigma == 1 || sigma == -1))
			throw new IllegalArgumentException("sigma must be equal to 1 or -1.");

		double omega = 2 * Math.PI / n;
		if (sigma == -1)
			omega = -omega;

		for (int i = 0; i < h; ++i) {
			twidre[i] = Math.cos(i * omega);
			twidim[i] = Math.sin(i * omega);
		}
	}
	
	public static void fft(double[] re, double[] im, double[] twidre, double[] twidim, int lgn) {
		final int n = re.length;
		double evenreal, evenimag,
				oddreal, oddimag,
				twidreal, twidimag,
				buf;
		int h, step, twididx, idx;
		
		bitrevShuffle(re, im);
		
		for (int k = 1; k <= lgn; k++) {
			step = 1 << k;
			h = step >> 1;
			for (int start = 0; start < n; start += step) {
				for (int i = 0; i < h; i++) {
					idx = i + start;
					evenreal = re[idx];
					evenimag = im[idx];
					oddreal  = re[idx+h];
					oddimag  = im[idx+h];

					twididx = i << (lgn-k);
					twidreal = twidre[twididx];
					twidimag = twidim[twididx];

					buf     = oddreal * twidreal - oddimag * twidimag;
					oddimag = oddreal * twidimag + oddimag * twidreal;
					oddreal = buf;

					re[idx]   = evenreal + oddreal;
					im[idx] = evenimag + oddimag;
					re[idx+h]   = evenreal - oddreal;
					im[idx+h] = evenimag - oddimag;
				}
			}
		}
	}
	
	private static void bitrevShuffle(double[] re, double[] im) {
		final int n = re.length;
		final int h = n >> 1;
		
		double tmp;
		
		int i = 0,
			j = 0;
		while (i < n) {
			/* At all times: j = bitrev(i).
			 * swap a[i] and a[j] only if i < j.
			 * This avoids double swaps and swaps where i is equal to j. */
			if (i < j) {
				/* swap */
				tmp = re[i];
				re[i] = re[j];
				re[j] = tmp;
				
				tmp = im[i];
				im[i] = im[j];
				im[j] = tmp;
			}
			
			/* increment */
			i += 1;
			j = BitReverse.revinc(j, h);
		}
	}
}
