package nl.knpl.microphone;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import nl.knpl.microphone.util.Comp;

public class DFT {

	public static float[] makeDFTMatrix(final int n, int sigma) {
		if (!(sigma == 1 || sigma == -1)) {
			throw new IllegalArgumentException();
		}
		
		final float[] mat = new float[n*(2*n)];
		final float omega = (float) (2*Math.PI) / n;
		
		int m, idx;
		float re, im;
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j <= i; ++j) {
				m = (i * j) % n;
				if (m == 0) {
					re = 1;
					im = 0;
				}
				else {
					re = (float) Math.cos(omega * m);
					im = (float) (sigma * Math.sin(omega * m));
				}
				
				idx = i*n+j;
				mat[2*idx] = re;
				mat[2*idx+1] = im;
				if (j != i) {
					idx = j*n+i;
					mat[2*idx] = re;
					mat[2*idx+1] = im;
				}
			}
		}
		
		return mat;
	}
	
	public static void dft(float[] dft, float[] in, float[] out, int n) {
		if (dft.length != 2*n*n || in.length != 2*n || out.length != 2*n) {
			throw new IndexOutOfBoundsException();
		}
		mul(dft, in, out, n);
	}
	
	public static void idft(float[] idft, float[] in, float[] out, int n) {
		if (idft.length != 2*n*n || in.length != 2*n || out.length != 2*n) {
			throw new IndexOutOfBoundsException();
		}
		mul(idft, in, out, n);
		for (int i = 0; i < 2*n; ++i) {
			out[i] /= n;
		}
	}

	/* Returns n / 2 twiddle factors required to compute the fft */
	public static Comp[] twiddleFactors(int n, int sigma) {
		int h = n >> 1;
		if (!(sigma == 1 || sigma == -1))
			throw new IllegalArgumentException("sigma must be equal to 1 or -1.");
		Comp[] twiddles = new Comp[h];

		double omega = 2 * Math.PI / n;
		if (sigma == -1)
			omega = -omega;

		for (int i = 0; i < h; ++i)
			twiddles[i] = Comp.fromPolar(1, i * omega);

		return twiddles;
	}

	public static float[] twiddleFactorsPacked(int n, int sigma) {
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

	public static double[] twiddleFactorsPackedDoubles(int n, int sigma) {
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

	public static void cfft_iterative_packed(double[] a, double[] twiddles, int lgn) {
		final int n = a.length >> 1;
		bitrevShuffle(a);
		double evenreal, evenimag,
				oddreal, oddimag,
				twidreal, twidimag,
				buf;
		int h, step, twididx;
		for (int k = 1; k <= lgn; k++) {
			step = 1 << k;
			h = step >> 1;
			for (int start = 0; start < n; start += step) {
				for (int i = start; i < start + h; i++) {
					evenreal = a[2*i];
					evenimag = a[2*i+1];
					oddreal  = a[2*(i+h)];
					oddimag  = a[2*(i+h)+1];

					twididx = (i-start) << (lgn-k);
					twidreal = twiddles[2*twididx];
					twidimag = twiddles[2*twididx+1];

					buf     = oddreal * twidreal - oddimag * twidimag;
					oddimag = oddreal * twidimag + oddimag * twidreal;
					oddreal = buf;

					a[2*i]   = evenreal + oddreal;
					a[2*i+1] = evenimag + oddimag;
					a[2*(i+h)]   = evenreal - oddreal;
					a[2*(i+h)+1] = evenimag - oddimag;
				}
			}
		}
	}

	public static void cfft_iterative_packed(float[] a, float[] twiddles, int lgn) {
		final int n = a.length >> 1;
		bitrevShuffle(a);
		float evenreal, evenimag,
				oddreal, oddimag,
				twidreal, twidimag,
				buf;
		int h, step, twididx;
		for (int k = 1; k <= lgn; k++) {
			step = 1 << k;
			h = step >> 1;
			for (int start = 0; start < n; start += step) {
				for (int i = start; i < start + h; i++) {
					evenreal = a[2*i];
					evenimag = a[2*i+1];
					oddreal  = a[2*(i+h)];
					oddimag  = a[2*(i+h)+1];

					twididx = (i-start) << (lgn-k);
					twidreal = twiddles[2*twididx];
					twidimag = twiddles[2*twididx+1];

					buf     = oddreal * twidreal - oddimag * twidimag;
					oddimag = oddreal * twidimag + oddimag * twidreal;
					oddreal = buf;

					a[2*i]   = evenreal + oddreal;
					a[2*i+1] = evenimag + oddimag;
					a[2*(i+h)]   = evenreal - oddreal;
					a[2*(i+h)+1] = evenimag - oddimag;
				}
			}
		}
	}

	public static void cfft_iterative(Comp[] a, Comp[] twiddles, int lgn) {
		final int n = a.length;
		bitrevShuffle(a);
		int h, step;
		for (int k = 1; k <= lgn; k++) {
			step = 1 << k;
			h = step >> 1;
			for (int start = 0; start < n; start += step) {
				for (int i = start; i < start + h; i++) {
					Comp even = a[i];
					Comp odd  = Comp.mul(a[i+h], twiddles[(i-start) << (lgn-k)]);
					a[i]   = Comp.add(even, odd);
					a[i+h] = Comp.sub(even, odd);
				}
			}
		}
	}
	
	public static void cfft(Comp[] a) {
		bitrevShuffle(a);
		cfft_r(a, 0, a.length, -1);
	}
	
	public static void cifft(Comp[] a) {
		bitrevShuffle(a);
		
		final int n = a.length;
		cfft_r(a, 0, n, 1);
		
		final double ninv = 1.0 / (double)n;
		for (int i = 0; i < n; ++i) {
			a[i].mul(ninv);
		}
	}
	
	private static void cfft_r(Comp[] a, int start, int n, int sign) {
		Comp even, odd;
		if (n == 2) {
			even = a[start];
			odd  = a[start+1];
			
			a[start]   = Comp.add(even, odd);
			a[start+1] = Comp.sub(even, odd);
			return;
		}
		
		final int h = n / 2;
		cfft_r(a, start, h, sign);
		cfft_r(a, start + h, h, sign);
		
		final Comp root = Comp.fromPolar(1, sign * 2 * Math.PI / n);
		Comp twiddle = new Comp(1, 0);
		
		for (int i = start; i < start + h; ++i) {
			even = a[i];
			odd  = Comp.mul(twiddle, a[i+h]);
			
			a[i]   = Comp.add(even, odd);
			a[i+h] = Comp.sub(even, odd);
			
			twiddle.mul(root);
		}
	}
	
	public static void cfft(float[] re, float[] im) {
		bitrevShuffle(re, im);
		cfft_r(re, im, 0, re.length);
	}
	
	private static void cfft_r(float[] re, float[] im, int start, int n) {
		float tmpreal, tmpimag;
		float real, imag;
		if (n == 2) {
			/* butterfly */
			real = re[start+1];
			imag = im[start+1];
			tmpreal = re[start];
			tmpimag = im[start];
			
			re[start] = tmpreal + real;
			im[start] = tmpimag + imag;
			
			re[start+1] = tmpreal - real;
			im[start+1] = tmpimag - imag;
			
			return;
		}
		
		final int h = n / 2,
				  pivot = start + h;
		cfft_r(re, im, start, h);
		cfft_r(re, im, pivot, h);
		
		final double omega = -2*Math.PI/n,
			 	 	 rootreal = Math.cos(omega),
			 	 	 rootimag = Math.sin(omega);
		
		double twidreal = 1,
			   twidimag = 0,
			   tmp;
		
		for (int i = start; i < pivot; ++i) {
			real = (float) (twidreal * re[i+h] - twidimag * im[i+h]);
			imag = (float) (twidimag * re[i+h] + twidreal * im[i+h]);
			tmpreal = re[i];
			tmpimag = im[i];
			
			re[i] = tmpreal + real;
			im[i] = tmpimag + imag;
			
			re[i+h] = tmpreal - real;
			im[i+h] = tmpimag - imag;
			
			tmp		 = twidreal*rootreal - twidimag*rootimag;
			twidimag = twidreal*rootimag + twidimag*rootreal;
			twidreal = tmp;
		}
	}
	
	public static void cfft_packed(float[] a) {
		bitrevShuffle(a);
		cfft_packed_r(a, 0, a.length / 2);
	}
	
	private static void cfft_packed_r(float[] a, int start, int n) {
		float tmpreal, tmpimag;
		float real, imag;
		if (n == 2) {
			/* butterfly */
			real = a[2*(start+1)];
			imag = a[2*(start+1)+1];
			tmpreal = a[2*start];
			tmpimag = a[2*start+1];
			
			a[2*start]   	 = tmpreal + real;
			a[2*start+1] 	 = tmpimag + imag;
			
			a[2*(start+1)]   = tmpreal - real;
			a[2*(start+1)+1] = tmpimag - imag;
			
			return;
		}
		
		final int h = n / 2,
				  pivot = start + h;
		cfft_packed_r(a, start, h);
		cfft_packed_r(a, pivot, h);
		
		final double omega = -2*Math.PI/n,
			 	 	 rootreal = Math.cos(omega),
			 	 	 rootimag = Math.sin(omega);
		
		double twidreal = 1,
			   twidimag = 0,
			   tmp;
		
		for (int i = start; i < pivot; ++i) {
			real = (float) (twidreal * a[2*(i+h)] - twidimag * a[2*(i+h)+1]);
			imag = (float) (twidimag * a[2*(i+h)] + twidreal * a[2*(i+h)+1]);
			tmpreal = a[2*i];
			tmpimag = a[2*i+1];
			
			a[2*i]   	 = tmpreal + real;
			a[2*i+1] 	 = tmpimag + imag;
			
			a[2*(i+h)]   = tmpreal - real;
			a[2*(i+h)+1] = tmpimag - imag;
			
			tmp		 = twidreal*rootreal - twidimag*rootimag;
			twidimag = twidreal*rootimag + twidimag*rootreal;
			twidreal = tmp;
		}
	}
	
	public static void bitrevShuffle(float[] a) {
		final int n = a.length >> 1;
		final int h = n >> 1;
		
		float real, imag;
		
		int i = 0,
			j = 0;
		while (i < n) {
			/* At all times: j = revinc(i).
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
			j = revinc(j, h);
		}
	}

	private static void bitrevShuffle(double[] a) {
		final int n = a.length >> 1;
		final int h = n >> 1;

		double real, imag;

		int i = 0,
				j = 0;
		while (i < n) {
			/* At all times: j = revinc(i).
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
			j = revinc(j, h);
		}
	}
	
	private static void bitrevShuffle(float[] re, float[] im) {
		final int n = re.length;
		final int h = n >> 1;
		
		float tmp;
		
		int i = 0,
			j = 0;
		while (i < n) {
			/* At all times: j = revinc(i).
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
			j = revinc(j, h);
		}
	}
	
	private static void bitrevShuffle(Comp[] a) {
		final int n = a.length;
		final int h = n >> 1;
		
		Comp tmp;
		
		int i = 0,
			j = 0;
		while (i < n) {
			/* At all times: j = revinc(i).
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
			j = revinc(j, h);
		}
	}
	
	public static int revinc(int a, int h) {
		if (a != (h << 1) - 1)
			while (((a^=h) & h) == 0)
				h >>= 1;
		return a;
	}
	
	public static int reverseBitsInt(int a, int nbits) {
		a = (a & 0x55555555) << 1  | (a & 0xaaaaaaaa) >> 1;
		a = (a & 0x33333333) << 2  | (a & 0xcccccccc) >> 2;
		a = (a & 0x0f0f0f0f) << 4  | (a & 0xf0f0f0f0) >> 4;
		a = (a & 0x00ff00ff) << 8  | (a & 0xff00ff00) >> 8;
		a = (a & 0x0000ffff) << 16 | (a & 0xffff0000) >> 16;
		return (a >> (32 - nbits)) & ((1 << nbits) - 1);
	}
	
	private static void mul(float[] mat, float[] in, float[] out, int n) {
		int idx;
		float x, y, u, v,
			  realsum, imagsum;
		for (int i = 0; i < n; ++i) {
			realsum = 0;
			imagsum = 0;
			for (int j = 0; j < n; ++j) {
				idx = i*n+j;
				/* mat[i,j] = u + v*i */
				u = mat[2*idx];
				v = mat[2*idx+1];
				/* a[j] = x + y*i */
				x = in[2*j];
				y = in[2*j+1];
				
				/* a[j] = mat[i,j] * a[j] = (u + v*i)*(x + y*i) = (ux - vy) + (xv + uy)*i */	
				realsum += x*u - y*v; 
				imagsum += x*v + y*u;
			}
			out[2*i]   = realsum;
			out[2*i+1] = imagsum;
		}
	}
	
	public static String printVector(float[] vec, int n) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(baos);
		
		if (vec.length != 2*n) {
			throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < n; ++i) {
			out.write(String.format("(%7.3f + %7.3f*i)\n", vec[2*i], vec[2*i+1]));
		}
		out.flush();
		
		String result = null;
		try {
			result = baos.toString("UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}
	
	public static String printMatrix(float[] mat, int n) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(baos);
		
		
		if (mat.length != 2*n*n) {
			throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				int idx = i*n+j;
				out.write(String.format("(%7.3f + %7.3f*i)  ", mat[2*idx], mat[2*idx+1]));
			}
			out.write("\n");
		}
		out.flush();
		
		String result = null;
		try {
			result = baos.toString("UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}
}
