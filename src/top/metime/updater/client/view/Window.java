package top.metime.updater.client.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

public class Window 
{
	private JFrame window;
	private Container wp;
	JPanel counting;
	JLabel filesTip;
	JProgressBar filesPro;
	JScrollPane jspane;
	JList<String> list;
	JLabel info;
	int x = 0;
	int y = 0;

	private ArrayList<String> dlist = new ArrayList<>();
	private DefaultListModel<String> dlm = new DefaultListModel<>();

	public static void main(String[] ag) throws IOException
	{
		Window w = new Window();
		w.init();
		w.viable();
	}

	public void setbstr(String str)
	{
		this.info.setText(str);
	}
	
	public void setTitle(String str)
	{
		window.setTitle(str);
	}

	public void adddlist(String str)
	{
		this.dlist.add(str);
		this.dlm.addElement(str);
		this.list.setModel(this.dlm);
	}

	public void removedlist(String str)
	{
		for (int c = 0; c < this.dlist.size(); c++)
		{
			if (((String)this.dlist.get(c)).equals(str))
			{
				this.dlist.remove(c);
				this.dlm.removeElement(str);
				this.list.setModel(this.dlm);
			}
		}
	}

	public void setPro(int hanrate)
	{
		this.filesPro.setValue(hanrate);
	}

	public void init() throws IOException
	{
		this.window = new JFrame();
		this.wp = this.window.getContentPane();
		this.counting = new JPanel();
		this.filesTip = new JLabel("进度");
		this.filesPro = new JProgressBar();
		this.list = new JList<>();
		this.jspane = new JScrollPane(this.list);
	
		this.info = new JLabel("hahaha");
	
		this.window.setTitle("OS: " + System.getProperty("os.name"));
		this.window.setUndecorated(false);
		this.window.setAlwaysOnTop(false);
		this.window.setSize(450, 300);
		this.window.setLocationRelativeTo(null);
		this.window.setDefaultCloseOperation(3);
	
		this.wp.setLayout(new BorderLayout());
		this.wp.add(this.jspane, "Center");
		this.wp.add(this.info, "South");
		this.wp.add(this.counting, "North");
		this.counting.setLayout(new BorderLayout(0, 0));
		this.counting.add(this.filesTip, "West");
		this.counting.add(this.filesPro);
		this.filesPro.setStringPainted(true);
	}

	public void viable()
	{
		this.window.setVisible(true);
	}

	public void destory()
	{
		this.window.dispose();
	}
}
