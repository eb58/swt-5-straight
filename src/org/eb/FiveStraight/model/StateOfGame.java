package org.eb.FiveStraight.model;

import org.eb.FiveStraight.util.TypeOfWinningRow;
import org.eb.FiveStraight.util.ValuesOfFields;
import org.eb.FiveStraight.util.Constants;
import java.util.ArrayList;

public final class StateOfGame implements Cloneable {

  private static final int MAXVAL = 15000;

  static int nodesInTreeOfGame; // statistics!

  private TypeOfWinningRow grbesetzt[];
  private int granz[];
  private int onesOfPlayer1, onesOfPlayer2;
  private int twosOfPlayer1, twosOfPlayer2;
  private int threesOfPlayer1, threesOfPlayer2;
  private int foursOfPlayer1, foursOfPlayer2;

  public ValuesOfFields[] gamingBoard;
  public ValuesOfFields whosOnTurn;

  public int maxLevel;
  public int numberOfOpenWinningRows;
  public int valueOfPlayingPosition;
  public int numberOfFieldsOccupied;
  public int lastMove;
  public int bestMove;
  public boolean isMill;

  // ////////////////////////////////////////////////////
  StateOfGame() {
    gamingBoard = new ValuesOfFields[Constants.NUMBEROFFIELDS];
    grbesetzt = new TypeOfWinningRow[WinningRows.winningRows.length];
    granz = new int[WinningRows.winningRows.length];
    init(ValuesOfFields.PLAYER1);
  }

  public void init(ValuesOfFields _whoOnTurn) {
    for (int i = 0; i < Constants.NUMBEROFFIELDS; i++) {
      gamingBoard[i] = ValuesOfFields.EMPTY;
    }

    for (int i = 0; i < WinningRows.winningRows.length; i++) {
      grbesetzt[i] = TypeOfWinningRow.EMPTY;
      granz[i] = 0;
    }
    numberOfFieldsOccupied = 0;
    onesOfPlayer1 = 0;
    onesOfPlayer2 = 0;
    twosOfPlayer1 = 0;
    twosOfPlayer2 = 0;
    threesOfPlayer1 = 0;
    threesOfPlayer2 = 0;
    foursOfPlayer1 = 0;
    foursOfPlayer2 = 0;
    isMill = false;
    whosOnTurn = _whoOnTurn;
    numberOfOpenWinningRows = WinningRows.winningRows.length;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    StateOfGame ss = (StateOfGame) super.clone();
    ss.gamingBoard = gamingBoard.clone();
    ss.grbesetzt = grbesetzt.clone();
    ss.granz = granz.clone();
    return (Object) ss;
  }

  void dump() {
    final String symbstr = "X O";
    System.out.println("INFO:>>>>>");
    for (int r = 0; r < Constants.NUMBEROFROWS; r++) {
      String str = "";
      for (int c = 0; c < Constants.NUMBEROFCOLUMNS; c++) {
        int n = gamingBoard[r * Constants.NUMBEROFCOLUMNS + c].ordinal();
        str = str + symbstr.charAt(n);
      }
      System.out.println("INFO:" + str);
    }
    System.out.println("INFO:");
  }

