package com.gmail.badfalcon610;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Check extends JFrame {

	BufferedImage[] source;

	public Check() {
		int sourcew = 0;
		int sourceh = 0;
		for (BufferedImage s : source) {
			sourcew += s.getWidth();
			sourceh += s.getHeight();
		}


		setPreferredSize(new Dimension(sourcew, sourceh));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public Check(BufferedImage[] source) {
		this.source = source;
		new Check();
	}
}
