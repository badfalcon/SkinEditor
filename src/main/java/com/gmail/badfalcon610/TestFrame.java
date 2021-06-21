package com.gmail.badfalcon610;

import java.awt.Component;
import java.awt.Container;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;

public class TestFrame extends JFrame implements ComponentListener{

	Component c;
	
	public TestFrame(Component c) {
		addComponentListener(this);
		this.c = c;
		Container pane = getContentPane();

		MenuBar mb = new MenuBar();
		mb.add(new Menu());
//		setMenuBar(mb);

		pane.add(c);

		pane.setMinimumSize(c.getMinimumSize());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		requestFocus();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		System.out.println("OUT " + getSize().width + " , " + getSize().height);
		System.out.println("IN " + c.getSize().width + " , " + c.getSize().height);
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
