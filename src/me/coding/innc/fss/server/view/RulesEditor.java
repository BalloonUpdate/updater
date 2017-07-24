package me.coding.innc.fss.server.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class RulesEditor 
{
	private JFrame jframe;
		private Container window;
			private JScrollPane scroll;
				private JPanel editor;
			private JPanel actionBar;
	
	
	
	public void setTitle(String title)
	{
		jframe.setTitle(title);
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void init()
	{
		jframe = new JFrame();
			window = jframe.getContentPane();
				scroll = new JScrollPane();
					editor = new JPanel();
				actionBar = new JPanel();
				
		
		window.setLayout(new BorderLayout());
		window.add(scroll, BorderLayout.CENTER);
		window.add(actionBar, BorderLayout.SOUTH);
		
		scroll.setViewportView(editor);
		
		editor.setBackground(Color.BLUE);
		actionBar.setBackground(Color.YELLOW);
		
	}
	
}
