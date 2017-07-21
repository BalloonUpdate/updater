package me.coding.innc.fss;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.jws.soap.SOAPBinding;

import org.json.JSONObject;

import me.coding.innc.fss.server.net.client.ClientStarter;
import me.coding.innc.fss.server.view.AdvTray;
import static me.coding.innc.fss.server.view.ImageData.*;

public class Test {

	public static void main(String[] args) throws InterruptedException, IOException 
	{
		
		ClientStarter a = new ClientStarter(5, null, 200, null);
		
		JSONObject o = new JSONObject();
		o.put("haha", a);
		System.out.println(o);
		System.exit(0);
		AdvTray at = new AdvTray();
		
		at.showTray();
		
		MenuItem mi0 = new MenuItem("load");
		MenuItem mi1 = new MenuItem("put");
		MenuItem mi2 = new MenuItem("save");
		MenuItem mi3 = new MenuItem("exit");
		
		mi0.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				at.displayMessage("nad", "mad");
				at.setImage(READY);
			}
		});
		mi1.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				at.setImage(LISTEING);
				
			}
		});
		mi2.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				at.setImage(READY);
			}
		});
		mi3.addActionListener((ActionEvent e)->
		{
			at.setImage(RED);
			at.displayMessage("重要", "应用程序即将退出!");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
			Runtime.getRuntime().exit(0);
		});
		
		at.addMenuItem(mi0);
		at.addMenuItem(mi1);
		at.addMenuItem(mi2);
		at.addMenuItem(mi3);
		
		at.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				switch(e.getButton())
				{
					case MouseEvent.BUTTON1:
					{
						;
						break;
					}
					case MouseEvent.BUTTON2:
					{
						
						//System.exit(0);
						break;
					}
					case MouseEvent.BUTTON3:
					{
						
						break;
					}
				}
			}
		});
	}

}
