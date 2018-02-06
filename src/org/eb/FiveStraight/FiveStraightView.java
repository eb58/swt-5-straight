package org.eb.FiveStraight;

import org.eb.FiveStraight.model.FiveStraightModel;
import org.eb.FiveStraight.util.Constants;
import java.util.Arrays;
import org.eb.FiveStraight.util.ValuesOfFields;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import static org.eclipse.jface.action.IAction.AS_PUSH_BUTTON;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;

public class FiveStraightView extends ApplicationWindow {

  FiveStraightModel FSM;
  Canvas canvas;
  Shell shell;
  Display display;
  Cursor waitCursor;
  StatusLineManager statusbarManager;

  ActionNewGame action_NewGame = new ActionNewGame();
  ActionUndoMove action_UndoMove = new ActionUndoMove();
  ActionStopPlaying action_StopPlaying = new ActionStopPlaying();

  private void myMessageBox(String title, String msg) {

    display.beep();
    statusbarManager.setMessage(msg);

    MessageBox mb = new MessageBox(shell, SWT.OK);
    mb.setText(title);
    mb.setMessage(msg);
    mb.open();
  }

  private void drawGamingBoard(GC gc) {
    int w = canvas.getClientArea().width / Constants.NUMBEROFCOLUMNS;
    int h = canvas.getClientArea().height / Constants.NUMBEROFROWS;

    for (int i = 0; i < Constants.NUMBEROFFIELDS; i++) {
      switch (FSM.ss.gamingBoard[i]) {
        case PLAYER2:
          gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
          break;
        case EMPTY:
          gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
          break;
        case PLAYER1:
          gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
          break;
      }
      int col = i / Constants.NUMBEROFCOLUMNS;
      int row = i % Constants.NUMBEROFCOLUMNS;
      gc.fillOval(row * w + 3, col * h + 3, w - 3, h - 3);
      int x = row * w + 4 * w / Constants.NUMBEROFCOLUMNS;
      int y = col * h + 4 * h / Constants.NUMBEROFROWS;
      gc.drawString(String.format("%2d", i), x, y);

    }
  }

