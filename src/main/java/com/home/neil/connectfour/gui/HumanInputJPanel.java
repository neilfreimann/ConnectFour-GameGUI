package com.home.neil.connectfour.gui;

import java.awt.AWTEvent;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.controller.Game;
import com.home.neil.connectfour.controller.GameController;
import com.home.neil.connectfour.controller.HumanPlayer;

import old.com.home.neil.connectfour.boardstate.OldBoardState;



public class HumanInputJPanel extends JPanel {
	private static final long serialVersionUID = 7243966915288858373L;
	public static final String CLASS_NAME = HumanInputJPanel.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private static final String NEWLINE = System.getProperty("line.separator");

	private JPanel mBoardButtonGridPanel;

	private GameController mGameController = null;
	private HumanPlayer mHumanPlayer = null;

	private JPanel mTopGameInputPanel = null;
	private JTextField mMovesTextField = null;
	private JButton mResignGameButton;
	private JButton mUndoMoveButton;
	private JButton mBoardMoveColumn6Button;
	private JButton mBoardMoveColumn5Button;
	private JButton mBoardMoveColumn7Button;
	private JButton mBoardMoveColumn4Button;
	private JButton mBoardMoveColumn3Button;
	private JButton mBoardMoveColumn2Button;
	private JButton mBoardMoveColumn1Button;
	private final Action mButtonAction = new GameButtonAction();

	private void resetInputPanelDuringSubmission() {
		sLogger.trace("Entering");
		mUndoMoveButton.setEnabled(false);
		mResignGameButton.setEnabled(false);
		mBoardMoveColumn1Button.setEnabled(false);
		mBoardMoveColumn2Button.setEnabled(false);
		mBoardMoveColumn3Button.setEnabled(false);
		mBoardMoveColumn4Button.setEnabled(false);
		mBoardMoveColumn5Button.setEnabled(false);
		mBoardMoveColumn6Button.setEnabled(false);
		mBoardMoveColumn7Button.setEnabled(false);
		sLogger.trace("Exiting");
	}

	public void setMoveButtons(OldBoardState[] pValidMoves, boolean lForceDisabled) {
		sLogger.trace("Entering");

		mBoardMoveColumn1Button.setEnabled(false);
		mBoardMoveColumn2Button.setEnabled(false);
		mBoardMoveColumn3Button.setEnabled(false);
		mBoardMoveColumn4Button.setEnabled(false);
		mBoardMoveColumn5Button.setEnabled(false);
		mBoardMoveColumn6Button.setEnabled(false);
		mBoardMoveColumn7Button.setEnabled(false);

		mBoardMoveColumn1Button.setText("1 (?)");
		mBoardMoveColumn2Button.setText("2 (?)");
		mBoardMoveColumn3Button.setText("3 (?)");
		mBoardMoveColumn4Button.setText("4 (?)");
		mBoardMoveColumn5Button.setText("5 (?)");
		mBoardMoveColumn6Button.setText("6 (?)");
		mBoardMoveColumn7Button.setText("7 (?)");

		if (pValidMoves != null) {
			for (int i = 0; i < pValidMoves.length; i++) {
				OldBoardState lCurrentValidMoveBoardState = pValidMoves[i];
				OldBoardState.Move lCurrentValidMove = lCurrentValidMoveBoardState.getMove();
				int lCurrentValidMoveIntValue = lCurrentValidMove.getMoveIntValue();
				byte lCurrentValidMoveScore = lCurrentValidMoveBoardState.getMoveScore().getMoveScore();
				if (lCurrentValidMoveIntValue == 1) {
					mBoardMoveColumn1Button.setEnabled(!lForceDisabled && true);
					mBoardMoveColumn1Button.setText("1 (" + lCurrentValidMoveScore + ")");
				} else if (lCurrentValidMoveIntValue == 2) {
					mBoardMoveColumn2Button.setEnabled(!lForceDisabled && true);
					mBoardMoveColumn2Button.setText("2 (" + lCurrentValidMoveScore + ")");
				} else if (lCurrentValidMoveIntValue == 3) {
					mBoardMoveColumn3Button.setEnabled(!lForceDisabled && true);
					mBoardMoveColumn3Button.setText("3 (" + lCurrentValidMoveScore + ")");
				} else if (lCurrentValidMoveIntValue == 4) {
					mBoardMoveColumn4Button.setEnabled(!lForceDisabled && true);
					mBoardMoveColumn4Button.setText("4 (" + lCurrentValidMoveScore + ")");
				} else if (lCurrentValidMoveIntValue == 5) {
					mBoardMoveColumn5Button.setEnabled(!lForceDisabled && true);
					mBoardMoveColumn5Button.setText("5 (" + lCurrentValidMoveScore + ")");
				} else if (lCurrentValidMoveIntValue == 6) {
					mBoardMoveColumn6Button.setEnabled(!lForceDisabled && true);
					mBoardMoveColumn6Button.setText("6 (" + lCurrentValidMoveScore + ")");
				} else if (lCurrentValidMoveIntValue == 7) {
					mBoardMoveColumn7Button.setEnabled(!lForceDisabled && true);
					mBoardMoveColumn7Button.setText("7 (" + lCurrentValidMoveScore + ")");
				}
			}
		}

		sLogger.trace("Exiting");
	}

