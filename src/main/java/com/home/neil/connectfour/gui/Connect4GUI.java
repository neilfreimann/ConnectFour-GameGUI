package com.home.neil.connectfour.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.DataFormatException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.boardstate.old.InvalidMoveException;
import com.home.neil.connectfour.boardstate.old.OldBoardState;
import com.home.neil.connectfour.boardstate.old.expansiontask.ExpansionTask;
import com.home.neil.connectfour.gamethreads.AutomaticMoveThread;
import com.home.neil.connectfour.gamethreads.BestMoveAutomaticMoveThread;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.knowledgebase.exception.KnowledgeBaseException;
import com.home.neil.connectfour.learninggamethread.givenmove.GivenMoveFixedDurationLearningThread;
import com.home.neil.connectfour.managers.AutomaticMoveThreadManager;
import com.home.neil.connectfour.managers.FixedDurationLearningThreadManager;



public class Connect4GUI {
	public static final String CLASS_NAME = Connect4GUI.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	
	
	public static enum AutomaticState {
		RANDOM_AUTOMATIC(1), BESTMOVE_AUTOMATIC(2), OFF_AUTOMATIC(0);
		private int mState = 0;

		AutomaticState(int lState) {
			mState = lState;
		}

		public int getAutomaticStateInt() {
			return mState;
		}
	}

	private JFrame mFrame;
	private JLabel[][] mSlots;

	private JButton mRandomAutomatic;
	private JButton mBestMoveAutomatic;
	private JButton mStopAutomaticPlay;
	private AutomaticState mAutomaticState = AutomaticState.OFF_AUTOMATIC;

	private FixedDurationLearningThreadManager mFixedDurationLearningThreadManager = null;
	private GivenMoveFixedDurationLearningThread mGivenMoveFixedDurationLearningThread = null;
	private AutomaticMoveThreadManager mAutomaticMoveThreadManager = null;
	private AutomaticMoveThread mAutomaticMoveThread = null;

	private JTextArea mScoreTextField;
	private JTextArea mDisplayTextField;

	private Timer mUpdateRecommendedMovesTimer;

	private static final int XSIZE = OldBoardState.MAX_COLUMNS;
	private static final int YSIZE = OldBoardState.MAX_ROWS;

	private OldBoardState mCurrentBoardState = null;
	private ArrayList<OldBoardState> mCurrentSubBoardStates = null;
	
	private KnowledgeBaseFilePool mKnowledgeBaseFilePool = null;


