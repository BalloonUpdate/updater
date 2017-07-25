package top.metime.updater.server.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Config 
{
	private static final String cfgf = "./setting.txt";
	
	public int port = 8888;
	public int maxrate = 500;
	public int maxcnt = 4;
	
	public static Config getInstance()
	{
		return new Config();
	}
	
	private Config()
	{
		try 
		{
			File file = new File(cfgf);
			if(!file.exists())
			{
				file.createNewFile();
				Properties pro = new Properties();
				pro.setProperty("port", "8888");
				pro.setProperty("maxrate_Kb/s", "500");
				pro.setProperty("maxconnect", "4");
				
				FileWriter writer = new FileWriter(file);
				pro.store(writer, "no comments");
				writer.close();
			}
			
			Properties pro = new Properties();
			FileReader reader = new FileReader(file);
			pro.load(reader);
			reader.close();

			port = Integer.parseInt(pro.getProperty("port", "8888"));
			maxrate = Integer.parseInt(pro.getProperty("maxrate_Kb/s", "500"));
			maxcnt = Integer.parseInt(pro.getProperty("maxconnect", "4"));
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	
}
