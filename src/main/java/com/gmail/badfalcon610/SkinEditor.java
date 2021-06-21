package com.gmail.badfalcon610;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEditSupport;

public class SkinEditor extends JFrame {
	public class MyTransferHandler extends TransferHandler {

		@Override
		public boolean canImport(TransferSupport support) {
			if (!support.isDrop()) {
				// ドロップ操作でない場合は受け取らない
				return false;
			}

			if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// ドロップされたのがファイルでない場合は受け取らない
				return false;
			}

			return true;
		}

		/**
		 * ドロップされたファイルを受け取る
		 */
		@Override
		public boolean importData(TransferSupport support) {
			// 受け取っていいものか確認する
			if (!canImport(support)) {
				return false;
			}

			// ドロップ処理
			Transferable t = support.getTransferable();
			try {
				// ファイルを受け取る
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) t
						.getTransferData(DataFlavor.javaFileListFlavor);

				boolean openflag = saveBeforeQuit();
				if (openflag) {
					open(files.get(0));
					repaint();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	static ResourceBundle resources;

	static SkinEditor main;

	public static void main(String args[]) {
		// Locale.setDefault(Locale.ENGLISH);
		Locale locale = Locale.getDefault();
		if (locale.equals(Locale.JAPAN)) {
			resources = ResourceBundle
					.getBundle("com.gmail.badfalcon610.ResourcesJA");
		} else if (locale.equals(Locale.JAPANESE)) {
			resources = ResourceBundle
					.getBundle("com.gmail.badfalcon610.ResourcesJA");
		} else {
			resources = ResourceBundle
					.getBundle("com.gmail.badfalcon610.ResourcesEN");
		}
		main = new SkinEditor();
		try {
			InputStream inputStream = new FileInputStream(new File(
					"SkinEditor.properties"));
			configuration.load(inputStream);
		} catch (FileNotFoundException e) {
			configuration = new Properties();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			loadConfig();
		}
	}

	static String filename;

	static int tool;
	static int previoustool;

	final int MODE_64 = 0;
	final int MODE_32 = 1;

	static int scale = 6;

	final String version = "1.0c";

	static boolean converted;
	static boolean edited;
	static boolean hideTemplate;
	static boolean hidePallet;
	static boolean popPreview;

	static boolean slim;
	static boolean old;

	static int minwidth;
	static int minheight;

	static File currentdirectory = new File(SkinEditor.class
			.getProtectionDomain().getCodeSource().getLocation().getPath());
	static File loadedfile;

	static BufferedImage savedsource;
	static BufferedImage source;
	static Graphics2D display;

	final String[] menuName = { resources.getString("File"),
			resources.getString("Edit"), resources.getString("Settings"),
			resources.getString("Skin"), resources.getString("view"),
			resources.getString("Help") };

	final String[] fileItemName = { resources.getString("New"),
			resources.getString("Open"), resources.getString("Save"),
			resources.getString("Save as") };

	final String[] fileItemIdentifier = { "New", "Open", "Save", "Save as" };

	static JMenuItem[] editItemList;

	final String[] editItemName = { resources.getString("Undo"),
			resources.getString("Redo"), resources.getString("Copy"),
			resources.getString("Cut"), resources.getString("Paste") };

	final String[] editItemIdentifier = { "undo", "redo", "copy", "cut",
			"paste" };

	final String[] settingItemName = {
			resources.getString("change background color"),
			resources.getString("change format color"),
			resources.getString("reset color settings"),
			resources.getString("toggle format model") };
	final String[] settingItemIdentifier = { "change background color",
			"change format color", "reset color settings",
			"toggle format model" };

	final String[] formatName = { "Outer Head", "Outer Body", "Outer Arm(R)",
			"Outer Arm(L)", "Outer Leg(R)", "Outer Leg(L)", "Inner Head",
			"Inner Body", "Inner Arm(R)", "Inner Arm(L)", "Inner Leg(R)",
			"Inner Leg(L)", "Outer", "Inner", resources.getString("All") };
	final String[] formatIdentifier = { "Outer Head", "Outer Body",
			"Outer Arm(R)", "Outer Arm(L)", "Outer Leg(R)", "Outer Leg(L)",
			"Inner Head", "Inner Body", "Inner Arm(R)", "Inner Arm(L)",
			"Inner Leg(R)", "Inner Leg(L)", "Outer", "Inner", "All" };

	static JCheckBoxMenuItem[] skinItemList;

	final String[] toolItemName = { resources.getString("preview skin"),
			resources.getString("upload and use in Minecraft") };

	final String[] toolItemIdentifier = { "preview", "upload" };

	final String[] skinItemName = { resources.getString("older preview"),
			resources.getString("Slim Skin"),
			resources.getString("upload and use in Minecraft") };
	final String[] skinItemIdentifier = { "old", "slim", "upload" };

	JCheckBoxMenuItem[] formatItemList = new JCheckBoxMenuItem[formatName.length];

	static JCheckBoxMenuItem[] displayItemList;

	final String[] displayItemName = { resources.getString("hidepallet"),
			resources.getString("poppreview") };
	final String[] displayItemIdentifier = { "hidepallet", "popprev" };

	static JMenuItem[] fileItemList;

	final String[] commandName = { "brush", "eraser", "dropper", "bucket",
			"line", "square", "fsquare", "ellipse", "fellipse", "feraser",
			"select" };

	static final String[] imgpass = { "/img/brush.png", "/img/eraser.png",
			"/img/color_dropper.png", "/img/paint_bucket.png", "/img/line.png",
			"/img/rectangle.png", "/img/frectangle.png", "/img/ellipse.png",
			"/img/fellipse.png", "/img/feraser.png", "/img/select.png" };

	final static String iconPass = "/img/stevehead.png";

	static JToggleButton[] togglebuttons;
	ButtonGroup buttongroup;

	JButton[] buttonlist = new JButton[imgpass.length];

	static PreviewSkin3DPanel preview;
	static ColorChooser colorchooser;

	Toolkit tk = Toolkit.getDefaultToolkit();

	static Canvas can;
	static ToolBar toolbar;

	static JMenuItem[] settingItemList;

	static JCheckBoxMenuItem oldpreview;

	protected UndoableEditSupport undoSupport;

	static JPanel toolpanel;

	static JPanel prePanel;

	static PreviewFrame prevFrame;

	static Properties configuration;

	ActionListener al;

	void initialize() {
		can = new Canvas();
		configuration = new Properties();
		previoustool = -1;
		can.editNum = 0;
		slim = false;
		loadedfile = null;
		hidePallet = false;
		popPreview = false;
	}

	void newsource() {
		can.initUndo();
		source = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB_PRE);
		can.layer1 = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB_PRE);
		filename = resources.getString("untitled");
		fileItemList[3].setEnabled(true);
		edited = false;
		converted = false;
	}