	public Connect4GUI() throws InvalidMoveException, KnowledgeBaseException, ConfigurationException, IOException {
		sLogger.trace("Entering");
		
		mKnowledgeBaseFilePool = KnowledgeBaseFilePool.getMasterInstance();
		try {
			mCurrentBoardState = new OldBoardState(mKnowledgeBaseFilePool, OldBoardState.Move.OPPONENT_NOMOVE, false, null);
		} catch (InvalidMoveException eIME) {
			sLogger.fatal("Invalid Move on the first move?  WOW!");
			sLogger.trace("Exiting");
			throw eIME;
		} catch (KnowledgeBaseException eKBE) {
			sLogger.fatal("Knowledge Base Exception on the first move?  WOW!");
			sLogger.trace("Exiting");
			throw eKBE;
		}

		mFrame = new JFrame("Connect Four");

		JPanel lMainPanel = (JPanel) mFrame.getContentPane();


		lMainPanel.setLayout(new BorderLayout());
		
		JPanel lGridLayoutPanel = new JPanel();

		lGridLayoutPanel.setLayout(new GridLayout(XSIZE, YSIZE + 1));

		mSlots = new JLabel[XSIZE][YSIZE];


		for (int lRow = YSIZE - 1; lRow >= 0; lRow--) {
			for (int lColumn = 0; lColumn < XSIZE; lColumn++) {
				mSlots[lColumn][lRow] = new JLabel();
				mSlots[lColumn][lRow].setHorizontalAlignment(SwingConstants.CENTER);
				mSlots[lColumn][lRow].setBorder(new LineBorder(Color.black));
				lGridLayoutPanel.add(mSlots[lColumn][lRow]);
			}
		}

	

		mStopAutomaticPlay = new JButton("Stop Automatic Play");
		mStopAutomaticPlay.setActionCommand("Stop Auto");
		mStopAutomaticPlay.setEnabled(false);
		mStopAutomaticPlay.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent pActionEvent) {
				mAutomaticState = AutomaticState.OFF_AUTOMATIC;

				stopAutomaticMoveThread();

//				mUndoMoveButton.setEnabled(true);
//				mClearBoardButton.setEnabled(true);
//				mMovesTextField.setEnabled(true);
//
//				for (int i = 0; i < XSIZE; i++) {
//					mButtons[i].setEnabled(true);
//				}

				restartLearningThread();

				updateBoard();
			}
		});

		mRandomAutomatic = new JButton("Random Automatic Play");
		mRandomAutomatic.setActionCommand("Random Auto");
		mRandomAutomatic.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent pActionEvent) {
				mAutomaticState = AutomaticState.RANDOM_AUTOMATIC;

				stopLearningThread();

//				mUndoMoveButton.setEnabled(false);
//				mClearBoardButton.setEnabled(false);
//				mMovesTextField.setEnabled(false);
//
//				for (int i = 0; i < XSIZE; i++) {
//					mButtons[i].setEnabled(false);
//				}

				restartAutomaticMoveThread();

				updateBoard();
			}
		});

		mBestMoveAutomatic = new JButton("Best Move Automatic Play");
		mBestMoveAutomatic.setActionCommand("Best Move Auto");
		mBestMoveAutomatic.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent pActionEvent) {
				mAutomaticState = AutomaticState.BESTMOVE_AUTOMATIC;

				sLogger.info("Stopping the learning threads");
				stopLearningThread();

				sLogger.info("Disabling Manual Move buttons");
//				mUndoMoveButton.setEnabled(false);
//				mClearBoardButton.setEnabled(false);
//				mMovesTextField.setEnabled(false);
				mBestMoveAutomatic.setEnabled(false);
				mRandomAutomatic.setEnabled(false);

//				for (int i = 0; i < XSIZE; i++) {
//					mButtons[i].setEnabled(false);
//				}
				
				sLogger.info("Enabling Stop Automatic Play button.");
				mStopAutomaticPlay.setEnabled(true);

				sLogger.info("Starting the Automatic Move Threads.");
				restartAutomaticMoveThread();

				sLogger.info("Updating the Board.");
				updateBoard();
			}
		});


		mFrame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent pArg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent pArg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent pArg0) {
				sLogger.trace("Entering");

				if (mFixedDurationLearningThreadManager != null && mGivenMoveFixedDurationLearningThread != null) {
					mFixedDurationLearningThreadManager.deregisterFixedDurationLearningThread(mGivenMoveFixedDurationLearningThread);

					while (mGivenMoveFixedDurationLearningThread.getState() != Thread.State.TERMINATED) {
						try {
							mGivenMoveFixedDurationLearningThread.join();
						} catch (InterruptedException e) {
						}
					}
				}


				if (mAutomaticMoveThreadManager!= null && mAutomaticMoveThread != null) {
					mAutomaticMoveThreadManager.deregisterAutomaticMoveThread(mAutomaticMoveThread);

					while (mAutomaticMoveThread.getState() != Thread.State.TERMINATED) {
						try {
							mAutomaticMoveThread.join();
						} catch (InterruptedException e) {
						}
					}
				}
				
				try {
					KnowledgeBaseFilePool lKnowledgeBaseFilePool = KnowledgeBaseFilePool.getMasterInstance();
					lKnowledgeBaseFilePool.cleanupAll();
				} catch (IOException | ConfigurationException | DataFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sLogger.trace("Exiting");

			}

			@Override
			public void windowDeactivated(WindowEvent pArg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent pArg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent pArg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent pArg0) {
				// TODO Auto-generated method stub

			}

		});

		mDisplayTextField = new JTextArea();
		mDisplayTextField.setEditable(false);
		JScrollPane lScrollPane = new JScrollPane(mDisplayTextField);
		lScrollPane.setPreferredSize(new Dimension(400, 125));

		mUpdateRecommendedMovesTimer = new Timer(5000, new ActionListener() {
			public synchronized void actionPerformed(ActionEvent event) {
				evaluateSubMoves();
			}
		});
		mUpdateRecommendedMovesTimer.start();

		JPanel lBottomButtonLayoutPanel = new JPanel();
		lBottomButtonLayoutPanel.setLayout(new GridLayout(1, 0));
		lBottomButtonLayoutPanel.add(mBestMoveAutomatic);
		lBottomButtonLayoutPanel.add(mRandomAutomatic);
		lBottomButtonLayoutPanel.add(mStopAutomaticPlay);
