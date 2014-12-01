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

public class HSBPanel extends JPanel {

	public static void main(String[] args) {
		new TestFrame(new HSBPanel());
	}

	Color color;
	float[] hsb;

	MYColorChooserHS mcchs;
	MYColorChooserB mccb;

	public HSBPanel(Color c) {

		color = c;
		hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JPanel hspanel = new JPanel();
		mcchs = new MYColorChooserHS();
		hspanel.add(mcchs);
		JPanel bpanel = new JPanel();
		mccb = new MYColorChooserB();
		bpanel.add(mccb);
		add(hspanel);
		add(bpanel);
	}

	public HSBPanel() {
		hsb = new float[3];
		hsb[0] = 0.0f;
		hsb[1] = 1.0f;
		hsb[2] = 0.0f;

		SpringLayout layout = new SpringLayout();
		setLayout(layout);
//		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JPanel hspanel = new JPanel();
		mcchs = new MYColorChooserHS();
		hspanel.add(mcchs);
		JPanel bpanel = new JPanel();
		mccb = new MYColorChooserB();
		bpanel.add(mccb);
		layout.putConstraint(SpringLayout.NORTH, hspanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, hspanel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, bpanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, bpanel, 0, SpringLayout.EAST, hspanel);
		add(hspanel);
		add(bpanel);
		setPreferredSize(new Dimension(280,250));
	}

	public void setColor(Color c) {
		this.color = c;
		hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		repaint();
	}

	public void setColor(float[] hsbc) {
		this.color = new Color(Color.HSBtoRGB(hsbc[0], hsbc[1], hsbc[2]));
		hsb = hsbc;
		repaint();
	}

	public class MYColorChooserHS extends JPanel {

		float hueMax = 240;
		float saurationMax = 240;
		int Height = (int) saurationMax;
		int Width = (int) hueMax;

		public MYColorChooserHS() {
			addMouseListener(new ColorSliderListener());
			addMouseMotionListener(new ColorSliderListener());
			setBorder(new BevelBorder(BevelBorder.LOWERED));
			setPreferredSize(new Dimension(Width, Height));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D display = (Graphics2D) g.create();
			for (int j = 0; j < saurationMax; j++) {
				for (int i = 0; i < hueMax; i++) {
					display.setPaint(new Color(Color.HSBtoRGB(i / hueMax,
							1 - (j / saurationMax), 1.0f)));
					display.fillRect(i, j, 1, 1);
				}
			}
			display.setPaint(new Color(0, 0, 0));
			display.drawOval((int) (hsb[0] * hueMax - 5),
					(int) ((1.0f - hsb[1]) * saurationMax - 5), 10, 10);
			display.drawOval((int) (hsb[0] * hueMax - 6),
					(int) ((1.0f - hsb[1]) * saurationMax - 6), 12, 12);
			display.dispose();
		}

		public class ColorSliderListener extends MouseAdapter {
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				if (x < 0) {
					hsb[0] = 0.0f;
				} else if (0 <= x && x < Width + 1) {
					hsb[0] = x / hueMax;
				} else if (Width + 1 <= x) {
					hsb[0] = 1.0f;
				}
				if (y < 0) {
					hsb[1] = 1.0f;
				} else if (0 <= y && y < Height + 1) {
					hsb[1] = 1.0f - y / saurationMax;
				} else if (Height + 1 <= y) {
					hsb[1] = 0.0f;
				}
				repaint();
				mccb.repaint();
				ColorChooser.setMainColor(hsb);
			}

			public void mouseDragged(MouseEvent e) {
				U.say("dragged");
				int x = e.getX();
				int y = e.getY();
				if (x < 0) {
					hsb[0] = 0.0f;
				} else if (0 <= x && x < Width + 1) {
					hsb[0] = x / hueMax;
				} else if (Width + 1 <= x) {
					hsb[0] = 1.0f;
				}
				if (y < 0) {
					hsb[1] = 1.0f;
				} else if (0 <= y && y < Height + 1) {
					hsb[1] = 1.0f - y / saurationMax;
				} else if (Height + 1 <= y) {
					hsb[1] = 0.0f;
				}
				repaint();
				mccb.repaint();
				ColorChooser.setMainColor(hsb);
			}
		}
	}

	public class MYColorChooserB extends JPanel {
		float brightnessMax = 240;
		int Width = 20;
		int Height = (int) brightnessMax;

		public MYColorChooserB() {
			addMouseListener(new ColorSliderBListener());
			addMouseMotionListener(new ColorSliderBListener());
			setBorder(new BevelBorder(BevelBorder.LOWERED));
			setPreferredSize(new Dimension(Width, Height));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D display = (Graphics2D) g.create();
			for (int i = 0; i < brightnessMax; i++) {
				display.setPaint(new Color(Color.HSBtoRGB(hsb[0], hsb[1],
						1 - (i / brightnessMax))));
				display.drawLine(0, i, Width, i);
			}
			display.setPaint(new Color(255, 255, 255));
			display.drawLine(0, (int) ((1.0f - hsb[2]) * brightnessMax), Width,
					(int) ((1.0f - hsb[2]) * brightnessMax));
			display.dispose();
		}

		public class ColorSliderBListener extends MouseAdapter {
			public void mouseClicked(MouseEvent e) {
				int y = e.getY();
				if (y < 0) {
					hsb[2] = 1.0f;
				} else if (0 <= y && y < Height + 1) {
					hsb[2] = 1.0f - y / brightnessMax;
				} else if (Height + 1 <= y) {
					hsb[2] = 0.0f;
				}
				repaint();
				mcchs.repaint();
				ColorChooser.setMainColor(hsb);
			}

			public void mouseDragged(MouseEvent e) {
				int y = e.getY();
				if (y < 0) {
					hsb[2] = 1.0f;
				} else if (0 <= y && y < Height + 1) {
					hsb[2] = 1.0f - y / brightnessMax;
				} else if (Height + 1 <= y) {
					hsb[2] = 0.0f;
				}
				repaint();
				mcchs.repaint();
				U.say("hsb");
				U.say(hsb[0]);
				U.say(hsb[1]);
				U.say(hsb[2]);
				ColorChooser.setMainColor(hsb);
			}
		}

	}

}