	public void setInputPanelByGameState(Game.GAMESTATE pInputGameState, OldBoardState[] pValidMoves) {
		sLogger.trace("Entering");

		sLogger.debug("CurrentGameState is: " + pInputGameState.getGameStateString());

		if (pInputGameState == Game.GAMESTATE.COMPUTER_O_WON_GAME || pInputGameState == Game.GAMESTATE.COMPUTER_O_WON_GAME_BY_DISQUALIFIED
				|| pInputGameState == Game.GAMESTATE.COMPUTER_O_WON_GAME_BY_RESIGNATION || pInputGameState == Game.GAMESTATE.COMPUTER_O_WON_GAME_BY_TIMEOUT
				|| pInputGameState == Game.GAMESTATE.COMPUTER_X_WON_GAME || pInputGameState == Game.GAMESTATE.COMPUTER_X_WON_GAME_BY_RESIGNATION
				|| pInputGameState == Game.GAMESTATE.COMPUTER_X_WON_GAME_BY_TIMEOUT || pInputGameState == Game.GAMESTATE.DRAW_GAME
				|| pInputGameState == Game.GAMESTATE.HUMAN_O_WON_GAME || pInputGameState == Game.GAMESTATE.HUMAN_O_WON_GAME_BY_DISQUALIFIED
				|| pInputGameState == Game.GAMESTATE.HUMAN_O_WON_GAME_BY_RESIGNATION || pInputGameState == Game.GAMESTATE.HUMAN_O_WON_GAME_BY_TIMEOUT
				|| pInputGameState == Game.GAMESTATE.HUMAN_X_WON_GAME || pInputGameState == Game.GAMESTATE.HUMAN_X_WON_GAME_BY_DISQUALIFIED
				|| pInputGameState == Game.GAMESTATE.HUMAN_X_WON_GAME_BY_RESIGNATION || pInputGameState == Game.GAMESTATE.HUMAN_X_WON_GAME_BY_TIMEOUT) {
			mUndoMoveButton.setEnabled(true);
			mResignGameButton.setEnabled(true);
			setMoveButtons(pValidMoves, true);
		} else if (pInputGameState == Game.GAMESTATE.COMPUTER_O_MOVE || pInputGameState == Game.GAMESTATE.COMPUTER_X_MOVE) {
			mUndoMoveButton.setEnabled(true);
			mResignGameButton.setEnabled(true);
			setMoveButtons(pValidMoves, true);

		} else if (pInputGameState == Game.GAMESTATE.GAME_UNSTARTED || pInputGameState == Game.GAMESTATE.GAME_STARTED_COMPUTER_FIRST_X_MOVE) {
			mUndoMoveButton.setEnabled(false);
			mResignGameButton.setEnabled(false);
			setMoveButtons(pValidMoves, true);
		} else if (pInputGameState == Game.GAMESTATE.GAME_STARTED_HUMAN_FIRST_X_MOVE) {
			mUndoMoveButton.setEnabled(false);
			mResignGameButton.setEnabled(true);
			setMoveButtons(pValidMoves, false);
		} else if (pInputGameState == Game.GAMESTATE.HUMAN_O_MOVE || pInputGameState == Game.GAMESTATE.HUMAN_X_MOVE) {
			mUndoMoveButton.setEnabled(true);
			mResignGameButton.setEnabled(true);
			setMoveButtons(pValidMoves, false);
		} else {
			mUndoMoveButton.setEnabled(false);
			mResignGameButton.setEnabled(true);
			setMoveButtons(pValidMoves, true);
		}
		sLogger.trace("Exiting");
	}

