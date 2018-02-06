package org.eb.FiveStraight.model;

import org.eb.FiveStraight.util.Constants;
import java.util.ArrayList;

public class WinningRows {
static int gr[][];
  static int grs[][] = new int[Constants.NUMBEROFFIELDS][];
  static int neighbours[][] = new int[Constants.NUMBEROFFIELDS][];

  static void computeWinningRow(ArrayList<int[]> grr, int x, int y, int s, int z) {
    int l = 0;
    int reihe[] = new int[5];

    while (l < 5 && s >= 0 && s < Constants.NUMBEROFCOLUMNS && z >= 0 && z < Constants.NUMBEROFROWS) {
      reihe[l] = z * Constants.NUMBEROFCOLUMNS + s;
      l++;
      s += x;
      z += y;
    }
    if (l == 5) {
      grr.add(reihe);
    }
  }

  static void dumpWinningRows() {
    for (int a[] : gr) {
      String s = String.format("INFO:%d %d %d %d %d", a[0], a[1], a[2], a[3], a[4]);
      System.out.println(s);
    }
  }

  static void dumpNeighbours() {
    for (int z = 0; z < Constants.NUMBEROFFIELDS; z++) {
      StringBuilder s = new StringBuilder(String.format("Feld:%d - ", z));
      for (int a : neighbours[z]) {
        s.append(String.format("%d ", a));
      }
      System.out.println(s);
    }
  }

  static void initNeighbours() {
    for (int z = 0; z < Constants.NUMBEROFROWS; z++) {
      for (int s = 0; s < Constants.NUMBEROFCOLUMNS; s++) {
        int n = z * Constants.NUMBEROFCOLUMNS + s;
        ArrayList<Integer> nbs = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
          for (int j = -1; j <= 1; j++) {
            if (i != 0 || j != 0) {
              int zz = (z + i);
              int ss = (s + j);
              if (zz >= 0 && zz < Constants.NUMBEROFROWS && ss >= 0 && ss < Constants.NUMBEROFCOLUMNS) {
                nbs.add(zz * Constants.NUMBEROFCOLUMNS + ss);
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

  static void initWinningRows() {
    ArrayList<int[]> grr = new ArrayList<>();
    for (int s = 0; s < Constants.NUMBEROFCOLUMNS; s++) {
      for (int z = 0; z < Constants.NUMBEROFROWS; z++) {
        computeWinningRow(grr, 0, 1, s, z);
        computeWinningRow(grr, 1, 1, s, z);
        computeWinningRow(grr, 1, 0, s, z);
        computeWinningRow(grr, 1, -1, s, z);
      }
    }

    gr = new int[grr.size()][5];
    int k = 0;
    for (int[] r : grr) {
      gr[k++] = r;
    }

    for (int i = 0; i < Constants.NUMBEROFFIELDS; i++) {
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
    // dumpWinningRows();
  }
};