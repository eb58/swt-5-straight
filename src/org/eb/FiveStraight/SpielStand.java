package org.eb.FiveStraight;

import java.util.ArrayList;

public class SpielStand implements Cloneable {
	static final int	MAXVAL		= 15000;

	static int			knotenInSpielbaum;								// Statistik!
	static int			MaxLevel;

	FeldWerte			spielfeld[]	= new FeldWerte[Globals.SANZZANZ];	// Spielfeld
	GewinnReihenWerte	grbesetzt[];
	int					granz[];
	boolean				muehle;
	int					ceins, seins;
	int					czwei, szwei;
	int					cdrei, sdrei;
	int					cvier, svier;
	int					groffen;
	int					anzahlFelderBesetzt;
	int					wertDerStellung;
	int					letzterzug;
	int					besterzug;
	FeldWerte			amzug;

	// ////////////////////////////////////////////////////

	SpielStand() {
		grbesetzt = new GewinnReihenWerte[GewinnReihen.gr.length];
		granz = new int[GewinnReihen.gr.length];
		init(FeldWerte.COMP);
	}

	void init(FeldWerte anf) {
		for (int i = 0; i < Globals.SANZZANZ; i++)
			spielfeld[i] = FeldWerte.FREI;

		for (int i = 0; i < GewinnReihen.gr.length; i++) {
			grbesetzt[i] = GewinnReihenWerte.FREI;
			granz[i] = 0;
		}
		anzahlFelderBesetzt = 0;
		ceins = 0;
		seins = 0;
		czwei = 0;
		szwei = 0;
		cdrei = 0;
		sdrei = 0;
		cvier = 0;
		svier = 0;
		muehle = false;
		amzug = anf;
		groffen = GewinnReihen.gr.length;
	}

  @Override
	public String toString() {
		StringBuilder str = new StringBuilder("");
    for (FeldWerte spielfeld1 : spielfeld) {
      switch (spielfeld1) {
        case SPIELER:
          str.append("O");
          break;
        case FREI:
          str.append(" ");
          break;
        case COMP:
          str.append("X");
          break;
      }
    }
		return str.toString();
	}

	public String toStringRev() {
		StringBuilder str = new StringBuilder("");
    for (FeldWerte spielfeld1 : spielfeld) {
      switch (spielfeld1) {
        case SPIELER:
          str.append("X");
          break;
        case FREI:
          str.append(" ");
          break;
        case COMP:
          str.append("O");
          break;
      }
    }
		return str.toString();
	}

  @Override
	public Object clone() throws CloneNotSupportedException {
		try {
			SpielStand ss = (SpielStand) super.clone();
			ss.spielfeld = spielfeld.clone();
			ss.grbesetzt = grbesetzt.clone();
			ss.granz = granz.clone();
			return (Object) ss;
		} catch (CloneNotSupportedException e) { // Dire trouble!!!
			throw new InternalError("But we are Cloneable!!!");
		}
	}

	void dump() {
		final String symbstr = "X O";
		System.out.println("INFO:>>>>>");
		for (int r = 0; r < Globals.ZANZ; r++) {
			String str = "";
			for (int c = 0; c < Globals.SANZ; c++) {
				int n = spielfeld[r * Globals.SANZ + c].ordinal();
				str = str + symbstr.charAt(n);
			}
			System.out.println("INFO:" + str);
		}
		System.out.println("INFO:");
	}