	public HumanInputJPanel(GameController pGameController, HumanPlayer pHumanPlayer) {
		sLogger.trace("Entering");

		mGameController = pGameController;
		mHumanPlayer = pHumanPlayer;

		mTopGameInputPanel = new JPanel();
		mTopGameInputPanel.setLayout(new GridLayout(1, 3));

		mMovesTextField = new JTextField();
		mMovesTextField.setText("");
		mMovesTextField.setEditable(false);
		mMovesTextField.setEnabled(false);
		mMovesTextField.addKeyListener(new KeyListener() {
			public synchronized void keyTyped(KeyEvent pKE) {
				sLogger.trace("Entering");
				displayInfo(pKE, "KEY TYPED: ");
				sLogger.trace("Exiting");
			}

			public void keyPressed(KeyEvent pKE) {
				sLogger.trace("Entering");

				resetInputPanelDuringSubmission();

				int lKeyCode = pKE.getKeyCode();

				if (lKeyCode >= 97 && lKeyCode <= 103) { // Between 1 and 7 key
															// pressed
					performMove(pKE);
				} else if (lKeyCode >= 49 && lKeyCode <= 55) { // Between 1 and
																// 7 non keypad
					performMove(pKE);
				} else if (lKeyCode == 8) {
					performUndoMove();
				} else if (lKeyCode == 27) {
					performResignation();
				}

				displayInfo(pKE, "KEY PRESSED: ");
				sLogger.trace("Exiting");
			}

			public void keyReleased(KeyEvent pKE) {
				sLogger.trace("Entering");
				displayInfo(pKE, "KEY RELEASED: ");
				sLogger.trace("Exiting");
			}
		});

		mTopGameInputPanel.add(mMovesTextField);

		mResignGameButton = new JButton("Resign");
		mResignGameButton.setActionCommand("resign");
		mResignGameButton.setAction(mButtonAction);
		mResignGameButton.setEnabled(false);

		mUndoMoveButton = new JButton("<");
		mUndoMoveButton.setEnabled(false);
		mUndoMoveButton.setActionCommand("undo");
		mUndoMoveButton.setAction(mButtonAction);

		mTopGameInputPanel.add(mUndoMoveButton);
		mTopGameInputPanel.add(mResignGameButton);

		mBoardButtonGridPanel = new JPanel();
		mBoardButtonGridPanel.setLayout(new GridLayout(1, 7));

		setLayout(new GridLayout(2, 1));
		add(mTopGameInputPanel);
		add(mBoardButtonGridPanel);

		mBoardMoveColumn1Button = new JButton("1(?)");
		mBoardMoveColumn1Button.setAction(mButtonAction);
		mBoardMoveColumn1Button.setEnabled(false);
		mBoardMoveColumn1Button.setActionCommand("1");
		mBoardButtonGridPanel.add(mBoardMoveColumn1Button);

		mBoardMoveColumn2Button = new JButton("2(?)");
		mBoardMoveColumn2Button.setAction(mButtonAction);
		mBoardMoveColumn2Button.setEnabled(false);
		mBoardMoveColumn2Button.setActionCommand("2");
		mBoardButtonGridPanel.add(mBoardMoveColumn2Button);

		mBoardMoveColumn3Button = new JButton("3(?)");
		mBoardMoveColumn3Button.setAction(mButtonAction);
		mBoardMoveColumn3Button.setEnabled(false);
		mBoardMoveColumn3Button.setActionCommand("3");
		mBoardButtonGridPanel.add(mBoardMoveColumn3Button);

		mBoardMoveColumn4Button = new JButton("4(?)");
		mBoardMoveColumn4Button.setAction(mButtonAction);
		mBoardMoveColumn4Button.setEnabled(false);
		mBoardMoveColumn4Button.setActionCommand("4");
		mBoardButtonGridPanel.add(mBoardMoveColumn4Button);

		mBoardMoveColumn5Button = new JButton("5(?)");
		mBoardMoveColumn5Button.setAction(mButtonAction);
		mBoardMoveColumn5Button.setEnabled(false);
		mBoardMoveColumn5Button.setActionCommand("5");
		mBoardButtonGridPanel.add(mBoardMoveColumn5Button);

		mBoardMoveColumn6Button = new JButton("6(?)");
		mBoardMoveColumn6Button.setAction(mButtonAction);
		mBoardMoveColumn6Button.setEnabled(false);
		mBoardMoveColumn6Button.setActionCommand("6");
		mBoardButtonGridPanel.add(mBoardMoveColumn6Button);

		mBoardMoveColumn7Button = new JButton("7(?)");
		mBoardMoveColumn7Button.setAction(mButtonAction);
		mBoardMoveColumn7Button.setEnabled(false);
		mBoardMoveColumn7Button.setActionCommand("7");
		mBoardButtonGridPanel.add(mBoardMoveColumn7Button);

		sLogger.trace("Exiting");
	}

