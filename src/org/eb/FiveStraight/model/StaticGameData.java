package org.eb.FiveStraight.model;

import java.util.ArrayList;
import org.eb.FiveStraight.util.Constants;

public class StaticGameData {

  static ArrayList<ArrayList<Integer>> neighbours = computeNeighbours();
  static ArrayList<ArrayList<Integer>> winningRows = computeWinningRows();
  static ArrayList<ArrayList<Integer>> winningRowsForField = computeWinningRowsForFields();

  static void dump() {
    dumpNeighbours();
    dumpWinningRows();
  }

  private static void computeWinningRow(ArrayList<ArrayList<Integer>> winnningRows, int x, int y, int row, int col) {
    ArrayList<Integer> winningrow = new ArrayList<>();

    while (winningrow.size() < 5 && row >= 0 && row < Constants.NUMBEROFCOLUMNS && col >= 0 && col < Constants.NUMBEROFROWS) {
      winningrow.add(col * Constants.NUMBEROFCOLUMNS + row);
      row += x;
      col += y;
    }

    if (winningrow.size() == 5) {
      winnningRows.add(winningrow);
    }

  }

  private static ArrayList<ArrayList<Integer>> computeWinningRows() {
    ArrayList<ArrayList<Integer>> winningRowsAsList = new ArrayList<>();
    for (int col = 0; col < Constants.NUMBEROFCOLUMNS; col++) {
      for (int row = 0; row < Constants.NUMBEROFROWS; row++) {
        computeWinningRow(winningRowsAsList, 0, 1, col, row);
        computeWinningRow(winningRowsAsList, 1, 1, col, row);
        computeWinningRow(winningRowsAsList, 1, 0, col, row);
        computeWinningRow(winningRowsAsList, 1, -1, col, row);
      }
    }
    return winningRowsAsList;
  }

  private static ArrayList<ArrayList<Integer>> computeWinningRowsForFields() {
    ArrayList<ArrayList<Integer>> winRowsForField = new ArrayList<>();//int[Constants.NUMBEROFFIELDS][];
    for (int fieldNumber = 0; fieldNumber < Constants.NUMBEROFFIELDS; fieldNumber++) {
      ArrayList<Integer> wrs = new ArrayList<>();
      for (int j = 0; j < winningRows.size(); j++) {
        if (winningRows.get(j).contains(fieldNumber)) {
          wrs.add(j);
        }
      }
      winRowsForField.add(wrs);
    }
    return winRowsForField;
  }

  private static ArrayList<ArrayList<Integer>> computeNeighbours() {
    ArrayList<ArrayList<Integer>> res = new ArrayList<>(Constants.NUMBEROFFIELDS);

    for (int row = 0; row < Constants.NUMBEROFROWS; row++) {
      for (int col = 0; col < Constants.NUMBEROFCOLUMNS; col++) {
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
        res.add(nbs);
      }
    }
    return res;
  }

  static void dumpWinningRows() {
    System.out.println("Dump WinningRows ------------------ " + winningRows.size());
    for (ArrayList winningRow : winningRows) {
      System.out.println(String.format("INFO: %s", winningRow));
    }
  }

  static void dumpNeighbours() {
    System.out.println("Dump Neigbours ------------------");
    for (int fieldNumber = 0; fieldNumber < Constants.NUMBEROFFIELDS; fieldNumber++) {
      StringBuilder s = new StringBuilder(String.format("Feld:%d - ", fieldNumber));
      for (Integer a : neighbours.get(fieldNumber)) {
        s.append(String.format("%d ", a));
      }
      System.out.println(s);
    }
  }

}