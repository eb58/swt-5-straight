package org.eb.FiveStraight.model;

import org.eb.FiveStraight.util.Constants;
import java.util.ArrayList;

public class WinningRows {

  static int[][] winningRows;
  static int[][] winningRowsForField = new int[Constants.NUMBEROFFIELDS][];
  static int neighbours[][] = new int[Constants.NUMBEROFFIELDS][];

  static void init() {
    WinningRows.initWinningRows();
    WinningRows.initNeighbours();
    dumpNeighbours();
    dumpWinningRows();
  }

  private static int[] Convert2IntArray(ArrayList<Integer> al) {
    int arr[] = new int[al.size()];

    for (int i = 0, sz = al.size(); i < sz; i++) {
      arr[i] = al.get(i);
    }

    return arr;
  }

  private static int[][] Convert2IntArrayArray(ArrayList<ArrayList<Integer>> al) {
    int arr[][] = new int[al.size()][];

    for (int i = 0, sz = al.size(); i < sz; i++) {
      arr[i] = Convert2IntArray(al.get(i));
    }

    return arr;
  }

  private static void computeWinningRow(ArrayList<ArrayList<Integer>> grr, int x, int y, int row, int col) {
    ArrayList<Integer> winningrow = new ArrayList<>();

    while (winningrow.size() < 5 && row >= 0 && row < Constants.NUMBEROFCOLUMNS && col >= 0 && col < Constants.NUMBEROFROWS) {
      winningrow.add(col * Constants.NUMBEROFCOLUMNS + row);
      row += x;
      col += y;
    }

    if (winningrow.size() == 5) {
      grr.add(winningrow);
    }

  }

  private static void initNeighbours() {
    for (int row = 0; row < Constants.NUMBEROFROWS; row++) {
      for (int col = 0; col < Constants.NUMBEROFCOLUMNS; col++) {
        int fieldNumber = row * Constants.NUMBEROFCOLUMNS + col;
        ArrayList<Integer> nbs = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
          for (int j = -1; j <= 1; j++) {
            if (i != 0 || j != 0) {
              int rr = (row + i);
              int cc = (col + j);
              if (rr >= 0 && rr < Constants.NUMBEROFROWS && cc >= 0 && cc < Constants.NUMBEROFCOLUMNS) {
                nbs.add(rr * Constants.NUMBEROFCOLUMNS + cc);
              }
            }
          }
        }
        neighbours[fieldNumber] = Convert2IntArray(nbs);
      }
    }
  }

  private static void initWinningRows() {
    ArrayList<ArrayList<Integer>> winningRowsAsList = new ArrayList<>();
    for (int col = 0; col < Constants.NUMBEROFCOLUMNS; col++) {
      for (int row = 0; row < Constants.NUMBEROFROWS; row++) {
        computeWinningRow(winningRowsAsList, 0, 1, col, row);
        computeWinningRow(winningRowsAsList, 1, 1, col, row);
        computeWinningRow(winningRowsAsList, 1, 0, col, row);
        computeWinningRow(winningRowsAsList, 1, -1, col, row);
      }
    }

    winningRows = Convert2IntArrayArray(winningRowsAsList);

    for (int fieldNumber = 0; fieldNumber < Constants.NUMBEROFFIELDS; fieldNumber++) {
      ArrayList<Integer> nbs = new ArrayList<>();
      for (int j = 0; j < winningRowsAsList.size(); j++) {
        ArrayList<Integer> a = winningRowsAsList.get(j);
        if( winningRowsAsList.get(j).contains(fieldNumber) ){
          nbs.add(j);
        }
      }
      winningRowsForField[fieldNumber] = Convert2IntArray(nbs);
    }
  }

  static void dumpWinningRows() {
    System.out.println("Dump WinningRows ------------------ " + winningRows.length);
    for (int winningRow[] : winningRows) {
      System.out.println(String.format("INFO: %d %d %d %d %d", winningRow[0], winningRow[1], winningRow[2], winningRow[3], winningRow[4]));
    }
  }

  static void dumpNeighbours() {
    System.out.println("Dump Neigbours ------------------");
    for (int fieldNumber = 0; fieldNumber < Constants.NUMBEROFFIELDS; fieldNumber++) {
      StringBuilder s = new StringBuilder(String.format("Feld:%d - ", fieldNumber));
      for (int a : neighbours[fieldNumber]) {
        s.append(String.format("%d ", a));
      }
      System.out.println(s);
    }
  }

};