  void makeMove(int z) {
    int gew[] = WinningRows.winningRowsForField[z];

    for (int i = 0; i < gew.length; i++) {
      int j = gew[i];
      switch (grbesetzt[j]) {
        case EMPTY:
          grbesetzt[j] = whosOnTurn == ValuesOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER1 : TypeOfWinningRow.PLAYER2;
          granz[j] = 1;
          if (whosOnTurn == ValuesOfFields.PLAYER2) {
            onesOfPlayer2++;
          } else {
            onesOfPlayer1++;
          }
          break;
        case PLAYER2:
          if (whosOnTurn == ValuesOfFields.PLAYER1) {
            int anz = granz[j];
            grbesetzt[j] = TypeOfWinningRow.NEUTRAL;
            foursOfPlayer2 -= anz == 4 ? 1 : 0;
            threesOfPlayer2 -= anz == 3 ? 1 : 0;
            twosOfPlayer2 -= anz == 2 ? 1 : 0;
            onesOfPlayer2 -= anz == 1 ? 1 : 0;
            numberOfOpenWinningRows--;
          } else {
            int anz = ++granz[j];
            if (anz == 5) {
              isMill = true;
            }
            if (anz == 4) {
              foursOfPlayer2++;
              threesOfPlayer2--;
            }
            if (anz == 3) {
              threesOfPlayer2++;
              twosOfPlayer2--;
            }
            if (anz == 2) {
              twosOfPlayer2++;
              onesOfPlayer2--;
            }
          }
          break;
        case PLAYER1:
          if (whosOnTurn == ValuesOfFields.PLAYER2) {
            int anz = granz[j];
            grbesetzt[j] = TypeOfWinningRow.NEUTRAL;
            foursOfPlayer1 -= anz == 4 ? 1 : 0;
            threesOfPlayer1 -= anz == 3 ? 1 : 0;
            twosOfPlayer1 -= anz == 2 ? 1 : 0;
            onesOfPlayer1 -= anz == 1 ? 1 : 0;
            numberOfOpenWinningRows--;
          } else {
            int anz = ++granz[j];
            if (anz == 5) {
              isMill = true;
            }
            if (anz == 4) {
              foursOfPlayer1++;
              threesOfPlayer1--;
            }
            if (anz == 3) {
              threesOfPlayer1++;
              twosOfPlayer1--;
            }
            if (anz == 2) {
              twosOfPlayer1++;
              onesOfPlayer1--;
            }
          }
          break;
        case NEUTRAL:
          break;
      }
    }
    lastMove = z;
    gamingBoard[z] = whosOnTurn;
    whosOnTurn = whosOnTurn == ValuesOfFields.PLAYER1 ? ValuesOfFields.PLAYER2 : ValuesOfFields.PLAYER1;
    numberOfFieldsOccupied++;
  }

  boolean isActiveField(int fieldNumber) {
    if (gamingBoard[fieldNumber] != ValuesOfFields.EMPTY) {
      return false;
    }

    int row = fieldNumber / Constants.NUMBEROFROWS;
    int col = fieldNumber % Constants.NUMBEROFCOLUMNS;

    if (numberOfFieldsOccupied <= 1
            && Math.abs(row + 1 - Constants.NUMBEROFROWS / 2) <= 1
            && Math.abs(col + 1 - Constants.NUMBEROFCOLUMNS / 2) <= 1) {
      return true;
    }
    if (numberOfFieldsOccupied > 2 * Constants.NUMBEROFFIELDS / 3) { // ab hier sind alle Felder aktiv!
      return true;
    }
    for (int n : WinningRows.neighbours[fieldNumber]) {
      if (gamingBoard[n] != ValuesOfFields.EMPTY) {
        return true;
      }
    }
    return false;
  }

  static boolean isInVec[] = new boolean[Constants.NUMBEROFFIELDS];
  static boolean isActiveVec[] = new boolean[Constants.NUMBEROFFIELDS];

  void initAddMoves() {
    for (int z = 0; z < Constants.NUMBEROFFIELDS; z++) {
      isInVec[z] = false;
    }
    for (int z = 0; z < Constants.NUMBEROFFIELDS; z++) {
      isActiveVec[z] = isActiveField(z);
    }
  }

  int addMoves(ArrayList<Integer> zugvec, int anz, TypeOfWinningRow typ) {
    for (int i = 0; i < WinningRows.winningRows.length; i++) {
      if (granz[i] == anz && grbesetzt[i] == typ) {
        int gr[] = WinningRows.winningRows[i];
        for (int z : gr) {
          if (isActiveVec[z] && !isInVec[z]) {
            isInVec[z] = true;
            zugvec.add(z);
          }
        }
      }
    }
    return zugvec.size();
  }

  void generatePossibleMove(ArrayList<Integer> zugvec) {
    initAddMoves();
    TypeOfWinningRow gw1 = whosOnTurn == ValuesOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER1 : TypeOfWinningRow.PLAYER2;
    TypeOfWinningRow gw2 = whosOnTurn == ValuesOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER2 : TypeOfWinningRow.PLAYER1;

    if (addMoves(zugvec, 4, gw1) > 0) {
      return;
    }
    if (addMoves(zugvec, 4, gw2) > 0) {
      return;
    }
    addMoves(zugvec, 3, gw1);
    addMoves(zugvec, 3, gw2);
    addMoves(zugvec, 2, gw1);
    addMoves(zugvec, 2, gw2);
    if (zugvec.isEmpty()) {
      addMoves(zugvec, 1, gw1);
    }
    if (zugvec.isEmpty()) {
      addMoves(zugvec, 1, gw2);
    }
    if (zugvec.isEmpty()) {
      addMoves(zugvec, 0, TypeOfWinningRow.EMPTY);
    }
  }

