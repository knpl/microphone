package nl.knpl.microphone.util;

public class BitReverse {
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
}
