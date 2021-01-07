package com.home.neil.connectfour.gui.example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class CountdownTimer extends JFrame {
	private JTextField mTextField;
	private Timer mTimer;
	private JButton mResetButton;

	private int mNumberOfMilliseconds = 60;
	
	public CountdownTimer(int pNumberOfMilliseconds) {
		super("Countdown timer");
		
		mNumberOfMilliseconds = pNumberOfMilliseconds;
		
		long lStartTime = System.currentTimeMillis();
		long lLimitTime = mNumberOfMilliseconds * 1000;
		
		long lRemaining = lLimitTime;
		
		long lSeconds = lRemaining / 1000;
		long lMinutes = lSeconds / 60;
		long lHours = lMinutes / 60;
		mTextField.setText(String.format("%02d:%02d:%02d", lHours, lMinutes, lSeconds % 60));
		
				
		mTextField = new JTextField("2", 8);		
		mResetButton = new JButton("Reset");
		
		
		
		
		mResetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent click) {
				final long current = System.currentTimeMillis();
				try {
					final long limit = Integer.parseInt(mTextField.getText().trim()) * 1000; // X
																						// seconds
					mTimer = new Timer(1000, new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							long time = System.currentTimeMillis();
							long passed = time - current;
							long remaining = limit - passed;
							if (remaining <= 0) {
								mTextField.setText("2");
								mTimer.stop();
							} else {
								long seconds = remaining / 1000;
								long minutes = seconds / 60;
								long hours = minutes / 60;
								mTextField.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds % 60));
							}
						}
					});
					mTimer.start();
				} catch (NumberFormatException nfe) {
					// debug/report here
					nfe.printStackTrace();
				}
			}
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(mTextField);
		panel.add(new JLabel(" seconds"));
		panel.add(mResetButton);
		add(panel);
	}
	
	

	public static void main(String[] args) throws Exception {
		CountdownTimer frame = new CountdownTimer(60);
		frame.setDefaultCloseOperation(CountdownTimer.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}