package org.eb.FiveStraight;

import java.util.ArrayList;

enum FeldWerte {
  COMP, FREI, SPIELER
};

enum GewinnReihenWerte {
  COMP, FREI, SPIELER, NEUTRAL
};

public class Globals {

  static final int SANZ = 10;
  static final int ZANZ = SANZ;
  static final int SANZZANZ = SANZ * ZANZ;

}

class GewinnReihen {

  static int gr[][];
  static int grs[][] = new int[Globals.SANZZANZ][];
  static int neighbours[][] = new int[Globals.SANZZANZ][];

  static void berechnegewinnreihe(ArrayList<int[]> grr, int x, int y, int s, int z) {
    int l = 0;
    int reihe[] = new int[5];

    while (l < 5 && s >= 0 && s < Globals.SANZ && z >= 0 && z < Globals.ZANZ) {
      reihe[l] = z * Globals.SANZ + s;
      l++;
      s += x;
      z += y;
    }
    if (l == 5) {
      grr.add(reihe);
    }
  }

  static void dumpgewinnreihen() {
    for (int a[] : gr) {
      String s = String.format("INFO:%d %d %d %d %d", a[0], a[1], a[2], a[3], a[4]);
      System.out.println(s);
    }
  }

  static void dumpneighbours() {
    for (int z = 0; z < Globals.SANZZANZ; z++) {
      StringBuilder s = new StringBuilder(String.format("Feld:%d - ", z));
      for (int a : neighbours[z]) {
        s.append(String.format("%d ", a));
      }
      System.out.println(s);
    }
  }

  static void initneighbours() {
    for (int z = 0; z < Globals.ZANZ; z++) {
      for (int s = 0; s < Globals.SANZ; s++) {
        int n = z * Globals.SANZ + s;
        ArrayList<Integer> nbs = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
          for (int j = -1; j <= 1; j++) {
            if (i != 0 || j != 0) {
              int zz = (z + i);
              int ss = (s + j);
              if (zz >= 0 && zz < Globals.ZANZ && ss >= 0 && ss < Globals.SANZ) {
                nbs.add(zz * Globals.SANZ + ss);
              }
            }
          }
        }
        neighbours[n] = new int[nbs.size()];
        int k = 0;
        for (int x : nbs) {
          neighbours[n][k++] = x;
        }
      }
    }
    //dumpneighbours();
  }

  static void initgewinnreihen() {
    ArrayList<int[]> grr = new ArrayList<>();
    for (int s = 0; s < Globals.SANZ; s++) {
      for (int z = 0; z < Globals.ZANZ; z++) {
        berechnegewinnreihe(grr, 0, 1, s, z);
        berechnegewinnreihe(grr, 1, 1, s, z);
        berechnegewinnreihe(grr, 1, 0, s, z);
        berechnegewinnreihe(grr, 1, -1, s, z);
      }
    }

    gr = new int[grr.size()][5];
    int k = 0;
    for (int[] r : grr) {
      gr[k++] = r;
    }

    for (int i = 0; i < Globals.SANZZANZ; i++) {
      ArrayList<Integer> v = new ArrayList<>();
      for (int j = 0; j < grr.size(); j++) {
        int a[] = grr.get(j);
        if (a[0] == i || a[1] == i || a[2] == i || a[3] == i || a[4] == i) {
          v.add(j);
        }
      }
      int x[] = new int[v.size()];
      int j = 0;
      for (Integer I : v) {
        x[j++] = I;
      }
      grs[i] = x;
    }
    // dumpgewinnreihen();
  }
};
