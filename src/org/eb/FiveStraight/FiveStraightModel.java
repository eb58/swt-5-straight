package org.eb.FiveStraight;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;


// ///////////////////////////////////////////////////////////////////////////////////////////////

public class FiveStraightModel {

	SpielStand	ss;
	ArrayList<Integer>	zuege;	// Spielverlauf
	int			MaxLevel;
	boolean		CompBeginnt;

	// ///////////////////////////////////////////////////////
	FiveStraightModel() {
		GewinnReihen.initgewinnreihen();
		GewinnReihen.initneighbours();
		ss = new SpielStand();
		zuege =  new ArrayList<>();
		MaxLevel = 4;
		CompBeginnt = true;
	}

	// //////////////////////////////////////////////////////////////// /

	void ziehen( int z){
		ss.ziehen(z);
		zuege.add(z);
	}

	void computerzug() {
		SpielStand.knotenInSpielbaum = 0;
		SpielStand.MaxLevel = MaxLevel;
		long start = System.currentTimeMillis();
		FiveStraightHashTab.iniths();
		ziehen(ss.BesterZug());
		long end = System.currentTimeMillis();
		long tdiff = (end - start) + 1;

		String s = String.format("INFO:Tiefe:%d Knoten:%d Zug:%d Val:%d MilliSec:%d Knoten/Sec:%d", MaxLevel,
				SpielStand.knotenInSpielbaum, ss.besterzug, ss.wertDerStellung, tdiff, SpielStand.knotenInSpielbaum * 1000 / tdiff);
		System.out.println(s);
	}

	// //////////////////////////////////////////////////////// /
	
	void ZugZurueck() {
		if( zuege.size() <=1  )
			return;
		
		ss.init(CompBeginnt ? FeldWerte.COMP : FeldWerte.SPIELER);

		ArrayList<Integer> oldzuege = new ArrayList<>();
		for( Integer I:zuege ) oldzuege.add(I);
		zuege = new ArrayList<>();
		int n = oldzuege.size() - 2;
		for (int i = 0; i < n; i++)
			ziehen(oldzuege.get(i));
	}

	int SaveGame(final String fname) {
		String s = CompBeginnt ? "C " : "M ";
		for (int i = 0; i < ss.anzahlFelderBesetzt; i++) {
			String z = String.format("%d ", zuege.get(i));
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

	int LoadGame(final String fname) {
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

		CompBeginnt = "C".equals(arr[0]);
		ss.init(CompBeginnt ? FeldWerte.COMP : FeldWerte.SPIELER);
		zuege = new ArrayList<>();
		
		for (int i = 1; i < arr.length; i++) {
			int z = Integer.parseInt(arr[i]);
			ziehen(z);
		}
		return 1;
	}
	
	void NeuesSpiel(){
		ss.init(CompBeginnt ? FeldWerte.COMP : FeldWerte.SPIELER);
		zuege = new ArrayList<>();
		if (CompBeginnt) 
			computerzug();
	}
}
