package org.eb.FiveStraight.model;

import java.util.HashMap;

public class FiveStraightHashTab {

  static HashMap<String, Integer> hs = new HashMap<String, Integer>();

  static void iniths() {
    hs.clear();
  }

  static public long APHash(String str) {
    long hash = 0;
    for (int i = 0; i < str.length(); i++) {
      if ((i & 1) == 0) {
        hash ^= ((hash << 7) ^ str.charAt(i) ^ (hash >> 3));
      } else {
        hash ^= (~((hash << 11) ^ str.charAt(i) ^ (hash >> 5)));
      }
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
    long hash = 5381;
    for (int i = 0; i < str.length(); i++) {
      hash = ((hash << 5) + hash) + str.charAt(i);
    }
    return hash;
  }

  static void insert(StateOfGame ss, int val, int lev) {
    String s = ss.toString();
    //System.out.println("insert" + s + " val:" + val + " Size of Hashtable:" + hs.size());
    long h = DJBHash(s);
    hs.put(s, val);
  }

  static public Integer getVal(StateOfGame ss) {
    String s = ss.toString();
    Long n = DJBHash(s);
    Integer val = hs.get(s);
    if (val != null) {
      System.out.println("AAA" + s + " val:" + val + " hash:" + s + " Size of Hashtable:" + hs.size());
      //System.out.println("HASHTABLE --- " +  hs.toString());

    }
    return val;
  }
}