  void generateForcedMoves(ArrayList<Integer> zugvec) {
    // Berechne alle zwingenden Zuege
    initAddMoves();
    TypeOfWinningRow gw1 = whosOnTurn == ValuesOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER1 : TypeOfWinningRow.PLAYER2;
    TypeOfWinningRow gw2 = whosOnTurn == ValuesOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER2 : TypeOfWinningRow.PLAYER1;

    if (addMoves(zugvec, 4, gw1) > 0) {
      return;
    }
    addMoves(zugvec, 4, gw2);
  }

  int wertDerStellungFuerPlayer1() {
    return foursOfPlayer1 > 1 ? 128 : 0 - foursOfPlayer2 > 1 ? 64 : 0 + 16 * (threesOfPlayer1 - threesOfPlayer2) + 4 * (twosOfPlayer1 - twosOfPlayer2) + (onesOfPlayer1 - onesOfPlayer2);
  }

  int alphaBeta(int lev, int alpha, int beta) {
    // Liefert Wert der Stellung ss aus Sicht der Seite, die am Zug ist!
    // Setzt außerdem den Wert ss.zug als den besten Zug fuer diese Stellung!

    nodesInTreeOfGame++;

    if (isMill) {
      FiveStraightHashTab.insert(this, -MAXVAL + lev, lev);
      return -MAXVAL + lev;
    }

    if (numberOfFieldsOccupied > Constants.NUMBEROFFIELDS || numberOfOpenWinningRows == 0) {
      return 0; // Remis!
    }
    int val = FiveStraightHashTab.getVal(this, lev);
    if (val != -100000) {
      return val;
    }

    ArrayList<Integer> zugvec = new ArrayList<>();
    if (lev >= maxLevel && lev <= maxLevel + 8) {
      generateForcedMoves(zugvec);
    } else {
      generatePossibleMove(zugvec);
    }

    if (zugvec.isEmpty()) {
      int v = wertDerStellungFuerPlayer1();
      FiveStraightHashTab.insert(this, this.whosOnTurn == ValuesOfFields.PLAYER1 ? v : -v, lev);
      return this.whosOnTurn == ValuesOfFields.PLAYER1 ? v : -v;
    }

    ArrayList<StateOfGame> lssvec = new ArrayList<>();
    try {
      for (int z : zugvec) {// looking for mills
        StateOfGame lss = (StateOfGame) clone();
        lss.makeMove(z);
        if (lss.isMill) {
          if (lev == 0) {
            bestMove = z;
            valueOfPlayingPosition = MAXVAL - lev;
          }
          FiveStraightHashTab.insert(this, MAXVAL - lev, lev);
          return MAXVAL - lev;
        }
        lssvec.add(lss);
      }
    } catch (CloneNotSupportedException ex) {
    }

    int max = -MAXVAL + lev; // wir gehen vom schlimmsten aus;
    for (StateOfGame lss : lssvec) {
      int w = -lss.alphaBeta(lev + 1, -beta, -alpha);
      if (w > max) { // neuer bester Wert gefunden
        max = w;
        if (lev == 0) {
          bestMove = lss.lastMove;
          valueOfPlayingPosition = max;
        }
        if (w >= beta) {
          // FiveStraightHashTab.insert(this, w, lev);
          return w;
        }
        if (w > alpha) {
          alpha = w;// Verbesserter alpha Wert
        }
      }
    }

    FiveStraightHashTab.insert(this, max, lev);
    return max;
  }

  int bestMove() {
    alphaBeta(0, -MAXVAL, MAXVAL);
    return this.bestMove;
  }

  public String getStatusString() {
    return String.format("Mein Zug:%d GWOffen:%d Val:%d c:(%d,%d,%d,%d) s:(%d,%d,%d,%d)",
            lastMove,
            numberOfOpenWinningRows,
            valueOfPlayingPosition,
            foursOfPlayer1, threesOfPlayer1, twosOfPlayer1, onesOfPlayer1,
            foursOfPlayer2, threesOfPlayer2, twosOfPlayer2, onesOfPlayer2
    );
  }
}
