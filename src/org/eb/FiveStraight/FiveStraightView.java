package org.eb.FiveStraight;

import java.util.Arrays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.MessageBox;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;

public class FiveStraightView extends ApplicationWindow {

  FiveStraightModel FSM;
  StatusLineManager sm;
  Canvas canvas;
  Shell sh;
  Display display;
  Cursor waitCursor;

  // /////////////////////////////////////////////////////////////////////////////////////
  ActionNeuesSpiel act_NeuesSpiel = new ActionNeuesSpiel();
  ActionBeenden act_Beenden = new ActionBeenden();
  ActionZugZurueckNehmen act_ZugZurueckNehmen = new ActionZugZurueckNehmen();

  // /////////////////////////////////////////////////////////////////////////////////////
  void MessageBox(String title, String msg) {
    MessageBox mb = new MessageBox(sh, SWT.OK);
    mb.setText(title);
    mb.setMessage(msg);
    mb.open();
  }

  class SpielFeldPaintListener implements PaintListener {

    @Override
    public void paintControl(PaintEvent e) {
      int w = canvas.getClientArea().width / Globals.SANZ;
      int h = canvas.getClientArea().height / Globals.ZANZ;

      for (int i = 0; i < Globals.SANZZANZ; i++) {
        switch (FSM.ss.spielfeld[i]) {
          case SPIELER:
            e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
            break;
          case FREI:
            e.gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
            break;
          case COMP:
            e.gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
            break;
        }
        int col = i / Globals.SANZ;
        int row = i % Globals.ZANZ;
        e.gc.fillOval(row * w + 3, col * h + 3, w - 3, h - 3);
        int x = row * w + 4 * w / Globals.SANZ;
        int y = col * h + 4 * h / Globals.ZANZ;
        e.gc.drawString(String.format("%2d", i), x, y);
      }
    }
  }

  public FiveStraightView(FiveStraightModel FSM, Shell parentShell) {
    super(parentShell);
    this.FSM = FSM;
    addStatusLine();
    addMenuBar();
    addToolBar(SWT.FLAT | SWT.WRAP);
  }

  @Override
  public boolean close() {
    if (waitCursor != null) {
      waitCursor.dispose();
    }
    return super.close();
  }

