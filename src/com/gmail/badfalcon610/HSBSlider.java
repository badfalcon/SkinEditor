package com.gmail.badfalcon610;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

public class HSBSlider extends JPanel {

	public static void main(String[] args) {
		HSBSlider hsb = new HSBSlider();
		TestFrame tf = new TestFrame(hsb);
		tf.pack();
	}

	public HSBSlider() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		JLabel RL = new JLabel("R");
		JPanel PR = new JPanel();
		PR.setPreferredSize(new Dimension(256, 20));
		PR.setBackground(Color.red);
		JTextArea RTA = new JTextArea(String.valueOf(255));

		layout.putConstraint(SpringLayout.WEST, RL, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.WEST, PR, 0, SpringLayout.EAST, RL);
		layout.putConstraint(SpringLayout.WEST, RTA, 0, SpringLayout.EAST, PR);
		layout.putConstraint(SpringLayout.NORTH, PR, 0, SpringLayout.NORTH,
				this);
		layout.putConstraint(SpringLayout.NORTH, RL, 0, SpringLayout.NORTH, PR);
		layout.putConstraint(SpringLayout.NORTH, RTA, 0, SpringLayout.NORTH, PR);

		add(RL);
		add(PR);
		add(RTA);
		/*
		 * add(new JLabel("G")); add(new JPanel()); add(new
		 * JTextArea(String.valueOf(254))); add(new JLabel("B")); add(new
		 * JPanel()); add(new JTextArea(String.valueOf(253)));
		 */

	}

}
