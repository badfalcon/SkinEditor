package com.gmail.badfalcon610;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class RGBSlider extends JPanel {

	int[] rgb;

	static final int R = 0;
	static final int G = 1;
	static final int B = 2;

	RGBLine red;
	RGBLine green;
	RGBLine blue;

	public static void main(String[] args) {
		RGBSlider rgbslider = new RGBSlider();
		TestFrame tf = new TestFrame(rgbslider);
		tf.pack();
	}

	public RGBSlider() {
		rgb = new int[] { 255, 0, 0 };

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		JPanel p = new JPanel();
		p.setMinimumSize(new Dimension(20, 20));
		p.setMaximumSize(new Dimension(20, 20));
		JPanel p0 = new JPanel();
		p0.setMinimumSize(new Dimension(20, 20));
		p0.setMaximumSize(new Dimension(20, 20));
		JPanel p1 = new JPanel();
		p1.setMinimumSize(new Dimension(20, 20));
		p1.setMaximumSize(new Dimension(20, 20));
		red = new RGBLine("R", R, Color.RED);
		green = new RGBLine("G", G, Color.GREEN);
		blue = new RGBLine("B", B, Color.BLUE);
		add(p);
		add(red);
		add(p0);
		add(green);
		add(p1);
		add(blue);
		// add(table);

	}

	public void setColor(Color c) {
		rgb = new int[] { c.getRed(), c.getGreen(), c.getBlue() };
		setText();
		repaint();
	}

	public void setColor(int[] rgb) {
		this.rgb = rgb;
		setText();
		repaint();
	}

	public void setText() {
		red.field.setText(String.valueOf(rgb[0]));
		green.field.setText(String.valueOf(rgb[1]));
		blue.field.setText(String.valueOf(rgb[2]));
	}

	public class RGBLine extends JPanel {

		String s;

		int n;

		JLabel label;
		JPanel slider;
		JTextField field;

		public RGBLine(String s, int n, Color c) {
			this.s = s;
			this.n = n;

			BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			setLayout(layout);

			label = new JLabel(s);
			slider = new Slider(n, new Color[] { Color.BLACK, c });
			field = new JTextField(String.valueOf(rgb[n]));
			field.addActionListener(new MyActionListener());
			field.getDocument().addDocumentListener(new MyDocumentListener());
			field.setName(String.valueOf(n));
			field.setHorizontalAlignment(JTextField.RIGHT);
			field.setInputVerifier(new IntegerInputVerifier());
			field.setBorder(new BevelBorder(BevelBorder.LOWERED));
			field.setMaximumSize(new Dimension(30, 20));

			add(label);
			add(slider);
			add(field);
			setPreferredSize(new Dimension(289, 75));

		}

		public class MyDocumentListener implements DocumentListener {

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}
		}

		public class IntegerInputVerifier extends InputVerifier {
			@Override
			public boolean verify(JComponent c) {
				boolean verified = false;
				JTextField textField = (JTextField) c;
				try {
					int i = Integer.parseInt(textField.getText());
					if (0 <= i && i < 256) {
						verified = true;
						rgb[Integer.parseInt(textField.getName())] = i;
						red.repaint();
						green.repaint();
						blue.repaint();
						ColorChooser.setMainColor(rgb);
					}
				} catch (NumberFormatException e) {
					UIManager.getLookAndFeel().provideErrorFeedback(c);
					// Toolkit.getDefaultToolkit().beep();
				}
				return verified;
			}
		}

		public class MyActionListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自動生成されたメソッド・スタブ
			}

		}

	}

	public void showRGB() {
		for (int i = 0; i < rgb.length; i++) {
			U.say("rgb[" + i + "] = " + rgb[i]);
		}
	}

	public class Slider extends JPanel {

		int Width = 256;
		int Height = 20;
		int n;

		float place;

		Color[] c;

		boolean entered = false;

		static final int HORIZONTAL = 0;
		static final int VERTICAL = 1;

		public Slider(int n, Color[] c) {
			this.c = c;
			this.n = n;
			place = 0.0f;
			SliderListener sl = new SliderListener();
			addMouseListener(sl);
			addMouseMotionListener(sl);
			setMaximumSize(new Dimension(Width, Height));
			// setBorder(new BevelBorder(BevelBorder.LOWERED));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D display = (Graphics2D) g.create();

			float[] dist = new float[c.length];
			for (int i = 0; i < dist.length; i++) {
				dist[i] = 1.0f / (dist.length - 1) * i;
			}
			LinearGradientPaint gradient = new LinearGradientPaint(
					new Point2D.Double(50.0d, 50.0d), new Point2D.Double(
							190.0f, 110.0f), dist, c,
					MultipleGradientPaint.CycleMethod.NO_CYCLE);

			display.setPaint(gradient);

			display.fill(new Rectangle2D.Double(0, 0, Width, Height));

			display.setPaint(new Color(255, 255, 255));
			display.drawLine(rgb[n], 0, rgb[n], Height);
			display.dispose();
		}

		public class SliderListener extends MouseAdapter {
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				if (x < 0) {
					rgb[n] = 0;
				} else if (x > 255) {
					rgb[n] = 255;
				} else {
					rgb[n] = x;
				}
				setText();
				repaint();
				ColorChooser.setMainColor(rgb);
			}

			public void mouseDragged(MouseEvent e) {
				int x = e.getX();
				if (x < 0) {
					rgb[n] = 0;
				} else if (x > 255) {
					rgb[n] = 255;
				} else {
					rgb[n] = x;
				}
				setText();
				red.repaint();
				green.repaint();
				blue.repaint();
				repaint();
				ColorChooser.setMainColor(rgb);
			}

		}

	}

}
