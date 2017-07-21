package me.coding.innc.fss.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import me.coding.innc.fss.client.net.MainNetter;
import me.coding.innc.fss.client.view.Window;

public class Main 
{
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		new Main().main();
	}

	public void main()
	{
		try
		{
			Window w = new Window();
			w.init();
			w.viable();
	
			File file = new File("./set.txt");
	
			int port = 8888;
			String host = "127.0.0.1";
			String launcher = "./HMCL-2.4.1.6.exe";
	
			if (file.exists())
			{
				Properties p = new Properties();
				FileInputStream fis = new FileInputStream(file);
				p.load(fis);
		
				port = Integer.parseInt(p.getProperty("port", "42685"));
				host = p.getProperty("host", "139.196.112.118");
				launcher = p.getProperty("launch", "./HMCL.exe");
			}
	
			MainNetter net = new MainNetter(host, port, w);
			net.start();
			w.setbstr("完成！");
			w.destory();
	
			File lc = new File(launcher);
			if (lc.exists())
			{
				Runtime.getRuntime().exec(lc.getAbsolutePath());
			}
		} 
		catch (IOException e) {e.printStackTrace();}
	}
}
