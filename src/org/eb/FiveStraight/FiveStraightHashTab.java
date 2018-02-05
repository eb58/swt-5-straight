package org.eb.FiveStraight;

import java.util.HashMap;

public class FiveStraightHashTab {

	static HashMap<Long, Integer>	hs	= new HashMap<Long, Integer>();

	static void iniths() {
		hs.clear();
	}

	static public long APHash(String str) {
		long hash = 0;
		for (int i = 0; i < str.length(); i++) {
			if ((i & 1) == 0)
				hash ^= ((hash << 7) ^ str.charAt(i) ^ (hash >> 3));

			else
				hash ^= (~((hash << 11) ^ str.charAt(i) ^ (hash >> 5)));
		}
		return hash;
	}

	static public long RSHash(String str) {
		int b = 378551;
		int a = 63689;
		long hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = hash * a + str.charAt(i);
			a = a * b;
		}
		return hash;
	}

	static public long JSHash(String str) {
		long hash = 1315423911;
		for (int i = 0; i < str.length(); i++) {
			hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));
		}
		return hash;
	}

	static public long DJBHash(String str) {
		int hash = 5381;
		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + hash) + str.charAt(i);
		}
		return hash;
	}

	static void insert(SpielStand ss, int val, int lev) {
		if( lev < 2 || lev >4 ) return;
		String s = ss.toString();
		long h = APHash(s);
		hs.put(h, val);
	}

	static public int getVal(SpielStand ss, int lev) {
		if( lev < 2 || lev >4 ) return -100000;
		String s = ss.toString();
		Long n = APHash(s);
		Integer val = hs.get(n);
		return val==null?-100000:val;
	}
}
