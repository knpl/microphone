package nl.knpl.microphone.tests;

import nl.knpl.microphone.DFT;
import nl.knpl.microphone.util.Comp;

public class Tests {

    public static void fftTests() {
        final int n = 4096, lgn = 12;
        float[] apacked = new float[2*n];
        double[] apackeddoubles = new double[2*n];
        float[] are = new float[n];
        float[] aim = new float[n];
        Comp[] a = new Comp[n];
        Comp[] b = new Comp[n];

        for (int i = 0; i < n; ++i) {
            apacked[2*i] = i+1;
            apacked[2*i+1] = 0;

            apackeddoubles[2*i] = i+1;
            apackeddoubles[2*i+1] = 0;

            are[i] = i+1;
            aim[i] = 0;

            a[i] = new Comp(i+1);
            b[i] = new Comp(i+1);
        }

        long now, then;
        then = System.currentTimeMillis();
        DFT.cfft(a);
        now = System.currentTimeMillis();
        android.util.Log.d("testtag", "old: " + (now - then) + "ms");

        Comp[] twiddles = DFT.twiddleFactors(n, -1);
        then = System.currentTimeMillis();
        DFT.cfft_iterative(b, twiddles, lgn);
        now = System.currentTimeMillis();
        android.util.Log.d("testtag", "new: " + (now - then) + "ms");

        float[] ftwiddles = DFT.twiddleFactorsPacked(n, -1);
        then = System.currentTimeMillis();
        DFT.cfft_iterative_packed(apacked, ftwiddles, lgn);
        now = System.currentTimeMillis();
        android.util.Log.d("testtag", "new packed: " + (now - then) + "ms");

        double[] ftwiddlesdoubles = DFT.twiddleFactorsPackedDoubles(n, -1);
        then = System.currentTimeMillis();
        DFT.cfft_iterative_packed(apackeddoubles, ftwiddlesdoubles, lgn);
        now = System.currentTimeMillis();
        android.util.Log.d("testtag", "new packed doubles: " + (now - then) + "ms");

        android.util.Log.d("testtag", "reference: ");
        for (int i = 0; i < 10; ++i) {
            android.util.Log.d("testtag", ""+a[i]);
        }
        android.util.Log.d("testtag", "...");

        android.util.Log.d("testtag", "iterative: ");
        for ( int i = 0; i < 10; ++i) {
            android.util.Log.d("testtag", ""+b[i]);
        }
        android.util.Log.d("testtag", "...");

        android.util.Log.d("testtag", "reference: ");
        for (int i = 0; i < 10; ++i) {
            android.util.Log.d("testtag", String.format("%7.3f + %7.3f*i", apacked[2*i], apacked[2*i+1]));
        }
        android.util.Log.d("testtag", "...");

        android.util.Log.d("testtag", "reference: ");
        for (int i = 0; i < 10; ++i) {
            android.util.Log.d("testtag", String.format("%7.3f + %7.3f*i", apackeddoubles[2*i], apackeddoubles[2*i+1]));
        }
        android.util.Log.d("testtag", "...");
    }
}