//		lBottomButtonLayoutPanel.add(mUndoMoveButton);
//		lBottomButtonLayoutPanel.add(mClearBoardButton);
//
//		lMainPanel.add(mMovesTextField, BorderLayout.PAGE_START);
		lMainPanel.add(lScrollPane, BorderLayout.LINE_START);
		lMainPanel.add(lGridLayoutPanel, BorderLayout.CENTER);
		lMainPanel.add(lBottomButtonLayoutPanel, BorderLayout.PAGE_END);

		mFrame.setContentPane(lMainPanel);
		mFrame.setSize(1400, 600);
		mFrame.setVisible(true);
		mFrame.setLocationRelativeTo(null);
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		restartLearningThread();

		updateBoard();
	}

	public synchronized void performAutomaticMove(OldBoardState.Move pMove) {
		try {
			mCurrentBoardState = new OldBoardState(mKnowledgeBaseFilePool, mCurrentBoardState, pMove, false, null);
		} catch (InvalidMoveException e) {
			JOptionPane.showMessageDialog(null, "Move is invalid.", "Move is invalid.", JOptionPane.INFORMATION_MESSAGE);
		} catch (KnowledgeBaseException e) {
			JOptionPane.showMessageDialog(null, "Knowledge Base Exception occurred.", "Knowledge Base Exception occurred.", JOptionPane.INFORMATION_MESSAGE);
		} catch (ConfigurationException e) {
			JOptionPane.showMessageDialog(null, "Configuration Exception occurred.", "Knowledge Base Exception occurred.", JOptionPane.INFORMATION_MESSAGE);
		}

		restartAutomaticMoveThread();

		updateBoard();
	}

	public synchronized void evaluateSubMoves() {
		ExpansionTask lExpandNodeThread = new ExpansionTask(mCurrentBoardState, null);
		boolean lSuccess = lExpandNodeThread.executeTask();
	
		if (!lSuccess) {
			mCurrentSubBoardStates = null;
			return;
		}
		mCurrentSubBoardStates = lExpandNodeThread.getSubBoardStates();
		updateBoard();
	}

	public synchronized void performUndoMove(AWTEvent pEvent) {
		sLogger.trace("Entering");

		int lActionCommand = 0;

		if (mCurrentBoardState.getParentBoardState() != null) {
			mCurrentBoardState = mCurrentBoardState.getParentBoardState();
		}

		restartLearningThread();

		updateBoard();

//		if (pEvent.getID() == ActionEvent.ACTION_PERFORMED) {
//			mMovesTextField.setText(mCurrentBoardState.getFileIndexString());
//		}

		sLogger.trace("Exiting");
	}

	public synchronized void performMove(AWTEvent pEvent) {

		int lActionCommand = 0;

		sLogger.error("getID=", pEvent.getID());

		if (pEvent.getID() == KeyEvent.KEY_PRESSED) {
			KeyEvent pKeyEvent = (KeyEvent) pEvent;
			char lCharTyped = pKeyEvent.getKeyChar();
			String lCharTypedString = String.valueOf(lCharTyped);
			lActionCommand = Integer.parseInt(lCharTypedString);
		} else if (pEvent.getID() == ActionEvent.ACTION_PERFORMED) {
			ActionEvent pActionEvent = (ActionEvent) pEvent;
			lActionCommand = Integer.parseInt(pActionEvent.getActionCommand());
		}

		sLogger.trace("Entering");
		if (mCurrentBoardState.whosMove() == OldBoardState.SELF_MOVE_NEXT) {
			try {
				mCurrentBoardState = new OldBoardState(mKnowledgeBaseFilePool, mCurrentBoardState, OldBoardState.Move.getSelfMove(lActionCommand), false, null);
			} catch (InvalidMoveException e) {
				JOptionPane.showMessageDialog(null, "Move is invalid.", "Move is invalid.", JOptionPane.INFORMATION_MESSAGE);
			} catch (ConfigurationException e) {
				JOptionPane.showMessageDialog(null, "Configuration Exception occurred.", "Knowledge Base Exception occurred.", JOptionPane.INFORMATION_MESSAGE);
			} catch (KnowledgeBaseException e) {
				JOptionPane
						.showMessageDialog(null, "Knowledge Base Exception occurred.", "Knowledge Base Exception occurred.", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			try {
				mCurrentBoardState = new OldBoardState(mKnowledgeBaseFilePool, mCurrentBoardState, OldBoardState.Move.getOpponentMove(lActionCommand), false, null);
			} catch (ConfigurationException e) {
				JOptionPane.showMessageDialog(null, "Configuration Exception occurred.", "Knowledge Base Exception occurred.", JOptionPane.INFORMATION_MESSAGE);
			} catch (InvalidMoveException e) {
				JOptionPane.showMessageDialog(null, "Move is invalid.", "Move is invalid.", JOptionPane.INFORMATION_MESSAGE);
			} catch (KnowledgeBaseException e) {
				JOptionPane
						.showMessageDialog(null, "Knowledge Base Exception occurred.", "Knowledge Base Exception occurred.", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		restartLearningThread();

		updateBoard();

		sLogger.trace("Exiting");
	}

	public synchronized void clearBoard() {
		try {
			mCurrentBoardState = new OldBoardState(mKnowledgeBaseFilePool, OldBoardState.Move.OPPONENT_NOMOVE, false, null);
		} catch (InvalidMoveException e) {
			JOptionPane.showMessageDialog(null, "Move is invalid.", "Move is invalid.", JOptionPane.INFORMATION_MESSAGE);
		} catch (ConfigurationException e) {
			JOptionPane.showMessageDialog(null, "Configuration Exception occurred.", "Knowledge Base Exception occurred.", JOptionPane.INFORMATION_MESSAGE);
		} catch (KnowledgeBaseException e) {
			JOptionPane.showMessageDialog(null, "Knowledge Base Exception occurred.", "Knowledge Base Exception occurred.", JOptionPane.INFORMATION_MESSAGE);
		}

		restartLearningThread();

		updateBoard();
	}

	public synchronized void restartLearningThread() {
		try {
			mFixedDurationLearningThreadManager = FixedDurationLearningThreadManager.getInstance();
			
			if (mGivenMoveFixedDurationLearningThread != null && mGivenMoveFixedDurationLearningThread != null) {
				mFixedDurationLearningThreadManager.deregisterFixedDurationLearningThread(mGivenMoveFixedDurationLearningThread);
			}
			if (mCurrentBoardState != null && mFixedDurationLearningThreadManager != null) {
				try {
					mGivenMoveFixedDurationLearningThread = GivenMoveFixedDurationLearningThread.getInstance(mKnowledgeBaseFilePool, mCurrentBoardState, 0, null);
					mGivenMoveFixedDurationLearningThread.start();
				} catch (ConfigurationException e) {
					sLogger.error("Could not create GivenMoveFixedDurationLearningThread");
				}
				
			}
		} catch (ConfigurationException e1) {

		} catch (IOException e1) {

		}
	}

	public synchronized void restartAutomaticMoveThread() {
		try {
			mAutomaticMoveThreadManager = AutomaticMoveThreadManager.getInstance();

			if (mAutomaticMoveThreadManager != null && mAutomaticMoveThread != null) {
				mAutomaticMoveThreadManager.deregisterAutomaticMoveThread(mAutomaticMoveThread);
			}
			if (mCurrentBoardState != null && mAutomaticMoveThreadManager != null) {
				try {
					mAutomaticMoveThread = BestMoveAutomaticMoveThread.getInstance (mKnowledgeBaseFilePool, mCurrentBoardState, 10000, this, null);
					mAutomaticMoveThread.start();
				} catch (ConfigurationException e) {
					sLogger.error("Could not create GivenMoveFixedDurationLearningThread");
				}
			}
		} catch (ConfigurationException e1) {

		} catch (IOException e1) {

		}
	}


public synchronized void stopLearningThread() {
	try {
		mFixedDurationLearningThreadManager = FixedDurationLearningThreadManager.getInstance();
		if (mGivenMoveFixedDurationLearningThread != null && mGivenMoveFixedDurationLearningThread != null) {
			mFixedDurationLearningThreadManager.deregisterFixedDurationLearningThread(mGivenMoveFixedDurationLearningThread);
		}
	} catch (ConfigurationException e1) {

	} catch (IOException e1) {

	}
}


	public synchronized void stopAutomaticMoveThread() {
		try {
			mAutomaticMoveThreadManager = AutomaticMoveThreadManager.getInstance();
			if (mAutomaticMoveThreadManager != null && mAutomaticMoveThread != null) {
				mAutomaticMoveThreadManager.deregisterAutomaticMoveThread(mAutomaticMoveThread);
			}
		} catch (ConfigurationException e1) {

		} catch (IOException e1) {

		}
	}

	public synchronized void updateBoard() {
		mCurrentBoardState.logBoardState(Level.FATAL);
		for (int lRow = 0; lRow < YSIZE; lRow++) {
			for (int lColumn = 0; lColumn < XSIZE; lColumn++) {
				OldBoardState.CellState lCurrentCellState = mCurrentBoardState.getCellState(lColumn, lRow);
				if (lCurrentCellState == OldBoardState.CellState.SELF_OCCUPIED) {
					mSlots[lColumn][lRow].setOpaque(true);
					mSlots[lColumn][lRow].setBackground(Color.red);
					mSlots[lColumn][lRow].setForeground(Color.BLACK);
					mSlots[lColumn][lRow].setText("X");
				} else if (lCurrentCellState == OldBoardState.CellState.OPPONENT_OCCUPIED) {
					mSlots[lColumn][lRow].setOpaque(true);
					mSlots[lColumn][lRow].setBackground(Color.blue);
					mSlots[lColumn][lRow].setForeground(Color.WHITE);
					mSlots[lColumn][lRow].setText("O");
				} else {
					mSlots[lColumn][lRow].setOpaque(true);
					mSlots[lColumn][lRow].setBackground(Color.lightGray);
					mSlots[lColumn][lRow].setForeground(Color.BLACK);
					mSlots[lColumn][lRow].setText(String.valueOf(lColumn) + "," + String.valueOf(lRow));
					mSlots[lColumn][lRow].setFont(mSlots[lColumn][lRow].getFont().deriveFont(24.0f));
				}
			}
		}

//		for (int i = 0; i < XSIZE; i++) {
//			mButtons[i].setText(i + 1 + "(" + "?" + ")");
//		}

		if (mCurrentSubBoardStates != null && !mCurrentSubBoardStates.isEmpty()) {
			for (Iterator<OldBoardState> lIterator = mCurrentSubBoardStates.iterator(); lIterator.hasNext();) {
				OldBoardState lCurrentSubBoardState = lIterator.next();
				int lButtonIndex = lCurrentSubBoardState.getMove().getMoveIntValue() - 1;
				byte lCurrentSubBoardStateScore = lCurrentSubBoardState.getMoveScore().getMoveScore();
//				mButtons[lButtonIndex].setText(lButtonIndex + "(" + lCurrentSubBoardStateScore + ")");
			}
		}

//		mMovesTextField.setText(mCurrentBoardState.getFileIndexString());
//		mMovesTextField.requestFocus();

	}

	static final String newline = System.getProperty("line.separator");

	private void displayInfo(KeyEvent e, String keyStatus) {

		// You should only rely on the key char if the event
		// is a key typed event.
		int id = e.getID();
		String keyString;
		if (id == KeyEvent.KEY_TYPED) {
			char c = e.getKeyChar();
			keyString = "key character = '" + c + "'";
		} else {
			int keyCode = e.getKeyCode();
			keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")";
		}

		int modifiersEx = e.getModifiersEx();
		String modString = "extended modifiers = " + modifiersEx;
		String tmpString = KeyEvent.getModifiersExText(modifiersEx);
		if (tmpString.length() > 0) {
			modString += " (" + tmpString + ")";
		} else {
			modString += " (no extended modifiers)";
		}

		String actionString = "action key? ";
		if (e.isActionKey()) {
			actionString += "YES";
		} else {
			actionString += "NO";
		}

		String locationString = "key location: ";
		int location = e.getKeyLocation();
		if (location == KeyEvent.KEY_LOCATION_STANDARD) {
			locationString += "standard";
		} else if (location == KeyEvent.KEY_LOCATION_LEFT) {
			locationString += "left";
		} else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
			locationString += "right";
		} else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
			locationString += "numpad";
		} else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
			locationString += "unknown";
		}

		mDisplayTextField.append(keyStatus + newline + "    " + keyString + newline + "    " + modString + newline + "    " + actionString + newline + "    "
				+ locationString + newline);
		mDisplayTextField.setCaretPosition(mDisplayTextField.getDocument().getLength());
	}

	public static void main(String[] args) {
		try {
			Connect4GUI Gui = new Connect4GUI();
		} catch (InvalidMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KnowledgeBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
