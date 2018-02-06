package org.eb.FiveStraight.model;


import org.eb.FiveStraight.util.ValuesOfFields;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;

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
      StateOfGame.nodesInTreeOfGame = 0;
      long start = System.currentTimeMillis();
      //FiveStraightHashTab.iniths();
      makeMove(ss.bestMove());
      long end = System.currentTimeMillis();
      long tdiff = (end - start) + 1;
      
      String s = String.format("INFO:Tiefe:%d Knoten:%d Zug:%d Val:%d MilliSec:%d Knoten/Sec:%d", ss.maxLevel,
              StateOfGame.nodesInTreeOfGame, ss.bestMove, ss.valueOfPlayingPosition, tdiff, StateOfGame.nodesInTreeOfGame * 1000 / tdiff);
      System.out.println(s);
  }

  // //////////////////////////////////////////////////////// /
  public void undoMove() {
    if (moves.size() <= 1) {
      return;
    }

    ss.init(player1Begins ? ValuesOfFields.PLAYER1 : ValuesOfFields.PLAYER2);

    ArrayList<Integer> oldzuege = new ArrayList<>();
    for (Integer I : moves) {
      oldzuege.add(I);
    }
    moves = new ArrayList<>();
    int n = oldzuege.size() - 2;
    for (int i = 0; i < n; i++) {
      makeMove(oldzuege.get(i));
    }
  }

  public int saveGame(final String fname) {
    String s = player1Begins ? "C " : "M ";
    for (int i = 0; i < ss.numberOfFieldsOccupied; i++) {
      String z = String.format("%d ", moves.get(i));
      s += z;
    }
    s = s.trim();

    try {
      PrintWriter pwr = new PrintWriter(new FileWriter(fname));
      pwr.println(s);
      pwr.close();
    } catch (IOException e) {
    }
    return 1;
  }

   public int loadGame(final String fname) {
    String s = "";
    try {
      LineNumberReader lnr = new LineNumberReader(new FileReader(fname));
      s = lnr.readLine();
      lnr.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
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