	void ziehen(int z) {
		int gew[] = GewinnReihen.grs[z];

		for (int i = 0; i < gew.length; i++) {
			int j = gew[i];
			switch (grbesetzt[j]) {
			case FREI:
				grbesetzt[j] = amzug == FeldWerte.COMP ? GewinnReihenWerte.COMP : GewinnReihenWerte.SPIELER;
				granz[j] = 1;
				if (amzug == FeldWerte.SPIELER)
					seins++;
				else
					ceins++;
				break;
			case SPIELER:
				if (amzug == FeldWerte.COMP) {
					int anz = granz[j];
					grbesetzt[j] = GewinnReihenWerte.NEUTRAL;
					svier -= anz == 4 ? 1 : 0;
					sdrei -= anz == 3 ? 1 : 0;
					szwei -= anz == 2 ? 1 : 0;
					seins -= anz == 1 ? 1 : 0;
					groffen--;
				} else {
					int anz = ++granz[j];
					if (anz == 5)
						muehle = true;
					if (anz == 4) {
						svier++;
						sdrei--;
					}
					if (anz == 3) {
						sdrei++;
						szwei--;
					}
					if (anz == 2) {
						szwei++;
						seins--;
					}
				}
				break;
			case COMP:
				if (amzug == FeldWerte.SPIELER) {
					int anz = granz[j];
					grbesetzt[j] = GewinnReihenWerte.NEUTRAL;
					cvier -= anz == 4 ? 1 : 0;
					cdrei -= anz == 3 ? 1 : 0;
					czwei -= anz == 2 ? 1 : 0;
					ceins -= anz == 1 ? 1 : 0;
					groffen--;
				} else {
					int anz = ++granz[j];
					if (anz == 5)
						muehle = true;
					if (anz == 4) {
						cvier++;
						cdrei--;
					}
					if (anz == 3) {
						cdrei++;
						czwei--;
					}
					if (anz == 2) {
						czwei++;
						ceins--;
					}
				}
				break;
			case NEUTRAL:
				break;
			}
		}
		letzterzug = z;
		spielfeld[z] = amzug;
		amzug = amzug == FeldWerte.COMP ? FeldWerte.SPIELER : FeldWerte.COMP;
		anzahlFelderBesetzt++;
	}

	static final int	N	= 2 * Globals.SANZZANZ / 3;
	boolean IsAktivField(int z) {
		if (spielfeld[z] != FeldWerte.FREI)
			return false;

		int r = z / Globals.ZANZ;
		int sp = z % Globals.SANZ;

		if (anzahlFelderBesetzt <= 1 && Math.abs(r + 1 - Globals.ZANZ / 2) <= 1
				&& Math.abs(sp + 1 - Globals.SANZ / 2) <= 1)
			return true;
		if (anzahlFelderBesetzt > N)
			return true; // ab hier sind alle Felder aktiv!

		for (int n : GewinnReihen.neighbours[z])
			if (spielfeld[n] != FeldWerte.FREI)
				return true;
		return false;
	}

	static boolean	IsInVec[]	= new boolean[Globals.SANZZANZ];
	static boolean	IsActive[]	= new boolean[Globals.SANZZANZ];

	void InitAddZuege() {
		for (int z = 0; z < Globals.SANZZANZ; z++)
			IsInVec[z] = false;
		for (int z = 0; z < Globals.SANZZANZ; z++)
			IsActive[z] = IsAktivField(z);
	}

	int AddZuege(ArrayList<Integer> zugvec, int anz, GewinnReihenWerte typ) {
		for (int i = 0; i < GewinnReihen.gr.length; i++)
			if (granz[i] == anz && grbesetzt[i] == typ) {
				int gr[] = GewinnReihen.gr[i];
				for (int z : gr) {
					if (IsActive[z] && !IsInVec[z]) {
						IsInVec[z] = true;
						zugvec.add(z);
					}
				}
			}
		return zugvec.size();
	}

	void GeneriereZuege(ArrayList<Integer> zugvec) {
		// Berechne alle Zuege
		InitAddZuege();
		GewinnReihenWerte gw1 = amzug == FeldWerte.COMP ? GewinnReihenWerte.COMP : GewinnReihenWerte.SPIELER;
		GewinnReihenWerte gw2 = amzug == FeldWerte.COMP ? GewinnReihenWerte.SPIELER : GewinnReihenWerte.COMP;

		if (AddZuege(zugvec, 4, gw1) > 0)
			return;
		if (AddZuege(zugvec, 4, gw2) > 0)
			return;
		AddZuege(zugvec, 3, gw1);
		AddZuege(zugvec, 3, gw2);
		AddZuege(zugvec, 2, gw1);
		AddZuege(zugvec, 2, gw2);
		if (zugvec.isEmpty())
			AddZuege(zugvec, 1, gw1);
		if (zugvec.isEmpty())
			AddZuege(zugvec, 1, gw2);
		if (zugvec.isEmpty())
			AddZuege(zugvec, 0, GewinnReihenWerte.FREI);
	}

