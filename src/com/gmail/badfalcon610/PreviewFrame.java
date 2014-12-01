package com.gmail.badfalcon610;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PreviewFrame extends JFrame implements WindowStateListener {

	Boolean maximized;

	Rectangle bounds;

	public PreviewFrame() {
		bounds = this.getBounds();
		maximized = false;
		setTitle("Preview");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(SkinEditor.iconPass)));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new CloseListener());
		addWindowStateListener(this);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(316, 339));
	}

	public void show(JPanel panel) {
		getContentPane().add(panel);
		pack();
		setVisible(true);
		if (maximized) {
			setExtendedState(MAXIMIZED_BOTH);
		} else {
			setBounds(bounds);
		}
	}

	public void hide(JPanel panel) {
		bounds = this.getBounds();
		if (!maximized) {
			SkinEditor.configuration.setProperty(
					"previewbounds",
					String.valueOf(bounds.x) + "," + String.valueOf(bounds.y)
							+ "," + String.valueOf(bounds.width) + ","
							+ String.valueOf(bounds.height));
		}
		setVisible(false);
		getContentPane().remove(panel);
	}

	public class CloseListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			U.say("aaaa");
			SkinEditor.displayItemList[1].doClick();
		}

		public void windowClosed(WindowEvent e) {
			U.say("bbbb");
		}
	}

	@Override
	public void windowStateChanged(WindowEvent e) {
		U.say(e.getNewState());
		SkinEditor.configuration.setProperty("previewmaximized",
				String.valueOf(e.getNewState() == 6));
		if (e.getNewState() == 6) {
			maximized = true;
		} else if (e.getNewState() == 0) {
			maximized = false;
		}
	}

	public void loadConfig() {
		bounds = this.getBounds();
		String[] prevbound = SkinEditor.configuration.getProperty(
				"previewbounds",
				String.valueOf(bounds.x) + "," + String.valueOf(bounds.y) + ","
						+ String.valueOf(bounds.width) + ","
						+ String.valueOf(bounds.height)).split(",");
		bounds = new Rectangle(Integer.parseInt(prevbound[0]),
				Integer.parseInt(prevbound[1]), Integer.parseInt(prevbound[2]),
				Integer.parseInt(prevbound[3]));
		maximized = Boolean.valueOf(SkinEditor.configuration.getProperty(
				"previewmaximized", String.valueOf(false)));

	}
}
