package org.eb.FiveStraight.model;

import org.eb.FiveStraight.util.ValuesOfFields;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

// ///////////////////////////////////////////////////////////////////////////////////////////////
public class FiveStraightModel {

  private ArrayList<Integer> moves;	// Spielverlauf
  public boolean player1Begins;
  public StateOfGame ss;
  
  // ///////////////////////////////////////////////////////
  public FiveStraightModel() {
    WinningRows.initWinningRows();
    WinningRows.initNeighbours();
    ss = new StateOfGame();
    moves = new ArrayList<>();
    player1Begins = true;
  }

  // //////////////////////////////////////////////////////////////// /
  public void makeMove(int z) {
    ss.makeMove(z);
    moves.add(z);
  }

  public void moveOfComputer() {
    long start = System.currentTimeMillis();
    StateOfGame.nodesInTreeOfGame = 0;
    //FiveStraightHashTab.iniths();
    makeMove(ss.bestMove());

    { // compute time and print time and statistics
      long end = System.currentTimeMillis();
      long tdiff = (end - start) + 1;

      String s = String.format("INFO:Tiefe:%d Knoten:%d Zug:%d Val:%d MilliSec:%d Knoten/Sec:%d",
              ss.maxLevel,
              StateOfGame.nodesInTreeOfGame,
              ss.bestMove,
              ss.valueOfPlayingPosition,
              tdiff,
              StateOfGame.nodesInTreeOfGame * 1000 / tdiff
      );
      System.out.println(s);
    }
  }

  // //////////////////////////////////////////////////////// /
  public void undoMove() {
    if (moves.size() <= 1) {
      return;
    }

    List<Integer>oldMoves =  moves.subList(0, moves.size() - 2);
    
    ss.init(player1Begins ? ValuesOfFields.PLAYER1 : ValuesOfFields.PLAYER2);
    moves = new ArrayList<>();
    
    for (int move : oldMoves) {
      makeMove(move);
    }
  }

  public int saveGame(final String fname) {
    String s = player1Begins ? "C " : "M ";
    for (int i = 0; i < ss.numberOfFieldsOccupied; i++) {
      String z = String.format("%d ", moves.get(i));
      s += z;
    }
    s = s.trim();

    try (PrintWriter pwr = new PrintWriter(new FileWriter(fname))) {
      pwr.println(s);
    } catch (IOException e) {
      return 0;
    }
    return 1;
  }

  public int loadGame(final String fname) {
    String s = "";
    try (LineNumberReader lnr = new LineNumberReader(new FileReader(fname))) {
      s = lnr.readLine();
    } catch (Exception e) {
      return 0;
    }
    String arr[] = s.split(" ");

    player1Begins = "C".equals(arr[0]);
    ss.init(player1Begins ? ValuesOfFields.PLAYER1 : ValuesOfFields.PLAYER2);
    moves = new ArrayList<>();

    for (int i = 1; i < arr.length; i++) {
      int z = Integer.parseInt(arr[i]);
      makeMove(z);
    }
    return 1;
  }

  public void newGame() {
    ss.init(player1Begins ? ValuesOfFields.PLAYER1 : ValuesOfFields.PLAYER2);
    moves = new ArrayList<>();
    if (player1Begins) {
      moveOfComputer();
    }
  }
}