	void GeneriereErzwungeneZuege(ArrayList<Integer> zugvec) {
		// Berechne alle zwingenden Zuege
		InitAddZuege();
		GewinnReihenWerte gw1 = amzug == FeldWerte.COMP ? GewinnReihenWerte.COMP : GewinnReihenWerte.SPIELER;
		GewinnReihenWerte gw2 = amzug == FeldWerte.COMP ? GewinnReihenWerte.SPIELER : GewinnReihenWerte.COMP;

		if (AddZuege(zugvec, 4, gw1) > 0)
			return;
		AddZuege(zugvec, 4, gw2);
	}

	int WertDerStellungFuerComp() {
		// Wert der Stellung aus Sicht des Computers
		return cvier > 1 ? 128 : 0 - svier > 1 ? 64 : 0 + 16 * (cdrei - sdrei) + 4 * (czwei - szwei) + (ceins - seins);
	}

	// class MyComparator implements Comparator<SpielStand> {
	// public int compare(SpielStand o1, SpielStand o2) {
	// if (o1.wertDerStellung == o2.wertDerStellung)
	// return 0;
	// return o1.wertDerStellung < o2.wertDerStellung ? 1 : -1;
	// }
	// }
	//

	int AlphaBeta(int lev, int alpha, int beta) throws CloneNotSupportedException {
		// Liefert Wert der Stellung ss aus Sicht der Seite, die am Zug ist!
		// Setzt au�erdem den Wert ss.zug als den besten Zug f�r diese Stellung!

		knotenInSpielbaum++;

		if (muehle) {
			FiveStraightHashTab.insert(this, -MAXVAL + lev, lev);
			return -MAXVAL + lev;
		}

		if (anzahlFelderBesetzt > Globals.SANZZANZ || groffen == 0)
			return 0; // Remis!

		int val = FiveStraightHashTab.getVal(this, lev);
		if (val != -100000)
			return val;

		ArrayList<Integer> zugvec = new ArrayList<>();
		if (lev >= MaxLevel && lev <= MaxLevel + 8)
			GeneriereErzwungeneZuege(zugvec);
		else
			GeneriereZuege(zugvec);

		if (zugvec.isEmpty()) {
			int v = WertDerStellungFuerComp();
			FiveStraightHashTab.insert(this, this.amzug == FeldWerte.COMP ? v : -v, lev);
			return this.amzug == FeldWerte.COMP ? v : -v;
		}

		ArrayList<SpielStand> lssvec = new ArrayList<>();
		for (int z : zugvec) {// Untersuche alle Zuege auf M�hle
			SpielStand lss = (SpielStand) clone();
			lss.ziehen(z);
			if (lss.muehle) {
				if (lev == 0) {
					besterzug = z;
					wertDerStellung = MAXVAL - lev;
				}
				FiveStraightHashTab.insert(this, MAXVAL - lev, lev);
				return MAXVAL - lev;
			}
			lssvec.add(lss);
		}

		int max = -MAXVAL + lev; // wir gehen vom schlimmsten aus;
		for (SpielStand lss : lssvec) {
			int w = -lss.AlphaBeta(lev + 1, -beta, -alpha);
			if (w > max) { // neuer bester Wert gefunden
				max = w;
				if (lev == 0) {
					besterzug = lss.letzterzug;
					wertDerStellung = max;
				}
				if (w >= beta) {
					// FiveStraightHashTab.insert(this, w, lev);
					return w;
				}
				if (w > alpha)
					alpha = w;// Verbesserter alpha Wert
			}
		}
		FiveStraightHashTab.insert(this, max, lev);
		return max;
	}

	int BesterZug() throws CloneNotSupportedException {
		AlphaBeta(0, -MAXVAL, MAXVAL);
		return this.besterzug;
	}
}
