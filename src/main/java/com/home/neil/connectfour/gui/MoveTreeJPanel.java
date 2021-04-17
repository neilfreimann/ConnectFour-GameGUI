package com.home.neil.connectfour.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.boardstate.old.InvalidMoveException;
import com.home.neil.connectfour.boardstate.old.OldBoardState;
import com.home.neil.connectfour.boardstate.old.expansiontask.ExpansionTask;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.knowledgebase.exception.KnowledgeBaseException;



public class MoveTreeJPanel extends JPanel implements TreeSelectionListener, TreeExpansionListener {
	public static final String CLASS_NAME = MoveTreeJPanel.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + MoveTreeJPanel.class.getSimpleName();

	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private JTree mMoveTree;
	private DefaultTreeModel mModel;
	private OldBoardState mRootBoardState = null;
	private DefaultMutableTreeNode mRootBoardStateNode = null;
	
	private KnowledgeBaseFilePool mKnowledgeBaseFilePool = null;

	public MoveTreeJPanel() throws ConfigurationException, InvalidMoveException, KnowledgeBaseException, IOException {

		super(new GridLayout(1, 0));

		mKnowledgeBaseFilePool = KnowledgeBaseFilePool.getMasterInstance();
		
		// Create the nodes.
		createInitialNodes();

		// Create a tree that allows one selection at a time.
		mModel = new DefaultTreeModel(mRootBoardStateNode);
		mMoveTree = new JTree(mModel);
		mMoveTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Listen for when the selection changes.
		mMoveTree.addTreeSelectionListener(this);
		mMoveTree.addTreeExpansionListener(this);

		// Create the scroll pane and add the tree to it.
		JScrollPane lTreeView = new JScrollPane(mMoveTree);

		Dimension lMinimumSize = new Dimension(300, 300);
		mMoveTree.setMinimumSize(lMinimumSize);

		// Add the split pane to this panel.
		add(lTreeView);

		setSize(1000, 1000);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		sLogger.trace("Entering");

		DefaultMutableTreeNode lNode = (DefaultMutableTreeNode) mMoveTree.getLastSelectedPathComponent();

		if (lNode == null)
			return;

		sLogger.error((lNode.getUserObject()));
		sLogger.trace("Exiting");
	}

	private void createInitialNodes() throws ConfigurationException, InvalidMoveException, KnowledgeBaseException {
		sLogger.trace("Entering");

		mRootBoardState = new OldBoardState(mKnowledgeBaseFilePool, OldBoardState.Move.OPPONENT_NOMOVE, true, null);
		mRootBoardStateNode = new DefaultMutableTreeNode(mRootBoardState);

		ExpansionTask lExpansionTask = new ExpansionTask(mRootBoardState, null);
		boolean lSuccess = lExpansionTask.executeTask();

		if (!lSuccess) {
			sLogger.trace("Exiting");
			return;
		}

		ArrayList<OldBoardState> lSubBoardStates = lExpansionTask.getSubBoardStates();

		sortSubBoardState(lSubBoardStates);

		for (Iterator<OldBoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
			OldBoardState lCurrentBoardState = lIterator.next();

			DefaultMutableTreeNode lCurrentBoardStateNode = new DefaultMutableTreeNode(lCurrentBoardState);

			mRootBoardStateNode.add(lCurrentBoardStateNode);

			ExpansionTask lExpansionTask2 = new ExpansionTask(lCurrentBoardState, null);
			lExpansionTask2.executeTask();

			if (!lSuccess) {
				sLogger.trace("Exiting");
				return;
			}

			ArrayList<OldBoardState> lSubBoardStates2 = lExpansionTask2.getSubBoardStates();

			sortSubBoardState(lSubBoardStates2);

			for (Iterator<OldBoardState> lIterator2 = lSubBoardStates2.iterator(); lIterator2.hasNext();) {
				OldBoardState lCurrentBoardState2 = lIterator2.next();

				DefaultMutableTreeNode lCurrentBoardStateNode2 = new DefaultMutableTreeNode(lCurrentBoardState2);

				lCurrentBoardStateNode.add(lCurrentBoardStateNode2);

			}

		}

		sLogger.trace("Exiting");
	}