  @Override
  protected Control createContents(Composite parent) {
    sh = getShell();
    display = sh.getDisplay();

    sh.setText("FiveStraight");
    sh.setSize(600, 700);
    sh.setImage(new Image(display, "src/org/eb/FiveStraight/icons/FiveStraight.gif"));

    waitCursor = new Cursor(sh.getDisplay(), SWT.CURSOR_WAIT);

    canvas = new Canvas(sh, SWT.BORDER);
    canvas.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        int w = canvas.getClientArea().width / Globals.SANZ;
        int h = canvas.getClientArea().height / Globals.ZANZ;

        for (int i = 0; i < Globals.SANZZANZ; i++) {
          switch (FSM.ss.spielfeld[i]) {
            case SPIELER:
              e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
              break;
            case FREI:
              e.gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
              break;
            case COMP:
              e.gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
              break;
          }
          int col = i / Globals.SANZ;
          int row = i % Globals.SANZ;
          e.gc.fillOval(row * w + 3, col * h + 3, w - 3, h - 3);
          int x = row * w + 4 * w / Globals.SANZ;
          int y = col * h + 4 * h / Globals.ZANZ;
          e.gc.drawString(String.format("%2d", i), x, y);
        }
      }
    });
    canvas.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if ((e.stateMask & SWT.CTRL) != 0) {
          if (((char) e.keyCode) == 's') {
            FSM.SaveGame("data/s.txt");
          }
          if (((char) e.keyCode) == 'l') {
            FSM.LoadGame("data/s.txt");
            canvas.redraw();
            String msg = String.format("Mein Zug:%d GWOffen:%d Val:%d c:(%d,%d,%d,%d) s:(%d,%d,%d,%d)", FSM.ss.letzterzug,
                    FSM.ss.groffen, FSM.ss.wertDerStellung, FSM.ss.cvier, FSM.ss.cdrei, FSM.ss.czwei, FSM.ss.ceins,
                    FSM.ss.svier, FSM.ss.sdrei, FSM.ss.szwei, FSM.ss.seins);
            sm.setMessage(msg);
          }
        }
      }
    });
    canvas.addMouseListener(new MouseListener() {
      @Override
      public void mouseDoubleClick(MouseEvent e) {
      }

      @Override
      public void mouseDown(MouseEvent e) {
      }

      @Override
      public void mouseUp(MouseEvent e) {
        int h = canvas.getClientArea().height;
        int w = canvas.getClientArea().width;
        double z = ((double) e.y / (double) h) * Globals.ZANZ;
        double s = ((double) e.x / (double) w) * Globals.SANZ;
        int zug = (int) z * Globals.SANZ + (int) s;

        if (FSM.ss.amzug == FeldWerte.COMP) {
          sm.setMessage("Du bist nicht am Zug!");
          display.beep();
          MessageBox("Warning", "Du bist nicht am Zug!");
          return;
        }
        if (FSM.ss.muehle || FSM.ss.anzahlFelderBesetzt >= Globals.SANZZANZ) {
          sm.setMessage("Spiel ist schon aus!");
          display.beep();
          MessageBox("Warning", "Das Spiel ist schon aus");
          return;
        }
        if (FSM.ss.spielfeld[zug] != FeldWerte.FREI) {
          sm.setMessage("Dieses Feld ist schon besetzt!");
          display.beep();
          return;
        }

        FSM.ziehen(zug);
        canvas.redraw();
        sh.update();

        if (FSM.ss.muehle) {
          sm.setMessage("Gratuliere, Du hast gewonnen!");
          display.beep();
          MessageBox("FiveStraight", "Gratuliere, Du hast gewonnen!");
          return;

        }
        if (FSM.ss.anzahlFelderBesetzt == Globals.SANZZANZ || FSM.ss.groffen == 0) {
          sm.setMessage("Gratuliere, Du hast ein Remis geschafft!");
          display.beep();
          MessageBox("FiveStraight", "Gratuliere, Du hast ein Remis geschafft!");
          return;
        }

        sh.setCursor(waitCursor);
        FSM.computerzug();
        canvas.redraw();
        sh.setCursor(null);

        String msg = String.format("Mein Zug:%d GWOffen:%d Val:%d c:(%d,%d,%d,%d) s:(%d,%d,%d,%d)", FSM.ss.letzterzug,
                FSM.ss.groffen, FSM.ss.wertDerStellung, FSM.ss.cvier, FSM.ss.cdrei, FSM.ss.czwei, FSM.ss.ceins,
                FSM.ss.svier, FSM.ss.sdrei, FSM.ss.szwei, FSM.ss.seins);
        sm.setMessage(msg);

        if (FSM.ss.muehle) {
          display.beep();
          MessageBox("FiveStraight", "Bedaure Du hast verloren!");
        } else if (FSM.ss.anzahlFelderBesetzt == Globals.SANZZANZ || FSM.ss.groffen == 0) {
          display.beep();
          MessageBox("FiveStraight", "Gratuliere, Du hast ein Remis geschafft!");
        }
      }
    });
    canvas.setFocus();
    return canvas;
  }

  @Override
  protected MenuManager createMenuManager() {

    MenuManager smenu = new MenuManager("Spiel");
    smenu.add(act_NeuesSpiel);
    smenu.add(act_ZugZurueckNehmen);
    smenu.add(act_Beenden);

    MenuManager omenu = new MenuManager("Optionen");
    ActionSpielStaerkeSetzen a1 = new ActionSpielStaerkeSetzen(4);
    ActionSpielStaerkeSetzen a2 = new ActionSpielStaerkeSetzen(5);
    ActionSpielStaerkeSetzen a3 = new ActionSpielStaerkeSetzen(6);
    ActionSpielStaerkeSetzen a4 = new ActionSpielStaerkeSetzen(7);
    ActionSpielStaerkeSetzen a5 = new ActionSpielStaerkeSetzen(8);
    switch (FSM.MaxLevel) {
      case 4:
        a1.setChecked(true);
        break;
      case 5:
        a2.setChecked(true);
        break;
      case 6:
        a3.setChecked(true);
        break;
      case 7:
        a4.setChecked(true);
        break;
      case 8:
        a5.setChecked(true);
        break;
    }
    omenu.add(a1);
    omenu.add(a2);
    omenu.add(a3);
    omenu.add(a4);
    omenu.add(a5);
    omenu.add(new Separator());

    Action aa = new Action("Computer beginnt", Action.AS_CHECK_BOX) {
      @Override
      public void run() {
        FSM.CompBeginnt = !FSM.CompBeginnt;
      }
    };
    aa.setChecked(FSM.CompBeginnt);
    omenu.add(aa);

    MenuManager hmenu = new MenuManager("Hilfe");
    hmenu.add(new Action("Info über FiveStraight") {
      @Override
      public void run() {
        MessageBox mb = new MessageBox(sh, SWT.OK);
        mb.setText("Info über FiveStraight");
        mb.setMessage("FiveStraight Version1.0\n\nCopyright (C) 2005");
        mb.open();
      }
    });

    MenuManager main_menu = new MenuManager(null);
    main_menu.add(smenu);
    main_menu.add(omenu);
    main_menu.add(hmenu);
    return main_menu;
  }

  @Override
  protected StatusLineManager createStatusLineManager() {
    this.sm = new StatusLineManager();
    return sm;
  }

  @Override
  protected ToolBarManager createToolBarManager(int style) {
    ToolBarManager tool_bar_manager = new ToolBarManager(style);
    tool_bar_manager.add(act_NeuesSpiel);
    tool_bar_manager.add(act_ZugZurueckNehmen);
    tool_bar_manager.add(act_Beenden);
    return tool_bar_manager;
  }

  // //////////////////////////////////////////////////////////
  // Actions!!
  // //////////////////////////////////////////////////////////
  class ActionNeuesSpiel extends Action {

    public ActionNeuesSpiel() {
      super("&Neues Spiel@Ctrl+N", AS_PUSH_BUTTON);
      setToolTipText("Neues Spiel starten");
      setImageDescriptor(ImageDescriptor.createFromFile(null, "src/org/eb/FiveStraight/icons/NeuesSpiel.gif"));
    }

    @Override
    public void run() {
      sh.setCursor(waitCursor);
      FSM.NeuesSpiel();
      sh.setCursor(null);
      canvas.redraw();
      sm.setMessage("Neues Spiel gestartet!!");
    } 
  }

  class ActionZugZurueckNehmen extends Action {

    public ActionZugZurueckNehmen() {
      super("Zug Zurücknehmen@Ctrl+Z", AS_PUSH_BUTTON);
      setToolTipText("Zug Zurücknehmen");
      setImageDescriptor(ImageDescriptor.createFromFile(null, "src/org/eb/FiveStraight/icons/ZugZurückNehmen.gif"));
    }

    @Override
    public void run() {
      FSM.ZugZurueck();
      canvas.redraw();
      String msg = String.format("Mein Zug:%d GWOffen:%d Val:%d c:(%d,%d,%d,%d) s:(%d,%d,%d,%d)", FSM.ss.letzterzug,
              FSM.ss.groffen, FSM.ss.wertDerStellung, FSM.ss.cvier, FSM.ss.cdrei, FSM.ss.czwei, FSM.ss.ceins,
              FSM.ss.svier, FSM.ss.sdrei, FSM.ss.szwei, FSM.ss.seins);
      sm.setMessage(msg);
    }
  };

  class ActionBeenden extends Action {

    public ActionBeenden() {
      super("&Beenden", AS_PUSH_BUTTON);
      setToolTipText("Programm beenden");
      setImageDescriptor(ImageDescriptor.createFromFile(null, "src/org/eb/FiveStraight/icons/Beenden.gif"));
    }

    @Override
    public void run() {
      sh.close();
    }
  }

  static final String[] SPIELSTAERKESTRING = {"---", "---", "---", "---", "Anfänger", "Fortgeschrittener", "Meister", "Großmeister", "Weltmeister", "---"};

  class ActionSpielStaerkeSetzen extends Action {

    int MaxLevel;

    public ActionSpielStaerkeSetzen(int MaxLevel) {
      super(SPIELSTAERKESTRING[MaxLevel], AS_RADIO_BUTTON);
      this.MaxLevel = MaxLevel;
      setToolTipText("Spielstärke:" + Arrays.toString(SPIELSTAERKESTRING));
    }

    @Override
    public void run() {
      FSM.MaxLevel = MaxLevel;
      sm.setMessage("Spielstärke " + SPIELSTAERKESTRING[MaxLevel] + " eingestellt");
    }
  }

  // //////////////////////////////////////////////////////////
  public static void main(String[] args) {
    FiveStraightModel FSM = new FiveStraightModel();
    FSM.MaxLevel = 5;
    FSM.CompBeginnt = true;
    FSM.ss.init(FSM.CompBeginnt ? FeldWerte.COMP : FeldWerte.SPIELER);
    /*
		 * FSM.vgziehen(54, FSM.ss); FSM.vgziehen(1, FSM.ss); FSM.vgziehen(55,
		 * FSM.ss); FSM.vgziehen(6, FSM.ss); FSM.vgziehen(67, FSM.ss);
		 * FSM.vgziehen(10, FSM.ss); FSM.vgziehen(78, FSM.ss); FSM.vgziehen(0,
		 * FSM.ss);
     */
    FSM.computerzug();
    FiveStraightView fsw = new FiveStraightView(FSM, null);
    fsw.setBlockOnOpen(true);
    fsw.open();
  }
}
