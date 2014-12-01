package com.gmail.badfalcon610;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class HSBcircle extends JPanel {

	public static void main(String[] args) {
		new TestFrame(new HSBcircle(300, 10));
	}

	int r;
	int d;

	public HSBcircle(int r, int d) {
		this.r = r;
		this.d = d;
		setPreferredSize(new Dimension(r,r));
	}

	final int N = 360;
	final int ANGLE = 360 / N;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D display = (Graphics2D) g.create();
		for (int i = 0; i < N; i++) {
			display.setPaint(Color.getHSBColor((float) i / (float) N, 1.0F, 1.0F));
			display.fillArc(0, 0, r, r, i * ANGLE, ANGLE);
		}
		display.setPaint(new Color(0,0,0,255));
		display.fillArc(d, d, r - d * 2, r - d * 2, 0, 360);
	}

	public void WhiteToNone() {
	}

}
