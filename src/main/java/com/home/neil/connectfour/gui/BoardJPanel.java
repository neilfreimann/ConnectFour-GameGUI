package com.home.neil.connectfour.gui;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import java.awt.Font;

public class BoardJPanel  extends JPanel {
	public BoardJPanel() {
		setLayout(new GridLayout(6, 7, 0, 0));
		
		mSlot16 = new JLabel("(1,6)");
		mSlot16.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mSlot16.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot16);
		
		mSlot26 = new JLabel("(2,6)");
		mSlot26.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mSlot26.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot26);
		
		mSlot36 = new JLabel("(3,6)");
		mSlot36.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mSlot36.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot36);
		
		mSlot46 = new JLabel("(4,6)");
		mSlot46.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mSlot46.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot46);
		
		mSlot56 = new JLabel("(5,6)");
		mSlot56.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mSlot56.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot56);
		
		mSlot66 = new JLabel("(6,6)");
		mSlot66.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mSlot66.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot66);
		
		mSlot76 = new JLabel("(7,6)");
		mSlot76.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mSlot76.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot76);
		
		mSlot15 = new JLabel("(1,5)");
		mSlot15.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mSlot15.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot15);
		
		mSlot25 = new JLabel("(2,5)");
		mSlot25.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot25);
		
		mSlot35 = new JLabel("(3,5)");
		mSlot35.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot35);
		
		mSlot45 = new JLabel("(4,5)");
		mSlot45.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot45);
		
		mSlot55 = new JLabel("(5,5)");
		mSlot55.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot55);
		
		mSlot65 = new JLabel("(6,5)");
		mSlot65.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot65);
		
		mSlot75 = new JLabel("(7,5)");
		mSlot75.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot75);
		
		mSlot14 = new JLabel("(1,4)");
		mSlot14.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot14);
		
		mSlot24 = new JLabel("(2,4)");
		mSlot24.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot24);
		
		mSlot34 = new JLabel("(3,4)");
		mSlot34.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot34);
		
		mSlot44 = new JLabel("(4,4)");
		mSlot44.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot44);
		
		mSlot54 = new JLabel("(5,4)");
		mSlot54.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot54);
		
		mSlot64 = new JLabel("(6,4)");
		mSlot64.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot64);
		
		mSlot74 = new JLabel("(7,4)");
		mSlot74.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot74);
		
		mSlot13 = new JLabel("(1,3)");
		mSlot13.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot13);
		
		mSlot23 = new JLabel("(2,3)");
		mSlot23.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot23);
		
		mSlot33 = new JLabel("(3,3)");
		mSlot33.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot33);
		
		mSlot43 = new JLabel("(4,3)");
		mSlot43.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot43);
		
		mSlot53 = new JLabel("(5,3)");
		mSlot53.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot53);
		
		mSlot63 = new JLabel("(6,3)");
		mSlot63.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot63);
		
		mSlot73 = new JLabel("(7,3)");
		mSlot73.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot73);
		
		mSlot12 = new JLabel("(1,2)");
		mSlot12.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot12);
		
		mSlot22 = new JLabel("(2,2)");
		mSlot22.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot22);
		
		mSlot32 = new JLabel("(3,2)");
		mSlot32.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot32);
		
		mSlot42 = new JLabel("(4,2)");
		mSlot42.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot42);
		
		mSlot52 = new JLabel("(5,2)");
		mSlot52.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot52);
		
		mSlot62 = new JLabel("(6,2)");
		mSlot62.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot62);
		
		mSlot72 = new JLabel("(7,2)");
		mSlot72.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot72);
		
		mSlot11 = new JLabel("(1,1)");
		mSlot11.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot11);
		
		mSlot21 = new JLabel("(2,1)");
		mSlot21.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot21);
		
		mSlot31 = new JLabel("(3,1)");
		mSlot31.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot31);
		
		mSlot41 = new JLabel("(4,1)");
		mSlot41.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot41);
		
		mSlot51 = new JLabel("(5,1)");
		mSlot51.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot51);
		
		mSlot61 = new JLabel("(6,1)");
		mSlot61.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot61);
		
		mSlot71 = new JLabel("(7,1)");
		mSlot71.setHorizontalAlignment(SwingConstants.CENTER);
		add(mSlot71);
	}
	public static final String CLASS_NAME = HumanInputJPanel.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private JLabel mSlot16;
	private JLabel mSlot71;
	private JLabel mSlot61;
	private JLabel mSlot51;
	private JLabel mSlot41;
	private JLabel mSlot31;
	private JLabel mSlot26;
	private JLabel mSlot36;
	private JLabel mSlot46;
	private JLabel mSlot66;
	private JLabel mSlot56;
	private JLabel mSlot15;
	private JLabel mSlot76;
	private JLabel mSlot35;
	private JLabel mSlot55;
	private JLabel mSlot25;
	private JLabel mSlot75;
	private JLabel mSlot65;
	private JLabel mSlot14;
	private JLabel mSlot24;
	private JLabel mSlot34;
	private JLabel mSlot53;
	private JLabel mSlot73;
	private JLabel mSlot63;
	private JLabel mSlot12;
	private JLabel mSlot22;
	private JLabel mSlot32;
	private JLabel mSlot42;
	private JLabel mSlot52;
	private JLabel mSlot72;
	private JLabel mSlot11;
	private JLabel mSlot21;
	private JLabel mSlot43;
	private JLabel mSlot33;
	private JLabel mSlot23;
	private JLabel mSlot44;
	private JLabel mSlot54;
	private JLabel mSlot64;
	private JLabel mSlot74;
	private JLabel mSlot13;
	private JLabel mSlot45;
	private JLabel mSlot62;


}
