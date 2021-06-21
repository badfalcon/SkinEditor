package com.gmail.badfalcon610;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

public class RGBPanel extends JPanel {

	public static void main(String[] args) {
		new TestFrame(new RGBPanel());
	}

	Color color;
	int[] rgb;

	MYColorChooserGB mccgb;
	MYColorChooserR mccr;

	public RGBPanel(Color c) {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JPanel hspanel = new JPanel();
		mccgb = new MYColorChooserGB();
		hspanel.add(mccgb);
		JPanel bpanel = new JPanel();
		mccr = new MYColorChooserR();
		bpanel.add(mccr);
		add(hspanel);
		add(bpanel);
	}

	public RGBPanel() {
		rgb = new int[] { 255, 0, 0 };
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JPanel hspanel = new JPanel();
		mccgb = new MYColorChooserGB();
		hspanel.add(mccgb);
		JPanel bpanel = new JPanel();
		mccr = new MYColorChooserR();
		bpanel.add(mccr);
		layout.putConstraint(SpringLayout.NORTH, hspanel, 0,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, hspanel, 0, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, bpanel, 0, SpringLayout.NORTH,
				this);
		layout.putConstraint(SpringLayout.WEST, bpanel, 0, SpringLayout.EAST,
				hspanel);
		add(hspanel);
		add(bpanel);
		setPreferredSize(new Dimension(295, 265));
	}

	public void setColor(Color c) {
		this.color = c;
		rgb = new int[] { color.getRed(), color.getGreen(), color.getBlue(), };
		repaint();
	}

	public void setColor(int[] rgb) {
		this.color = new Color(rgb[0], rgb[1], rgb[2]);
		this.rgb = rgb;
		repaint();
	}

	public class MYColorChooserGB extends JPanel {

		int greenMax = 255;
		int blueMax = 255;
		int Height = (int) blueMax;
		int Width = (int) greenMax;

		public MYColorChooserGB() {
			addMouseListener(new ColorSliderListener());
			addMouseMotionListener(new ColorSliderListener());
			setBorder(new BevelBorder(BevelBorder.LOWERED));
			setPreferredSize(new Dimension(Width, Height));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D display = (Graphics2D) g.create();
			for (int y = 0; y < blueMax; y++) {
				for (int x = 0; x < greenMax; x++) {
					display.setPaint(new Color(rgb[0], x, blueMax - y));
					display.fillRect(x, y, 1, 1);
				}
			}
			display.setPaint(new Color(0, 0, 0));
			display.drawOval(rgb[1] - 5, blueMax - rgb[2] - 5, 10, 10);
			display.drawOval(rgb[1] - 6, blueMax - rgb[2] - 6, 12, 12);
			display.dispose();
		}

		public class ColorSliderListener extends MouseAdapter {
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (x < 0) {
					rgb[1] = 0;
				} else if (0 <= x && x < Width + 1) {
					rgb[1] = x;
				} else if (Width + 1 <= x) {
					rgb[1] = greenMax;
				}
				if (y < 0) {
					rgb[2] = blueMax;
				} else if (0 <= y && y < Height + 1) {
					rgb[2] = blueMax - y;
				} else if (Height + 1 <= y) {
					rgb[2] = 0;
				}
				repaint();
				mccr.repaint();
				ColorChooser.setMainColor(rgb);
			}

			public void mouseDragged(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (x < 0) {
					rgb[1] = 0;
				} else if (0 <= x && x < Width + 1) {
					rgb[1] = x;
				} else if (Width + 1 <= x) {
					rgb[1] = greenMax;
				}
				if (y < 0) {
					rgb[2] = blueMax;
				} else if (0 <= y && y < Height + 1) {
					rgb[2] = blueMax - y;
				} else if (Height + 1 <= y) {
					rgb[2] = 0;
				}
				repaint();
				mccr.repaint();
				ColorChooser.setMainColor(rgb);
			}
		}
	}

	public class MYColorChooserR extends JPanel {
		int redMax = 255;
		int Width = 20;
		int Height = (int) redMax;

		public MYColorChooserR() {
			addMouseListener(new ColorSliderBListener());
			addMouseMotionListener(new ColorSliderBListener());
			setBorder(new BevelBorder(BevelBorder.LOWERED));
			setPreferredSize(new Dimension(Width, Height));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D display = (Graphics2D) g.create();
			for (int i = 0; i < redMax; i++) {
				display.setPaint(new Color(redMax - i, rgb[1], rgb[2]));
				display.drawLine(0, i, Width, i);
			}
			display.setPaint(new Color(255, 255, 255));
			display.drawLine(0, redMax - rgb[0], Width, redMax - rgb[0]);
			display.dispose();
		}

		public class ColorSliderBListener extends MouseAdapter {
			public void mouseClicked(MouseEvent e) {
				int y = e.getY();
				if (y < 0) {
					rgb[0] = redMax;
				} else if (0 <= y && y < Height + 1) {
					rgb[0] = redMax - y;
					;
				} else if (Height + 1 <= y) {
					rgb[0] = 0;
				}
				repaint();
				mccgb.repaint();
				ColorChooser.setMainColor(rgb);
			}

			public void mouseDragged(MouseEvent e) {
				int y = e.getY();
				if (y < 0) {
					rgb[0] = redMax;
				} else if (0 <= y && y < Height + 1) {
					rgb[0] = redMax - y;
					;
				} else if (Height + 1 <= y) {
					rgb[0] = 0;
				}
				repaint();
				mccgb.repaint();
				ColorChooser.setMainColor(rgb);
			}
		}

	}

}
