package org.eb.FiveStraight.model;

import org.eb.FiveStraight.util.TypeOfWinningRow;
import org.eb.FiveStraight.util.TypeOfFields;
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
  private int lastMove;

  public TypeOfFields[] gamingBoard;
  public TypeOfFields whosOnTurn;

  public int maxLevel;
  public int numberOfOpenWinningRows;
  public int valueOfPlayingPosition;
  public int numberOfFieldsOccupied;
  public int bestMove;
  public boolean isMill;

  // ////////////////////////////////////////////////////
  StateOfGame() {
    gamingBoard = new TypeOfFields[Constants.NUMBEROFFIELDS];
    grbesetzt = new TypeOfWinningRow[StaticGameData.winningRows.size()];
    granz = new int[StaticGameData.winningRows.size()];
    init(TypeOfFields.PLAYER1);
  }

  public void init(TypeOfFields _whoOnTurn) {
    for (int i = 0; i < Constants.NUMBEROFFIELDS; i++) {
      gamingBoard[i] = TypeOfFields.EMPTY;
    }

    for (int i = 0; i < StaticGameData.winningRows.size(); i++) {
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
    numberOfOpenWinningRows = StaticGameData.winningRows.size();
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
    ArrayList<Integer> gew = StaticGameData.winningRowsForField.get(z);

    for (Integer j : gew) {
      switch (grbesetzt[j]) {
        case EMPTY:
          grbesetzt[j] = whosOnTurn == TypeOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER1 : TypeOfWinningRow.PLAYER2;
          granz[j] = 1;
          if (whosOnTurn == TypeOfFields.PLAYER2) {
            onesOfPlayer2++;
          } else {
            onesOfPlayer1++;
          }
          break;
        case PLAYER2:
          if (whosOnTurn == TypeOfFields.PLAYER1) {
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
          if (whosOnTurn == TypeOfFields.PLAYER2) {
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
    whosOnTurn = whosOnTurn == TypeOfFields.PLAYER1 ? TypeOfFields.PLAYER2 : TypeOfFields.PLAYER1;
    numberOfFieldsOccupied++;
  }

  boolean isActiveField(int fieldNumber) {
    if (gamingBoard[fieldNumber] != TypeOfFields.EMPTY) {
      return false;
    }

    int row = fieldNumber / Constants.NUMBEROFROWS;
    int col = fieldNumber % Constants.NUMBEROFCOLUMNS;

    if (numberOfFieldsOccupied <= 1
            && Math.abs(row + 1 - Constants.NUMBEROFROWS / 2) <= 1
            && Math.abs(col + 1 - Constants.NUMBEROFCOLUMNS / 2) <= 1) {
      return true;
    }
    if (numberOfFieldsOccupied > 2 * Constants.NUMBEROFFIELDS / 3) {
      // all fields are active if so many fields are occupied!
      return true;
    }
    for (Integer n : StaticGameData.neighbours.get(fieldNumber)) {
      if (gamingBoard[n] != TypeOfFields.EMPTY) {
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

  int addMoves(ArrayList<Integer> moves, int anz, TypeOfWinningRow typ) {
    for (int i = 0, len = StaticGameData.winningRows.size(); i < len; i++) {
      if (granz[i] == anz && grbesetzt[i] == typ) {
        for (int move : StaticGameData.winningRows.get(i)) {
          if (isActiveVec[move] && !isInVec[move]) {
            isInVec[move] = true;
            moves.add(move);
          }
        }
      }
    }
    return moves.size();
  }

  void generatePossibleMove(ArrayList<Integer> moves) {
    initAddMoves();
    TypeOfWinningRow gw1 = whosOnTurn == TypeOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER1 : TypeOfWinningRow.PLAYER2;
    TypeOfWinningRow gw2 = whosOnTurn == TypeOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER2 : TypeOfWinningRow.PLAYER1;

    if (addMoves(moves, 4, gw1) > 0) {
      return;
    }
    if (addMoves(moves, 4, gw2) > 0) {
      return;
    }
    addMoves(moves, 3, gw1);
    addMoves(moves, 3, gw2);
    addMoves(moves, 2, gw1);
    addMoves(moves, 2, gw2);
    if (moves.isEmpty()) {
      addMoves(moves, 1, gw1);
    }
    if (moves.isEmpty()) {
      addMoves(moves, 1, gw2);
    }
    if (moves.isEmpty()) {
      addMoves(moves, 0, TypeOfWinningRow.EMPTY);
    }
  }

  void generateForcedMoves(ArrayList<Integer> moves) {
    // Berechne alle zwingenden Zuege
    initAddMoves();
    TypeOfWinningRow gw1 = whosOnTurn == TypeOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER1 : TypeOfWinningRow.PLAYER2;
    TypeOfWinningRow gw2 = whosOnTurn == TypeOfFields.PLAYER1 ? TypeOfWinningRow.PLAYER2 : TypeOfWinningRow.PLAYER1;

    if (addMoves(moves, 4, gw1) > 0) {
      return;
    }
    addMoves(moves, 4, gw2);
  }

  int valueForPlayer1() {
    return (foursOfPlayer1 > 1 ? 128 : 0) - (foursOfPlayer2 > 1 ? 64 : 0) + 16 * (threesOfPlayer1 - threesOfPlayer2) + 4 * (twosOfPlayer1 - twosOfPlayer2) + (onesOfPlayer1 - onesOfPlayer2);
  }

  int alphaBeta(int lev, int alpha, int beta) {   // computes the value of game in point of view of player who turn it is

    nodesInTreeOfGame++;

    if (isMill) {
      return -MAXVAL + lev;
    }

    if (numberOfFieldsOccupied > Constants.NUMBEROFFIELDS || numberOfOpenWinningRows == 0) {
      return 0; // Remis!
    }

    ArrayList<Integer> moves = new ArrayList<>();
    if (lev >= maxLevel && lev <= maxLevel + 8) {
      generateForcedMoves(moves);
    } else {
      generatePossibleMove(moves);
    }

    if (moves.isEmpty()) {
      int v = valueForPlayer1();
      return this.whosOnTurn == TypeOfFields.PLAYER1 ? v : -v;
    }

    ArrayList<StateOfGame> lssvec = new ArrayList<>();
    try {
      for (int move : moves) {// looking for mills
        StateOfGame lss = (StateOfGame) clone();
        lss.makeMove(move);
        if (lss.isMill) {
          if (lev == 0) {
            bestMove = move;
            valueOfPlayingPosition = MAXVAL - lev;
          }
          return MAXVAL - lev;
        }
        lssvec.add(lss);
      }
    } catch (CloneNotSupportedException ex) {
    }

    int max = -MAXVAL + lev; // we are assuming the worst case
    for (StateOfGame lss : lssvec) {
      int value = -lss.alphaBeta(lev + 1, -beta, -alpha);
      if (value > max) { // better value found
        max = value;
        if (lev == 0) {
          bestMove = lss.lastMove;
          valueOfPlayingPosition = max;
        }
        if (value >= beta) {
          return value;
        }
        if (value > alpha) {
          alpha = value;// better alpha value
        }
      }
    }
    return max;
  }

  int bestMove() {
    alphaBeta(0, -MAXVAL, MAXVAL);
    return this.bestMove;
  }

  public String getStatusString() {
    return String.format("Mein Zug:%d Offene Gewinnreihen:%d Value:%d c:(%d,%d,%d,%d) s:(%d,%d,%d,%d)",
            lastMove,
            numberOfOpenWinningRows,
            valueOfPlayingPosition,
            foursOfPlayer1, threesOfPlayer1, twosOfPlayer1, onesOfPlayer1,
            foursOfPlayer2, threesOfPlayer2, twosOfPlayer2, onesOfPlayer2
    );
  }
}
