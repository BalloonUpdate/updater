package top.metime.updater.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import top.metime.updater.client.net.MainNetter;
import top.metime.updater.client.view.Window;

public class Main 
{
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		new Main().main();
	}
	
	
	private final String address = "/ads.txt";
			
	public void main()
	{
		try
		{
			Window w = new Window();
			w.init();
			w.viable();
	
			File file = new File("./set.txt");
			InputStream input = getClass().getResourceAsStream(address);
	
			int port = 8888;
			String host = "127.0.0.1";
			String launcher = "./HMCL.exe";
	
			if (file.exists())
			{
				Properties p = new Properties();
				FileInputStream fis = new FileInputStream(file);
				p.load(fis);
		
				port = Integer.parseInt(p.getProperty("port", "8888"));
				host = p.getProperty("host", "127.0.0.1");
				launcher = p.getProperty("launch", "./HMCL.exe");
			}
			else
			if(input!=null)
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(input));
			    String temp = in.readLine();
				if(temp.trim().length()<0)
				{
					host = temp.split(":")[0];
					port = Integer.parseInt(temp.split(":")[1]);
				}
				in.close();
				input.close();
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
