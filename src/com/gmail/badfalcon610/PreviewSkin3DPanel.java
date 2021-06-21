package com.gmail.badfalcon610;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.FPSAnimator;

public class PreviewSkin3DPanel extends GLJPanel implements ActionListener {

	FPSAnimator animator;
	private PreviewSkin3D preview;
	JPopupMenu popup;

	static JMenuItem showAllMenuItem = new JMenuItem(SkinEditor.getResource("showall"));
	static JCheckBoxMenuItem headHideMenuItem = new JCheckBoxMenuItem(SkinEditor.getResource("hidehead"));
	static JCheckBoxMenuItem bodyHideMenuItem = new JCheckBoxMenuItem(SkinEditor.getResource("hidebody"));
	static JCheckBoxMenuItem armHideMenuItem = new JCheckBoxMenuItem(SkinEditor.getResource("hidearm"));
	static JCheckBoxMenuItem legHideMenuItem = new JCheckBoxMenuItem(SkinEditor.getResource("hideleg"));
	static JCheckBoxMenuItem outerHideMenuItem = new JCheckBoxMenuItem(SkinEditor.getResource("hideouter"));
	static JCheckBoxMenuItem innerHideMenuItem = new JCheckBoxMenuItem(SkinEditor.getResource("hideinner"));

	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.setSize(new Dimension(300, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		PreviewSkin3DPanel panel = new PreviewSkin3DPanel();
		frame.add(panel);
		frame.setVisible(true);

	}

	void start() {
		animator = new FPSAnimator(this, 60);
		animator.start();
	}

	public PreviewSkin3DPanel() {

		preview = new PreviewSkin3D(this);

		start();
		
		popup = new JPopupMenu();

		showAllMenuItem.addActionListener(this);
		headHideMenuItem.addActionListener(this);
		bodyHideMenuItem.addActionListener(this);
		armHideMenuItem.addActionListener(this);
		legHideMenuItem.addActionListener(this);
		outerHideMenuItem.addActionListener(this);
		innerHideMenuItem.addActionListener(this);
		showAllMenuItem.setActionCommand("showAll");
		headHideMenuItem.setActionCommand("hideHead");
		bodyHideMenuItem.setActionCommand("hideBody");
		armHideMenuItem.setActionCommand("hideArm");
		legHideMenuItem.setActionCommand("hideLeg");
		outerHideMenuItem.setActionCommand("hideOuter");
		innerHideMenuItem.setActionCommand("hideInner");
		popup.add(showAllMenuItem);
		popup.addSeparator();
		popup.add(headHideMenuItem);
		popup.add(bodyHideMenuItem);
		popup.add(armHideMenuItem);
		popup.add(legHideMenuItem);
		popup.addSeparator();
		popup.add(outerHideMenuItem);
		popup.add(innerHideMenuItem);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int btn = e.getButton();

				if (btn == MouseEvent.BUTTON1) {
					preview.prevMouseX = e.getX();
					preview.prevMouseY = e.getY();
				} else if (btn == MouseEvent.BUTTON2) {
					preview.zoom = 0.0f;
					preview.angleX = 0.0f;
					preview.angleY = 0.0f;
				} else if (btn == MouseEvent.BUTTON3) {
					showPopup(e);
				}
			}

			public void mouseClicked(MouseEvent e) {
				int btn = e.getButton();

				if (btn == MouseEvent.BUTTON1) {
					if (preview.moveStop) {
						preview.moveStop = false;
					} else {
						preview.moveStop = true;
					}
				} else if (btn == MouseEvent.BUTTON2) {
					preview.zoom = 0.0f;
					preview.angleX = 0.0f;
					preview.angleY = 0.0f;
				} else if (btn == MouseEvent.BUTTON3) {
					showPopup(e);
				}

			}

			public void mouseReleased(MouseEvent e) {
				int btn = e.getButton();

				if (btn == MouseEvent.BUTTON1) {
				} else if (btn == MouseEvent.BUTTON2) {
				} else if (btn == MouseEvent.BUTTON3) {
					showPopup(e);
				}
			}

		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					int x = e.getX();
					int y = e.getY();

					Dimension size = e.getComponent().getSize();
					float thetaY = 360.0f * ((float) (x - preview.prevMouseX) / size.width);
					float thetaX = 360.0f * ((float) (preview.prevMouseY - y) / size.height);

					if (-90 < preview.angleX - thetaX && preview.angleX - thetaX < 90) {
						preview.angleX -= thetaX;
						preview.angleY += thetaY;

					} else if (-90 >= preview.angleX - thetaX) {
						preview.angleX = -90;
					} else if (preview.angleX - thetaX >= 90) {
						preview.angleX = 90;
					}
					preview.prevMouseX = x;
					preview.prevMouseY = y;
				} else if (SwingUtilities.isMiddleMouseButton(e)) {
				} else if (SwingUtilities.isRightMouseButton(e)) {
				}
			}
		});

		addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				preview.zoom += (e.getWheelRotation() * -3);
			}
		});

	}

	public void actionPerformed(ActionEvent e) {
		String act = e.getActionCommand();
		if (act == null) {
			System.out.println("null");
		} else {
			if (act.equals("showAll")) {
				if (headHideMenuItem.isSelected()) {
					headHideMenuItem.doClick();
				}
				if (bodyHideMenuItem.isSelected()) {
					bodyHideMenuItem.doClick();
				}
				if (armHideMenuItem.isSelected()) {
					armHideMenuItem.doClick();
				}
				if (legHideMenuItem.isSelected()) {
					legHideMenuItem.doClick();
				}
				if (outerHideMenuItem.isSelected()) {
					outerHideMenuItem.doClick();
				}
				if (innerHideMenuItem.isSelected()) {
					innerHideMenuItem.doClick();
				}
			}
			else if (act.equals("hideHead")) {
				if (headHideMenuItem.isSelected()) {
					preview.hideHead = true;
					headHideMenuItem.setSelected(true);
				} else {
					preview.hideHead = false;
					headHideMenuItem.setSelected(false);
				}
			}
			else if (act.equals("hideBody")) {
				if (bodyHideMenuItem.isSelected()) {
					preview.hideBody = true;
					bodyHideMenuItem.setSelected(true);
				} else {
					preview.hideBody = false;
					bodyHideMenuItem.setSelected(false);
				}
			}
			else if (act.equals("hideArm")) {
				if (armHideMenuItem.isSelected()) {
					preview.hideArm = true;
					armHideMenuItem.setSelected(true);
				} else {
					preview.hideArm = false;
					armHideMenuItem.setSelected(false);
				}
			}
			else if (act.equals("hideLeg")) {
				if (legHideMenuItem.isSelected()) {
					preview.hideLeg = true;
					legHideMenuItem.setSelected(true);
				} else {
					preview.hideLeg = false;
					legHideMenuItem.setSelected(false);
				}
			}
			else if (act.equals("hideOuter")) {
				if (outerHideMenuItem.isSelected()) {
					preview.hideOuter = true;
					outerHideMenuItem.setSelected(true);
				} else {
					preview.hideOuter = false;
					outerHideMenuItem.setSelected(false);
				}
			}
			else if (act.equals("hideInner")) {
				System.out.println(innerHideMenuItem.isSelected());
				if (innerHideMenuItem.isSelected()) {
					preview.hideInner = true;
					innerHideMenuItem.setSelected(true);
				} else {
					preview.hideInner = false;
					innerHideMenuItem.setSelected(false);
				}
			}
			else {
				System.out.println("missing act");
			}
		}
	}

	private void showPopup(MouseEvent e) {
		popup.show(e.getComponent(), e.getX(), e.getY());
	}

	public void retex() {
		preview.retex();
	}

}