	public synchronized void performMove(AWTEvent pEvent) {
		sLogger.trace("Entering");
		int lActionCommand = 0;

		sLogger.debug("Event ID received:", pEvent.getID());

		if (pEvent.getID() == KeyEvent.KEY_PRESSED) {
			sLogger.debug("Key Press Event received");
			sLogger.info("Human Player Submitted Move: " + lActionCommand);
			KeyEvent pKeyEvent = (KeyEvent) pEvent;
			char lCharTyped = pKeyEvent.getKeyChar();
			String lCharTypedString = String.valueOf(lCharTyped);
			lActionCommand = Integer.parseInt(lCharTypedString);
		} else if (pEvent.getID() == ActionEvent.ACTION_PERFORMED) {
			sLogger.debug("Action Event received");
			ActionEvent pActionEvent = (ActionEvent) pEvent;
			lActionCommand = Integer.parseInt(pActionEvent.getActionCommand());
		}

		sLogger.info("Human Player Submitting Move: " + lActionCommand);

		mGameController.submitMove(mHumanPlayer, lActionCommand);

		sLogger.trace("Exiting");
	}

	public synchronized void performUndoMove() {
		sLogger.trace("Entering");

		sLogger.info("Human Player Submitting Undo Command.");

		mGameController.undoMove(mHumanPlayer);

		sLogger.trace("Exiting");
	}

	public synchronized void performResignation() {
		sLogger.trace("Entering");

		sLogger.info("Human Player Submitting Undo Command.");

		mGameController.resign(mHumanPlayer);

		sLogger.trace("Exiting");
	}

	private void displayInfo(KeyEvent pKE, String pKeyStatus) {
		sLogger.trace("Entering");

		// You should only rely on the key char if the event
		// is a key typed event.
		int lID = pKE.getID();
		String lKeyString;
		if (lID == KeyEvent.KEY_TYPED) {
			char lChar = pKE.getKeyChar();
			lKeyString = "Key Character = '" + lChar + "'";
		} else {
			int lKeyCode = pKE.getKeyCode();
			lKeyString = "Key Code = " + lKeyCode + " (" + KeyEvent.getKeyText(lKeyCode) + ")";
		}

		int lModifiersEx = pKE.getModifiersEx();
		String lModString = "Extended modifiers = " + lModifiersEx;
		String lTempString = KeyEvent.getModifiersExText(lModifiersEx);
		if (lTempString.length() > 0) {
			lModString += " (" + lTempString + ")";
		} else {
			lModString += " (no extended modifiers)";
		}

		String lActionString = "Action key? ";
		if (pKE.isActionKey()) {
			lActionString += "YES";
		} else {
			lActionString += "NO";
		}

		String locationString = "Key Location: ";
		int location = pKE.getKeyLocation();
		if (location == KeyEvent.KEY_LOCATION_STANDARD) {
			locationString += "Standard";
		} else if (location == KeyEvent.KEY_LOCATION_LEFT) {
			locationString += "Left";
		} else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
			locationString += "Right";
		} else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
			locationString += "Numpad";
		} else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
			locationString += "Unknown";
		}

		sLogger.debug("Keyboard input: " + pKeyStatus + NEWLINE + "    " + lKeyString + NEWLINE + "    " + lModString + NEWLINE + "    " + lActionString
				+ NEWLINE + "    " + locationString + NEWLINE);

		// TODO:Display in the status field
		// mDisplayTextField.append(pKeyStatus + newline + "    " + lKeyString +
		// newline + "    " + modString + newline + "    " + actionString +
		// newline + "    "
		// + locationString + newline);
		// mDisplayTextField.setCaretPosition(mDisplayTextField.getDocument().getLength());
		sLogger.trace("Exiting");
	}

	private class GameButtonAction extends AbstractAction {
		private static final long serialVersionUID = 4313617237008927949L;

		public GameButtonAction() {
			putValue(NAME, "Game Button Actions");
			putValue(SHORT_DESCRIPTION, "Game Button Actions");
		}

		public void actionPerformed(ActionEvent pActionEvent) {
			sLogger.trace("Entering");
			sLogger.info("Board Button Action Event Received.");
			
			String lActionCommand = pActionEvent.getActionCommand();
			sLogger.info("Submitting Move: " + lActionCommand);

			resetInputPanelDuringSubmission();

			if (lActionCommand.equals("1") || lActionCommand.equalsIgnoreCase("2") || lActionCommand.equals("3") || lActionCommand.equals("4") || lActionCommand.equals("5") || lActionCommand.equals("6") || lActionCommand.equals("7")) {
				performMove(pActionEvent);
			} else if (lActionCommand.equals("undo")) {
				performUndoMove();
			} else if (lActionCommand.equals("resign")) {
				performResignation();
			}
			sLogger.info("Action Performed for Move: " + lActionCommand);
			// TODO:Display in the status field
			sLogger.trace("Exiting");
		}
	}
}