	void resetcolor() {
		can.backgroundcolor = new Color(128, 128, 128, 255);
		can.foregroundcolor = new Color(255, 255, 255, 70);
		configuration.setProperty("backgroundColor",
				String.valueOf(can.backgroundcolor.getRGB()));
		configuration.setProperty("foregroundColor",
				String.valueOf(can.foregroundcolor.getRGB()));
	}

	SkinEditor() {
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
		initialize();
		addKeyListener(new newKeyListener());
		CloseListener cl = new CloseListener();
		addWindowListener(cl);
		addWindowStateListener(cl);
		// addComponentListener(new SizeCheckListener());
		setTransferHandler(new MyTransferHandler());

		JMenuBar menubar = new JMenuBar();

		JMenu[] menuList = new JMenu[menuName.length];

		for (int i = 0; i < menuName.length; i++) {
			menuList[i] = new JMenu(menuName[i]);
			menubar.add(menuList[i]);
		}

		al = new MenuActionListener();

		fileItemList = new JMenuItem[fileItemName.length];

		for (int i = 0; i < fileItemName.length; i++) {
			fileItemList[i] = new JMenuItem(fileItemName[i]);
			menuList[0].add(fileItemList[i]);
			fileItemList[i].addActionListener(al);
			fileItemList[i].setActionCommand(fileItemIdentifier[i]);
		}
		fileItemList[0].setMnemonic(KeyEvent.VK_N);
		fileItemList[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_DOWN_MASK));
		fileItemList[1].setMnemonic(KeyEvent.VK_O);
		fileItemList[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_DOWN_MASK));
		fileItemList[2].setMnemonic(KeyEvent.VK_S);
		fileItemList[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK));

		editItemList = new JMenuItem[editItemName.length];

		for (int i = 0; i < editItemName.length; i++) {
			editItemList[i] = new JMenuItem(editItemName[i]);
			menuList[1].add(editItemList[i]);
			editItemList[i].addActionListener(al);
			editItemList[i].setActionCommand(editItemIdentifier[i]);
			editItemList[i].setEnabled(false);
		}

		editItemList[0].setMnemonic(KeyEvent.VK_Z);
		editItemList[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_DOWN_MASK));
		editItemList[1].setMnemonic(KeyEvent.VK_Y);
		editItemList[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				InputEvent.CTRL_DOWN_MASK));
		editItemList[2].setMnemonic(KeyEvent.VK_C);
		editItemList[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_DOWN_MASK));
		editItemList[3].setMnemonic(KeyEvent.VK_X);
		editItemList[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_DOWN_MASK));
		editItemList[4].setMnemonic(KeyEvent.VK_V);
		editItemList[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				InputEvent.CTRL_DOWN_MASK));

		settingItemList = new JMenuItem[settingItemName.length];
		for (int i = 0; i < settingItemName.length; i++) {
			if (i == 3) {
				menuList[2].addSeparator();
				settingItemList[i] = new JCheckBoxMenuItem(settingItemName[i]);
			} else {
				settingItemList[i] = new JMenuItem(settingItemName[i]);
			}
			menuList[2].add(settingItemList[i]);
			settingItemList[i].addActionListener(al);
			settingItemList[i].setActionCommand(settingItemIdentifier[i]);
		}

		skinItemList = new JCheckBoxMenuItem[skinItemName.length];
		for (int i = 0; i < skinItemName.length; i++) {
			if (i == 1) {
				menuList[3].addSeparator();
			}
			skinItemList[i] = new JCheckBoxMenuItem(skinItemName[i]);
			menuList[3].add(skinItemList[i]);
			skinItemList[i].addActionListener(al);
			skinItemList[i].setActionCommand(skinItemIdentifier[i]);
		}

		displayItemList = new JCheckBoxMenuItem[displayItemName.length];
		for (int i = 0; i < displayItemName.length; i++) {
			displayItemList[i] = new JCheckBoxMenuItem(displayItemName[i]);
			menuList[4].add(displayItemList[i]);
			displayItemList[i].addActionListener(al);
			displayItemList[i].setActionCommand(displayItemIdentifier[i]);
		}

		JMenuItem helpitem1 = new JMenuItem(resources.getString("About"));
		helpitem1.addActionListener(al);
		helpitem1.setActionCommand("about");
		menuList[5].add(helpitem1);

		setJMenuBar(menubar);

		// Tbar toolbar = new Tbar();
		toolbar = new ToolBar();

		Container pane = getContentPane();

		// pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));

		pane.add(toolbar, BorderLayout.WEST);

		pane.add(can, BorderLayout.CENTER);

		toolpanel = new JPanel();
		toolpanel.setMaximumSize(new Dimension(320, 703));
		toolpanel.setLayout(new BoxLayout(toolpanel, BoxLayout.Y_AXIS));
		// toolpanel.setLayout(new GridLayout(2, 1));

		colorchooser = new ColorChooser();

		toolpanel.add(colorchooser);

		preview = new PreviewSkin3DPanel();
		preview.setPreferredSize(new Dimension(300, 300));
		prePanel = new JPanel();
		prevFrame = new PreviewFrame();
		/*
		 * if (popPreview) { prevFrame.add(preview); prevFrame.pack();
		 * prevFrame.setVisible(true); } else { prePanel.add(preview);
		 * toolpanel.add(prePanel); }
		 */
		// toolpanel.add(preview);
		pane.add(toolpanel, BorderLayout.EAST);

		pane.setMinimumSize(new Dimension(40 + 300 + can.MINIMUM_SCALE * 72,
				can.MINIMUM_SCALE * 72));

		setIconImage(tk.getImage(getClass().getResource(iconPass)));

		newsource();

		// addComponentListener(new SizeCheckListener());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Skin Editor");
		// setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	static void loadConfig() {
		Rectangle bounds = main.getBounds();
		String[] bound = configuration.getProperty(
				"bounds",
				String.valueOf(bounds.x) + "," + String.valueOf(bounds.y) + ","
						+ String.valueOf(bounds.width) + ","
						+ String.valueOf(bounds.height)).split(",");
		if (Boolean.valueOf(configuration.getProperty("maximized",
				String.valueOf(false)))) {
			main.setExtendedState(MAXIMIZED_BOTH);
		} else {
			main.setBounds(Integer.parseInt(bound[0]),
					Integer.parseInt(bound[1]), Integer.parseInt(bound[2]),
					Integer.parseInt(bound[3]));
		}

		tool = Integer.parseInt(configuration.getProperty("selectedTool",
				String.valueOf(0)));
		togglebuttons[tool].doClick();

		ColorChooser.setMainColor(new Color(Integer.parseInt(configuration
				.getProperty("primaryColor", String.valueOf(new Color(Color
						.HSBtoRGB(0.0f, 1.0f, 1.0f)).getRGB())))));

		hideTemplate = Boolean.parseBoolean(configuration.getProperty(
				"showTemplate", String.valueOf(false)));
		settingItemList[3].setSelected(hideTemplate);

		old = Boolean.parseBoolean(configuration.getProperty("oldSkin",
				String.valueOf(false)));
		skinItemList[0].setSelected(old);
		if (old) {
			skinItemList[1].setEnabled(!old);
			can.dotheight = 32;
		} else {
			can.dotheight = 64;
		}

		slim = Boolean.parseBoolean(configuration.getProperty("slim",
				String.valueOf(false)));
		skinItemList[1].setSelected(slim);
		if (slim) {
			skinItemList[0].setEnabled(!slim);
		}
		popPreview = Boolean.parseBoolean(configuration.getProperty("popprev",
				String.valueOf(false)));
		displayItemList[1].setSelected(popPreview);

		prevFrame.loadConfig();
		togglePrev();

		hidePallet = Boolean.parseBoolean(configuration.getProperty(
				"hidepallet", String.valueOf(false)));
		if (hidePallet) {
			main.setMinimumSize(new Dimension(can.MINIMUM_SCALE * 72 + 40 + 16,
					413 + 60));
		} else {
			main.setMinimumSize(new Dimension(can.MINIMUM_SCALE * 72 + 40 + 320
					+ 16, 413 + 60));
		}
		displayItemList[0].setSelected(hidePallet);
		toolpanel.setVisible(!hidePallet);

		can.backgroundcolor = new Color(
				Integer.parseInt(configuration.getProperty("backgroundColor",
						String.valueOf(new Color(128, 128, 128, 255).getRGB()))));
		can.foregroundcolor = new Color(
				Integer.parseInt(configuration.getProperty("foregroundColor",
						String.valueOf(new Color(255, 255, 255, 70).getRGB()))),
				true);
	}

	void bselect(String act) {
		for (JButton bact : buttonlist) {
			if (bact.getActionCommand().equals(act)) {
				bact.setBorderPainted(true);
			} else {
				bact.setBorderPainted(false);
			}
		}
	}

	public int[] mnemonic = { KeyEvent.VK_B, KeyEvent.VK_E, KeyEvent.VK_I,
			KeyEvent.VK_F, KeyEvent.VK_L, KeyEvent.VK_R, KeyEvent.VK_R,
			KeyEvent.VK_C, KeyEvent.VK_C, KeyEvent.VK_E, KeyEvent.VK_S };

	class ToolBar extends JPanel {
		public ToolBar() {
			JToolBar toolbar = new JToolBar(null, JToolBar.VERTICAL);
			buttongroup = new ButtonGroup();
			togglebuttons = new JToggleButton[imgpass.length];
			toolbar.setFloatable(false);
			toolbar.setBorderPainted(false);
			toolbar.setPreferredSize(new Dimension(40, 40 * imgpass.length));
//			setMaximumSize(new Dimension(40, 40 * imgpass.length));
//			setMinimumSize(new Dimension(40, 40 * imgpass.length));
			for (int y = 0; y < imgpass.length; y++) {
				togglebuttons[y] = new JToggleButton(new ImageIcon(
						tk.getImage(getClass().getResource(imgpass[y]))));
				togglebuttons[y].setActionCommand(String.valueOf(y));
				togglebuttons[y].addActionListener(new ToolListener());
				togglebuttons[y].setMnemonic(mnemonic[y]);
				togglebuttons[y].setFocusable(false);
				buttongroup.add(togglebuttons[y]);
				toolbar.add(togglebuttons[y]);
				togglebuttons[y].setToolTipText(resources
						.getString(commandName[y]));
			}
			buttongroup.setSelected(togglebuttons[0].getModel(), true);
			add(toolbar);
		}
	}

	public class newKeyListener implements KeyListener {

		boolean pressing = false;
		boolean longpressed = false;

		@Override
		public void keyTyped(KeyEvent e) {
			int c = e.getKeyCode();
			if (c == KeyEvent.VK_B) {
				togglebuttons[0].doClick();
			} else if (c == KeyEvent.VK_I) {
				togglebuttons[2].doClick();
			} else if (c == KeyEvent.VK_F) {
				togglebuttons[3].doClick();
			} else if (c == KeyEvent.VK_L) {
				togglebuttons[4].doClick();
			} else if (c == KeyEvent.VK_R) {
				if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK) {
					togglebuttons[6].doClick();
				} else {
					togglebuttons[5].doClick();
				}
			} else if (c == KeyEvent.VK_C) {
				if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK) {
					togglebuttons[8].doClick();
				} else {
					togglebuttons[7].doClick();
				}
			} else if (c == KeyEvent.VK_E) {
				if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK) {
					togglebuttons[9].doClick();
				} else {
					togglebuttons[1].doClick();
				}
			}
			can.repaint();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (!pressing) {
				int c = e.getKeyCode();
				if (c == KeyEvent.VK_B) {
					togglebuttons[0].doClick();
				} else if (c == KeyEvent.VK_I) {
					togglebuttons[2].doClick();
				} else if (c == KeyEvent.VK_F) {
					togglebuttons[3].doClick();
				} else if (c == KeyEvent.VK_L) {
					togglebuttons[4].doClick();
				} else if (c == KeyEvent.VK_R) {
					if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK) {
						togglebuttons[6].doClick();
					} else {
						togglebuttons[5].doClick();
					}
				} else if (c == KeyEvent.VK_C) {
					if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK) {
						togglebuttons[8].doClick();
					} else {
						togglebuttons[7].doClick();
					}
				} else if (c == KeyEvent.VK_E) {
					if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK) {
						togglebuttons[9].doClick();
					} else {
						togglebuttons[1].doClick();
					}
				}
				can.repaint();
				pressing = true;
			} else {
				longpressed = true;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (longpressed && previoustool != -1) {
				togglebuttons[previoustool].doClick();
				longpressed = false;
			}
			pressing = false;
		}
	}

	public class ToolListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("2")) {
				previoustool = tool;
			}
			tool = Integer.parseInt(e.getActionCommand());
			configuration.setProperty("selectedTool", String.valueOf(tool));
		}
	}

	public class MenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String act = e.getActionCommand();
			if (act == null) {
				System.out.println("null");
			} else {
				if (act.equals("New")) {
					String[] options = { resources.getString("continue"),
							resources.getString("cancel") };
					int conf = JOptionPane.showOptionDialog(main, resources
							.getString("All unsaved changes will be lost"),
							resources.getString("Create new skin"),
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);
					if (conf == JOptionPane.OK_OPTION) {
						loadedfile = null;
						newsource();
						editItemList[0].setEnabled(can.undoManager.canUndo());
						editItemList[1].setEnabled(can.undoManager.canRedo());
						updateTitle();
					}
				} else if (act.equals("Open")) {

					boolean openflag = saveBeforeQuit();
					if (openflag) {
						JFileChooser rc = new JFileChooser();
						rc.setAccessory(new ImagePreview(rc));

						FileFilter rf = new FileNameExtensionFilter(".png",
								"png");

						rc.setCurrentDirectory(currentdirectory);
						rc.setFileFilter(rf);

						int rs = rc.showOpenDialog(main);

						if (rs == JFileChooser.APPROVE_OPTION) {

							File rfile = rc.getSelectedFile();

							open(rfile);
						}
					}

				} else if (act.equals("Save")) {
					if (loadedfile != null) {
						overwrite(loadedfile);
					} else {
						savenew();
					}
				} else if (act.equals("Save as")) {
					savenew();
				} else if (act.equals("undo")) {
					try {
						can.undoManager.undo();
					} catch (CannotRedoException cre) {
						cre.printStackTrace();
					}
					can.repaint();
					editItemList[0].setEnabled(can.undoManager.canUndo());
					editItemList[1].setEnabled(can.undoManager.canRedo());
				} else if (act.equals("redo")) {
					try {
						can.undoManager.redo();
					} catch (CannotRedoException cre) {
						cre.printStackTrace();
					}
					can.repaint();
					editItemList[0].setEnabled(can.undoManager.canUndo());
					editItemList[1].setEnabled(can.undoManager.canRedo());
				} else if (act.equals("copy")) {
					can.copy(false);
				} else if (act.equals("cut")) {
					can.copy(true);
				} else if (act.equals("paste")) {
					can.paste();
				} else if (act.equals("change background color")) {
					Color newbc = JColorChooser.showDialog(main,
							resources.getString("Background Color"),
							can.backgroundcolor);
					if (newbc != null) {
						configuration.setProperty("backgroundColor",
								String.valueOf(newbc.getRGB()));
						can.backgroundcolor = newbc;
					}
					repaint();
				} else if (act.equals("change format color")) {
					Color newfc = JColorChooser.showDialog(main,
							resources.getString("format color"),
							can.foregroundcolor);
					if (newfc != null) {
						configuration.setProperty("foregroundColor",
								String.valueOf(newfc.getRGB()));
						can.foregroundcolor = new Color(newfc.getRed(),
								newfc.getGreen(), newfc.getBlue(), 70);
					}
					repaint();
				} else if (act.equals("reset color settings")) {
					resetcolor();
					repaint();
				} else if (act.equals("toggle format model")) {
					if (!settingItemList[3].isSelected()) {
						hideTemplate = false;
					} else {
						hideTemplate = true;
					}
					configuration.setProperty("showTemplate",
							String.valueOf(hideTemplate));
					can.repaint();
				} else if (act.equals("preview")) {
				} else if (act.equals("upload")) {
					if (edited) {
						JOptionPane.showMessageDialog(null,
								"You need to save before uploading");
						if (loadedfile != null) {
							overwrite(loadedfile);
						} else {
							savenew();
						}
					}
					URL url = null;
					try {
						url = new URL("https://minecraft.net/profile");
					} catch (MalformedURLException ee) {
						ee.printStackTrace();
					}
					openWebpage(url);
				} else if (act.equals("old")) {
					old = skinItemList[0].isSelected();
					skinItemList[1].setEnabled(!old);
					configuration.setProperty("oldSkin", String.valueOf(old));
					if (old) {
						can.dotheight = 32;
					} else {
						can.dotheight = 64;
					}
					can.repaint();
				} else if (act.equals("slim")) {
					slim = skinItemList[1].isSelected();
					skinItemList[0].setEnabled(!slim);
					configuration.setProperty("slim", String.valueOf(slim));
					can.repaint();
				} else if (act.equals("hidepallet")) {
					if (!displayItemList[0].isSelected()) {
						hidePallet = false;
					} else {
						hidePallet = true;
					}
					configuration.setProperty("hidepallet",
							String.valueOf(hidePallet));
					toolpanel.setVisible(!hidePallet);
					if (hidePallet) {
						main.setMinimumSize(new Dimension(
								can.MINIMUM_SCALE * 72 + 40 + 16, 413 + 60));
					} else {
						main.setMinimumSize(new Dimension(can.MINIMUM_SCALE
								* 72 + 40 + 320 + 16, 413 + 60));
					}
				} else if (act.equals("popprev")) {
					if (!displayItemList[1].isSelected()) {
						popPreview = false;
					} else {
						popPreview = true;
					}
					configuration.setProperty("popprev",
							String.valueOf(popPreview));

					togglePrev();
				} else if (act.equals("about")) {
					JFrame about = new JFrame(resources.getString("about"));

					JLabel aboutText = new JLabel(
							resources.getString("<html>Skin Editor Ver.")
									+ version
									+ resources
											.getString("<br> made by badfalcon<br><br>Great respect to <br>Minecraft Skin Edit made by Patrik Swedman <br>Minecraft made by Mojang<br><br>paint icons from Icons8 (http://icons8.com/)<br>"));
					Toolkit tk = Toolkit.getDefaultToolkit();
					aboutText.setIcon(new ImageIcon(tk.getImage(getClass()
							.getResource(iconPass))));

					about.add(aboutText, BorderLayout.CENTER);

					about.setIconImage(tk.getImage(getClass().getResource(
							iconPass)));
					aboutText.setForeground(UIManager
							.getColor("Label.foreground"));
					about.setSize(370, 150);
					about.setLocationByPlatform(true);
					about.setResizable(false);
					about.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					about.setLocationRelativeTo(null);
					about.setVisible(true);
				} else {
					System.out.println("missing");
					System.out.println(e.getSource());
				}
			}
		}
	}

	public static void togglePrev() {
		preview.animator.stop();

		if (popPreview) {
			prePanel.remove(preview);
			toolpanel.remove(prePanel);
			toolpanel.revalidate();

			preview.setPreferredSize(new Dimension(300, 300));
			preview.animator.start();

			prevFrame.show(preview);
		} else {
			prevFrame.hide(preview);

			preview.setPreferredSize(new Dimension(300, 300));
			preview.animator.start();

			prePanel.add(preview);

			toolpanel.add(prePanel);
			toolpanel.revalidate();

		}
	}

	public void open(File file) {
		BufferedImage temp = source;
		try {
			newsource();
			source = ImageIO.read(file);
		} catch (Exception er) {
			er.printStackTrace();
		}
		if (source.getWidth() == 64) {
			if (source.getHeight() == 32) {
				source = convert(source, true);
				loadedfile = file;
				currentdirectory = new File(loadedfile.getPath());
				filename = loadedfile.getName();
				converted = true;
				can.editNum = 0;
				fileItemList[3].setEnabled(true);
				can.initUndo();
				editItemList[0].setEnabled(can.undoManager.canUndo());
				editItemList[1].setEnabled(can.undoManager.canRedo());
				updateTitle();
				can.changed = true;
			} else if (source.getHeight() == 64) {
				source = convert(source, false);
				loadedfile = file;
				currentdirectory = new File(loadedfile.getPath());
				filename = loadedfile.getName();
				can.editNum = 0;
				fileItemList[3].setEnabled(true);
				can.initUndo();
				editItemList[0].setEnabled(can.undoManager.canUndo());
				editItemList[1].setEnabled(can.undoManager.canRedo());
				updateTitle();
				can.changed = true;
			} else {
				JOptionPane.showMessageDialog(this,
						resources.getString("unsupported size"));
				source = temp;
			}
		} else {
			JOptionPane.showMessageDialog(this,
					resources.getString("unsupported size"));
			source = temp;
		}
	}

	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop()
				: null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage convert(BufferedImage source, boolean to64) {
		BufferedImage bitemp = new BufferedImage(64, 64,
				BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D gtemp = bitemp.createGraphics();
		gtemp.drawImage(source, 0, 0, this);
		int alpha = bitemp.getRGB(0, 0);
		for (int i = 0; i < bitemp.getHeight(); i++) {
			for (int j = 0; j < bitemp.getWidth(); j++) {
				if (bitemp.getRGB(i, j) == alpha) {
//					bitemp.setRGB(i, j, IU.argb(0, 0, 0, 0));
				}
			}
		}
		if (to64) {
			reverse(source, gtemp, 0, 16, 16, 48);
			reverse(source, gtemp, 40, 16, 32, 48);
			JOptionPane.showMessageDialog(this,
					resources.getString("converted successfuly"),
					resources.getString("convert completed"),
					JOptionPane.INFORMATION_MESSAGE);
		}
		gtemp.dispose();
		source = bitemp;
		return source;
	}

	public void reverse(BufferedImage source, Graphics2D gtemp, int x, int y,
			int dx, int dy) {
		gtemp.drawImage(source, dx + 12, dy + 4, dx + 8, dy + 16, x + 0, y + 4,
				x + 4, y + 16, this);
		gtemp.drawImage(source, dx + 8, dy + 0, dx + 4, dy + 16, x + 4, y + 0,
				x + 8, y + 16, this);
		gtemp.drawImage(source, dx + 4, dy + 4, dx + 0, dy + 16, x + 8, y + 4,
				x + 12, y + 16, this);
		gtemp.drawImage(source, dx + 12, dy + 0, dx + 8, dy + 4, x + 8, y + 0,
				x + 12, y + 4, this);
		gtemp.drawImage(source, dx + 16, dy + 4, dx + 12, dy + 16, x + 12,
				y + 4, x + 16, y + 16, this);
	}

	public void overwrite(File file) {
		BufferedImage image = getImage();
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (edited) {
			edited = false;
		}
		can.editNum = 0;
		updateTitle();
	}

	private BufferedImage getImage() {
		BufferedImage image = can.deepCopy(source);
		if (old) {
			image = image.getSubimage(0, 0, 64, can.dotheight);
		}
		return image;
	}

	public void savenew() {
		JFileChooser filechooser = new JFileChooser();

		FileFilter filter = new FileNameExtensionFilter(".png", "png");

		if (loadedfile == null) {
			filechooser.setCurrentDirectory(new File(SkinEditor.class
					.getProtectionDomain().getCodeSource().getLocation()
					.getPath()));
			filechooser.setSelectedFile(new File("skin.png"));
		} else {
			filechooser.setCurrentDirectory(loadedfile);
			filechooser.setSelectedFile(loadedfile);
		}

		filechooser.setFileFilter(filter);
		int selected = filechooser.showSaveDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			if (file.exists()) {
				int select = JOptionPane.showConfirmDialog(this,
						resources.getString("The file exists.Overwrite?"),
						resources.getString("existing file"),
						JOptionPane.OK_CANCEL_OPTION);
				if (select == JOptionPane.OK_OPTION) {
					overwrite(file);
				}
			}
			try {
				BufferedImage image = getImage();
				ImageIO.write(image, "png", file);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			loadedfile = file;
			currentdirectory = new File(loadedfile.getPath());
			filename = loadedfile.getName();
			if (edited) {
				edited = false;
			}
			can.editNum = 0;
			updateTitle();
		} else if (selected == JFileChooser.CANCEL_OPTION) {
		} else if (selected == JFileChooser.ERROR_OPTION) {
		}
	}

	public static void updateTitle() {
		if (can.getChangesNew(source,
				new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB_PRE))
				.size() != 0) {
			fileItemList[3].setEnabled(true);
		} else {
			fileItemList[3].setEnabled(false);
		}
		if (edited) {
			fileItemList[2].setEnabled(true);
		} else {
			fileItemList[2].setEnabled(false);
		}
	}

	public class SizeCheckListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			Component comp = e.getComponent();
			String name = comp.getName();
			int width = comp.getWidth();
			int height = comp.getHeight();

			System.out.println("");
			System.out.println("name:" + name + "  width:" + width
					+ "  height:" + height);
		}
	}

	public class CloseListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			Rectangle bounds = main.getBounds();
			configuration.setProperty(
					"bounds",
					String.valueOf(bounds.x) + "," + String.valueOf(bounds.y)
							+ "," + String.valueOf(bounds.width) + ","
							+ String.valueOf(bounds.height));
			Rectangle prevbounds = prevFrame.getBounds();
			configuration.setProperty(
					"previewbounds",
					String.valueOf(prevbounds.x) + ","
							+ String.valueOf(prevbounds.y) + ","
							+ String.valueOf(prevbounds.width) + ","
							+ String.valueOf(prevbounds.height));
			boolean exitflag = saveBeforeQuit();
			if (exitflag) {
				System.exit(0);
			}
		}

		public void windowStateChanged(WindowEvent e) {
			configuration.setProperty("maximized",
					String.valueOf(e.getNewState() == 6));
		}

	}

	public void saveConfig() {
		try {
			configuration.store(new FileOutputStream(new File(
					"SkinEditor.properties")),
					"Skin Editor Settings #Please do not edit#");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public boolean saveBeforeQuit() {
		if (edited) {
			int save = JOptionPane
					.showConfirmDialog(
							getParent(),
							resources
									.getString("You have unsaved changes\n\nSave before closing?"),
							"unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if (save == JOptionPane.YES_OPTION) {
				if (loadedfile != null) {
					overwrite(loadedfile);
					saveConfig();
					return true;
				} else {
					savenew();
					if (!edited) {
						saveConfig();
						return true;
					}
					return false;
				}
			} else if (save == JOptionPane.NO_OPTION) {
				saveConfig();
				return true;
			} else {
				return false;
			}
		} else {
			saveConfig();
			return true;
		}

	}

	public class undoListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				can.undoManager.undo();
			} catch (CannotRedoException cre) {
				cre.printStackTrace();
			}
			can.repaint();
			editItemList[0].setEnabled(can.undoManager.canUndo());
			editItemList[1].setEnabled(can.undoManager.canRedo());
		}
	}

	public class redoListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				can.undoManager.redo();
			} catch (CannotRedoException cre) {
				cre.printStackTrace();
			}
			can.repaint();
			editItemList[0].setEnabled(can.undoManager.canUndo());
			editItemList[1].setEnabled(can.undoManager.canRedo());
		}
	}

	public int[] getRGBA(Color c) {
		int[] rgba = new int[4];
		rgba[0] = c.getRed();
		rgba[1] = c.getGreen();
		rgba[2] = c.getBlue();
		rgba[3] = c.getAlpha();
		return rgba;
	}

	public static void say(String str) {
		System.out.println(str);
	}

	public static boolean isOld() {
		if (old) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSlim() {
		if (slim) {
			return true;
		} else {
			return false;
		}
	}

	class ImagePreview extends JComponent implements PropertyChangeListener {
		static final int PREVIEW_WIDTH = 90;
		static final int PREVIEW_MARGIN = 5;
		ImageIcon thumbnail = null;
		File file = null;

		public ImagePreview(JFileChooser fc) {
			setPreferredSize(new Dimension(PREVIEW_WIDTH + PREVIEW_MARGIN * 2,
					50));
			fc.addPropertyChangeListener(this);
		}

		public void loadImage() {
			if (file == null) {
				thumbnail = null;
				return;
			}
			ImageIcon tmpIcon = new ImageIcon(file.getPath());
			if (tmpIcon.getIconWidth() > PREVIEW_WIDTH) {
				// Image img = tmpIcon.getImage().getScaledInstance(
				// PREVIEW_WIDTH,-1,Image.SCALE_DEFAULT);
				// The Perils of Image.getScaledInstance() | Java.net
				// http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
				float scale = PREVIEW_WIDTH / (float) tmpIcon.getIconWidth();
				int newW = (int) (tmpIcon.getIconWidth() * scale);
				int newH = (int) (tmpIcon.getIconHeight() * scale);
				BufferedImage img = new BufferedImage(newW, newH,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = (Graphics2D) img.getGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.drawImage(tmpIcon.getImage(), 0, 0, newW, newH, null);
				g2.dispose();
				thumbnail = new ImageIcon(img);
			} else {
				thumbnail = tmpIcon;
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent e) {
			boolean update = false;
			String prop = e.getPropertyName();
			if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
				file = null;
				update = true;
			} else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
				file = (File) e.getNewValue();
				update = true;
			}
			if (update) {
				thumbnail = null;
				if (isShowing()) {
					loadImage();
					repaint();
				}
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (thumbnail == null) {
				loadImage();
			}
			if (thumbnail != null) {
				int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
				int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;
				if (y < 0)
					y = 0;
				if (x < PREVIEW_MARGIN)
					x = PREVIEW_MARGIN;
				thumbnail.paintIcon(this, g, x, y);
			}
		}
	}

	public static String getResource(String key) {
		return resources.getString(key);
	}

}
