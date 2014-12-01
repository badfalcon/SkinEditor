package com.gmail.badfalcon610;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class ColorChooser extends JPanel implements ActionListener {
	public static void main(String[] args) {
		JPanel panel = new JPanel();
		ColorChooser t = new ColorChooser();
		panel.add(t);
		new TestFrame(panel);
		t.addToHistory(Color.RED);
	}

	private static Color colormain;
	private Color colorsub;

	static JLayeredPane preview;
	
	private static MainPreview ppanelmain;
	private static MainPreview ppanelsub;
	private HistoryPanel historypanel;

	private JButton exchangebutton;

	private JTabbedPane tabbedPane;

	private static HSBPanel hsb;

	public ColorChooser() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			//
		} catch (InstantiationException e) {
			//
		} catch (IllegalAccessException e) {
			//
		} catch (UnsupportedLookAndFeelException e) {
			//
		}
		SwingUtilities.updateComponentTreeUI(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setFocusable(false);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		add(tabbedPane);

		hsb = new HSBPanel();

		tabbedPane.addTab("HSB", null, hsb, null);

		//		JPanel Sample = new JPanel();
		//		tabbedPane.addTab("Sample", null, Sample, null);

		preview = new JLayeredPane();
		SpringLayout layout = new SpringLayout();
		preview.setLayout(layout);
		//preview.setLayout(new GridLayout(0, 3, 10, 10));

		ppanelmain = new MainPreview(colormain, 60, 60);

		colorsub = Color.WHITE;
		ppanelsub = new MainPreview(colorsub, 60, 60);

		exchangebutton = new JButton();
		exchangebutton.setPreferredSize(new Dimension(30, 30));
		exchangebutton.setBorderPainted(false);
		exchangebutton.addActionListener(this);
		historypanel = new HistoryPanel(5, 2);

		layout.putConstraint(SpringLayout.NORTH, ppanelmain, 0, SpringLayout.NORTH, tabbedPane);
		layout.putConstraint(SpringLayout.WEST, ppanelmain, 0, SpringLayout.WEST, preview);

		layout.putConstraint(SpringLayout.NORTH, ppanelsub, -30, SpringLayout.SOUTH, ppanelmain);
		layout.putConstraint(SpringLayout.WEST, ppanelsub, -30, SpringLayout.EAST, ppanelmain);

		layout.putConstraint(SpringLayout.NORTH, exchangebutton, 0, SpringLayout.SOUTH, ppanelmain);
		layout.putConstraint(SpringLayout.WEST, exchangebutton, 0, SpringLayout.WEST, ppanelmain);

		layout.putConstraint(SpringLayout.NORTH, historypanel, 0, SpringLayout.NORTH, tabbedPane);
		layout.putConstraint(SpringLayout.EAST, historypanel, 0, SpringLayout.EAST, this);

		preview.add(ppanelsub);

		preview.add(ppanelmain);
		
		preview.add(exchangebutton);

		preview.add(historypanel);

//		this.setMinimumSize(new Dimension(300, 403));

		add(preview);

		setMainColor(new Color(Integer.parseInt(SkinEditor.configuration.getProperty("primaryColor",
				String.valueOf(new Color(Color.HSBtoRGB(0.0f, 1.0f, 1.0f)).getRGB())))));
		
		setPreferredSize(new Dimension(310, 372));
		
		preview.moveToFront(ppanelmain);
	}

	public void addTo(Component c) {
		tabbedPane.add("preview", c);
	}

	public static void setMainColor(Color c) {
		Canvas.primaryColor = c;
		colormain = c;
		ppanelmain.setColor(c);
		hsb.setColor(c);
		SkinEditor.configuration.setProperty("primaryColor", String.valueOf(c.getRGB()));
		preview.moveToFront(ppanelmain);
	}

	public static void setMainColor(float[] hsbc) {
		Color c = new Color(Color.HSBtoRGB(hsbc[0], hsbc[1], hsbc[2]));
		Canvas.primaryColor = c;
		colormain = c;
		ppanelmain.setColor(c);
		hsb.setColor(hsbc);
		SkinEditor.configuration.setProperty("primaryColor", String.valueOf(c.getRGB()));
		preview.moveToFront(ppanelmain);
	}

	public static Color getColor() {
		return colormain;
	}

	public void addToHistory(Color c) {
		historypanel.addColor(c);
		historypanel.repaint();
	}

	public class MainPreview extends JPanel {
		private Color color;

		private final int WIDTH;
		private final int HEIGHT;

		public MainPreview(Color c, int w, int h) {
			color = c;
			WIDTH = w;
			HEIGHT = h;
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
			LineBorder lb1 = new LineBorder(Color.LIGHT_GRAY, 5);
			LineBorder lb2 = new LineBorder(Color.GRAY);
			CompoundBorder cb = new CompoundBorder(lb2, lb1);
			
			setBorder(cb);
		}

		public void setColor(Color c) {
			color = c;
			repaint();
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D display = (Graphics2D) g.create();
			display.setPaint(color);
			display.fillRect(0, 0, WIDTH, HEIGHT);
			display.dispose();
		}

	}

	public class HistoryPanel extends JPanel {

		private final int x;
		private final int y;

		private LinkedList<Color> colorhistory;

		private History[][] history;

		public HistoryPanel(int x, int y) {
			this.x = x;
			this.y = y;
			colorhistory = new LinkedList<Color>();
			LineBorder lb = new LineBorder(Color.BLACK);
			TitledBorder tb = new TitledBorder(lb, "History");
			setLayout(new GridLayout(y, x));
			//mainpreview.setAlignmentY(CENTER_ALIGNMENT);
			history = new History[x][y];
			for (int j = 0; j < x; j++) {
				for (int i = 0; i < y; i++) {
					history[j][i] = new History(35, 35);
					add(history[j][i]);
				}
			}
			setBorder(tb);
		}

		public void addColor(Color c) {
			if (colorhistory.contains(c)) {
				colorhistory.remove(c);
			}
			colorhistory.addFirst(c);
			if (colorhistory.size() > x * y) {
				colorhistory.removeLast();
			}
			for (int i = 0; i < colorhistory.size(); i++) {
				history[i / y][i % x].setColor(colorhistory.get(i));

			}
		}

		public class History extends JPanel {
			private Color color;

			private final int WIDTH;
			private final int HEIGHT;

			public History(int w, int h) {
				WIDTH = w;
				HEIGHT = h;
				setPreferredSize(new Dimension(WIDTH, HEIGHT));
				setBorder(new BevelBorder(BevelBorder.LOWERED));
				addMouseListener(new historyMouseListener());
			}

			public void setColor(Color c) {
				color = c;
				repaint();
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D display = (Graphics2D) g.create();
				if (color != null) {
					display.setPaint(color);
				} else {
					display.setPaint(new Color(0, 0, 0, 0));
				}
				display.fillRect(0, 0, WIDTH, HEIGHT);
				display.dispose();
			}

			public class historyMouseListener extends MouseAdapter {
				public void mouseClicked(MouseEvent e) {
					if (color != null) {
						setMainColor(color);
					}

				}
			}

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Color temp = new Color(colormain.getRGB());
		ColorChooser.setMainColor(colorsub);
		colorsub = new Color(temp.getRGB());
		ppanelsub.setColor(colorsub);
	}

}