	public void sortSubBoardState(List<OldBoardState> pBoardStates) {
		Collections.sort(pBoardStates, new Comparator<OldBoardState>() {
			public int compare(OldBoardState p1, OldBoardState p2) {
				int lMove1Value = p1.getMove().getMoveIntValue();
				int lMove2Value = p2.getMove().getMoveIntValue();
				if (lMove1Value > lMove2Value)
					return 1;
				else if (lMove1Value < lMove2Value)
					return -1;
				else
					return 0;
			}
		});
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 * 
	 * @throws KnowledgeBaseException
	 * @throws InvalidMoveException
	 * @throws ConfigurationException
	 * @throws IOException 
	 */
	private static void createAndShowGUI() throws ConfigurationException, InvalidMoveException, KnowledgeBaseException, IOException {

		// Create and set up the window.
		JFrame frame = new JFrame("MoveTree");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(new MoveTreeJPanel());

		// Display the window.
		frame.setSize(1000, 1000);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					StringWriter lSW = new StringWriter();
					PrintWriter lPW = new PrintWriter(lSW);
					e.printStackTrace(lPW);
					lSW.toString(); // stack trace as a string
					sLogger.debug("StackTrace: " + lSW);
				} catch (InvalidMoveException e) {
					// TODO Auto-generated catch block
					StringWriter lSW = new StringWriter();
					PrintWriter lPW = new PrintWriter(lSW);
					e.printStackTrace(lPW);
					lSW.toString(); // stack trace as a string
					sLogger.debug("StackTrace: " + lSW);
				} catch (KnowledgeBaseException e) {
					// TODO Auto-generated catch block
					StringWriter lSW = new StringWriter();
					PrintWriter lPW = new PrintWriter(lSW);
					e.printStackTrace(lPW);
					lSW.toString(); // stack trace as a string
					sLogger.debug("StackTrace: " + lSW);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					StringWriter lSW = new StringWriter();
					PrintWriter lPW = new PrintWriter(lSW);
					e.printStackTrace(lPW);
					lSW.toString(); // stack trace as a string
					sLogger.debug("StackTrace: " + lSW);
				}
			}
		});
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent pArg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeExpanded(TreeExpansionEvent pTreeExpansionEvent) {
		sLogger.trace("Entering");

		TreePath lTreePath = pTreeExpansionEvent.getPath();

		DefaultTreeModel lModel = ((DefaultTreeModel) mMoveTree.getModel());

		DefaultMutableTreeNode lNode = (DefaultMutableTreeNode) lTreePath.getLastPathComponent();
		OldBoardState lExpandedNode = (OldBoardState) lNode.getUserObject();

		for (Enumeration lEnum = lNode.children(); lEnum.hasMoreElements();) {

			DefaultMutableTreeNode lNodeChild = (DefaultMutableTreeNode) lEnum.nextElement();

			lNodeChild.removeAllChildren();
			
			OldBoardState lCurrentBoardState = (OldBoardState) lNodeChild.getUserObject();

			ExpansionTask lExpansionTask2 = new ExpansionTask(lCurrentBoardState, null);
			boolean lSuccess = lExpansionTask2.executeTask();

			if (!lSuccess) {
				sLogger.trace("Exiting");
				return;
			}
			
			lNodeChild.setUserObject(lNodeChild.getUserObject());
			lModel.nodeChanged(lNodeChild);
			
			ArrayList<OldBoardState> lSubBoardStates2 = lExpansionTask2.getSubBoardStates();
			if (lSubBoardStates2 != null && !lSubBoardStates2.isEmpty()) {
				sortSubBoardState(lSubBoardStates2);

				for (Iterator<OldBoardState> lIterator2 = lSubBoardStates2.iterator(); lIterator2.hasNext();) {
					OldBoardState lCurrentBoardState2 = lIterator2.next();

					DefaultMutableTreeNode lCurrentBoardStateNode2 = new DefaultMutableTreeNode(lCurrentBoardState2);

					lNodeChild.add(lCurrentBoardStateNode2);

				}
			}
		}

		lModel.nodeStructureChanged(lNode);
		
		while (lNode != null) {
			lNode.setUserObject(lNode.getUserObject());
			
			lModel.nodeChanged(lNode);
			
			for (Enumeration lEnum = lNode.children(); lEnum.hasMoreElements();) {

				DefaultMutableTreeNode lNodeChild = (DefaultMutableTreeNode) lEnum.nextElement();

				lNodeChild.setUserObject(lNodeChild.getUserObject());
				
				lModel.nodeChanged(lNodeChild);
			}
			
			lNode = (DefaultMutableTreeNode) lNode.getParent();
		}


		sLogger.trace("Exiting");

	}
}
