package com.gmail.badfalcon610;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public class MainPanel extends JPanel {
	private MainPanel() {
		super(new BorderLayout());
		add(new JScrollPane(new JTree()));
		setPreferredSize(new Dimension(320, 240));
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		JMenuBar mb = new JMenuBar();
		mb.add(LookAndFeelUtil.createLookAndFeelMenu());

		JFrame frame = new JFrame("@title@");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new MainPanel());
		frame.setJMenuBar(mb);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

final class LookAndFeelUtil {
	private static String lookAndFeel = UIManager.getLookAndFeel().getClass()
			.getName();

	private LookAndFeelUtil() {
	}

	public static JMenu createLookAndFeelMenu() {
		JMenu menu = new JMenu("LookAndFeel");
		ButtonGroup lookAndFeelRadioGroup = new ButtonGroup();
		for (UIManager.LookAndFeelInfo lafInfo : UIManager
				.getInstalledLookAndFeels()) {
			menu.add(createLookAndFeelItem(lafInfo.getName(),
					lafInfo.getClassName(), lookAndFeelRadioGroup));
		}
		return menu;
	}

	private static JRadioButtonMenuItem createLookAndFeelItem(String lafName,
			String lafCalssName, final ButtonGroup lookAndFeelRadioGroup) {
		JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem();
		lafItem.setSelected(lafCalssName.equals(lookAndFeel));
		lafItem.setHideActionText(true);
		lafItem.setAction(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ButtonModel m = lookAndFeelRadioGroup.getSelection();
				try {
					setLookAndFeel(m.getActionCommand());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
					ex.printStackTrace();
				}
			}
		});
		lafItem.setText(lafName);
		lafItem.setActionCommand(lafCalssName);
		lookAndFeelRadioGroup.add(lafItem);
		return lafItem;
	}

	private static void setLookAndFeel(String lookAndFeel)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		String oldLookAndFeel = LookAndFeelUtil.lookAndFeel;
		if (!oldLookAndFeel.equals(lookAndFeel)) {
			UIManager.setLookAndFeel(lookAndFeel);
			LookAndFeelUtil.lookAndFeel = lookAndFeel;
			updateLookAndFeel();
		}
	}

	private static void updateLookAndFeel() {
		for (Window window : Frame.getWindows()) {
			SwingUtilities.updateComponentTreeUI(window);
		}
	}
}