  private String statusMessage(FiveStraightModel FSM) {
    return FSM.ss.getStatusString();
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
    shell = getShell();
    display = shell.getDisplay();

    shell.setText("FiveStraight");
    shell.setSize(600, 700);
    shell.setImage(new Image(display, "src/org/eb/FiveStraight/icons/FiveStraight.gif"));

    waitCursor = new Cursor(display, SWT.CURSOR_WAIT);

    canvas = new Canvas(shell, SWT.BORDER);
    canvas.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    canvas.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        drawGamingBoard(e.gc);
      }
    });

    canvas.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if ((e.stateMask & SWT.CTRL) == 0) {
          return;
        }

        if (((char) e.keyCode) == 's') {
          FSM.saveGame("/temp/s.txt");
          statusbarManager.setMessage("Spiel geladen");
        }
        if (((char) e.keyCode) == 'l') {
          FSM.loadGame("/temp/s.txt");
          canvas.redraw();
          statusbarManager.setMessage(statusMessage(FSM));
        }
      }
    });
    canvas.addMouseListener(new MouseAdapter() {

      private int getFieldNumberFromClickEvent(MouseEvent e) {
        double row = ((double) e.y / (double) canvas.getClientArea().height) * Constants.NUMBEROFROWS;
        double col = ((double) e.x / (double) canvas.getClientArea().width) * Constants.NUMBEROFCOLUMNS;
        return (int) row * Constants.NUMBEROFCOLUMNS + (int) col;
      }

      @Override
      public void mouseUp(MouseEvent e) {
        int fieldNumber = getFieldNumberFromClickEvent(e);

        if (FSM.ss.whosOnTurn == ValuesOfFields.PLAYER1) {
          myMessageBox("Warning", "Du bist nicht am Zug!");
          return;
        }
        if (FSM.ss.isMill || FSM.ss.numberOfFieldsOccupied >= Constants.NUMBEROFFIELDS) {
          myMessageBox("Warning", "Das Spiel ist schon aus");
          return;
        }
        if (FSM.ss.gamingBoard[fieldNumber] != ValuesOfFields.EMPTY) {
          display.beep();
          statusbarManager.setMessage("Dieses Feld ist schon besetzt!");
          return;
        }

        FSM.makeMove(fieldNumber);
        canvas.redraw();
        shell.update();

        if (FSM.ss.isMill) {
          myMessageBox("FiveStraight", "Gratuliere, Du hast gewonnen!");
          return;
        }
        if (FSM.ss.numberOfFieldsOccupied == Constants.NUMBEROFFIELDS || FSM.ss.numberOfOpenWinningRows == 0) {
          myMessageBox("FiveStraight", "Gratuliere, Du hast ein Remis geschafft!");
          return;
        }

        shell.setCursor(waitCursor);
        FSM.moveOfComputer();

        canvas.redraw();
        shell.setCursor(null);
        statusbarManager.setMessage(statusMessage(FSM));

        if (FSM.ss.isMill) {
          myMessageBox("FiveStraight", "Bedaure Du hast verloren!");
        } else if (FSM.ss.numberOfFieldsOccupied == Constants.NUMBEROFFIELDS || FSM.ss.numberOfOpenWinningRows == 0) {
          myMessageBox("FiveStraight", "Gratuliere, Du hast ein Remis geschafft!");
        }
      }

    });

    canvas.setFocus();
    return canvas;
  }

  @Override
  protected MenuManager createMenuManager() {

    MenuManager gameMenu = new MenuManager("Spiel");
    gameMenu.add(action_NewGame);
    gameMenu.add(action_UndoMove);
    gameMenu.add(action_StopPlaying);

    MenuManager optionsMenu = new MenuManager("Optionen");
    for (int i = 4; i <= 8; i++) {
      ActionSetLevel action = new ActionSetLevel(i);
      action.setChecked(FSM.ss.maxLevel == i);
      optionsMenu.add(action);
    }

    optionsMenu.add(new Separator());
    optionsMenu.add(new ActionToggleBeginner());

    MenuManager helpMenu = new MenuManager("Hilfe");
    helpMenu.add(new Action("Info über FiveStraight") {
      @Override
      public void run() {
        MessageBox mb = new MessageBox(shell, SWT.OK);
        mb.setText("Info über FiveStraight");
        mb.setMessage("FiveStraight Version1.0\n\nCopyright (C) 2005");
        mb.open();
      }
    });

    MenuManager mainMenu = new MenuManager(null);
    mainMenu.add(gameMenu);
    mainMenu.add(optionsMenu);
    mainMenu.add(helpMenu);
    return mainMenu;
  }

  @Override
  protected StatusLineManager createStatusLineManager() {
    statusbarManager = new StatusLineManager();
    return statusbarManager;
  }

  @Override
  protected ToolBarManager createToolBarManager(int style) {
    ToolBarManager toolbarManager = new ToolBarManager(style);
    toolbarManager.add(action_NewGame);
    toolbarManager.add(action_UndoMove);
    toolbarManager.add(action_StopPlaying);
    return toolbarManager;
  }

  // //////////////////////////////////////////////////////////
  // Actions!!
  // //////////////////////////////////////////////////////////
  class ActionToggleBeginner extends Action {

    public ActionToggleBeginner() {
      super("Computer beginnt", AS_CHECK_BOX);
      setToolTipText("Ändere den Spieler der beginnt.");
      setChecked(FSM.player1Begins);
    }

    @Override
    public void run() {
      FSM.player1Begins = !FSM.player1Begins;
    }

  }

  class ActionNewGame extends Action {

    public ActionNewGame() {
      super("&Neues Spiel@Ctrl+N", AS_PUSH_BUTTON);
      setToolTipText("Neues Spiel starten");
      setImageDescriptor(ImageDescriptor.createFromFile(null, "src/org/eb/FiveStraight/icons/NeuesSpiel.gif"));
    }

    @Override
    public void run() {
      shell.setCursor(waitCursor);
      FSM.newGame();
      shell.setCursor(null);
      canvas.redraw();
      statusbarManager.setMessage("Neues Spiel gestartet!!");
    }

  }

  class ActionUndoMove extends Action {

    public ActionUndoMove() {
      super("Zug Zurücknehmen@Ctrl+Z", AS_PUSH_BUTTON);
      setToolTipText("Zug Zurücknehmen");
      setImageDescriptor(ImageDescriptor.createFromFile(null, "src/org/eb/FiveStraight/icons/ZugZurückNehmen.gif"));
    }

    @Override
    public void run() {
      FSM.undoMove();
      canvas.redraw();
      statusbarManager.setMessage(FSM.ss.getStatusString());
    }

  };

  class ActionStopPlaying extends Action {

    public ActionStopPlaying() {
      super("&Beenden", AS_PUSH_BUTTON);
      setToolTipText("Programm beenden");
      setImageDescriptor(ImageDescriptor.createFromFile(null, "src/org/eb/FiveStraight/icons/Beenden.gif"));
    }

    @Override
    public void run() {
      shell.close();
    }

  }

  final static String[] STRING_OF_SKILL_LEVELS = {"---", "---", "---", "---", "Anfänger", "Fortgeschrittener", "Meister", "Großmeister", "Weltmeister", "---"};

  class ActionSetLevel extends Action {

    int maxLevel;

    public ActionSetLevel(int maxLevel) {
      super(STRING_OF_SKILL_LEVELS[maxLevel], AS_RADIO_BUTTON);
      this.maxLevel = maxLevel;
    }

    @Override
    public void run() {
      FSM.ss.maxLevel = maxLevel;
      statusbarManager.setMessage("Spielstärke " + STRING_OF_SKILL_LEVELS[maxLevel] + " eingestellt.");
    }

  }

// //////////////////////////////////////////////////////////
  public static void main(String[] args) {
    FiveStraightModel FSM = new FiveStraightModel();
    FSM.ss.maxLevel = 5;
    FSM.player1Begins = true;
    FSM.ss.init(FSM.player1Begins ? ValuesOfFields.PLAYER1 : ValuesOfFields.PLAYER2);
    /*
		 * FSM.vgziehen(54, FSM.ss); FSM.vgziehen(1, FSM.ss); FSM.vgziehen(55,
		 * FSM.ss); FSM.vgziehen(6, FSM.ss); FSM.vgziehen(67, FSM.ss);
		 * FSM.vgziehen(10, FSM.ss); FSM.vgziehen(78, FSM.ss); FSM.vgziehen(0,
		 * FSM.ss);
     */
    FSM.moveOfComputer();
    FiveStraightView fsv = new FiveStraightView(FSM, null);
    fsv.setBlockOnOpen(true);
    fsv.open();
  }
}
