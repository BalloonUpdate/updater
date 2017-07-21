package me.coding.innc.fss.server.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import me.coding.innc.fss.share.description.Rule;
import me.coding.innc.fss.share.description.Storage;

public class Rules 
{
	private static final String RULES = "./rules.txt";
	
	public final ArrayList<Rule> rules = new ArrayList<>();
	
	public static Rules getInstance()
	{
		return new Rules();
	}
	
	private Rules()
	{
		try
		{
			File file = new File(RULES);
			if(!file.exists())
			{
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				Properties pro = new Properties();
				pro.put("COMMENT#0", "只要标识符中带有 COMMENT# 程序就不会读取，可作注释，例如 COMMENT#0 asdfgCOMMENT# dgghCOMMENT#0fegdgd 都不会被读取");
				pro.put("COMMENT#1", "规则填写格式为 <服务端本地路径(E.G.: /home/user/dir/)>########<客户端远程路径(E.G.: ./.minecraft/mods/)>");
				pro.put("COMMENT#2", "E.G.: /home/user/dir/)########./.minecraft/mods/");
				pro.store(writer, "");
				writer.close();
			}
			
			FileReader reader = new FileReader(file);
			Properties pro = new Properties();
			pro.load(reader);
			reader.close();

			Enumeration<?> em = pro.propertyNames();
			while(em.hasMoreElements())
			{
				String key = (String)em.nextElement();
				if(key.trim().length()>0 && !key.contains("COMMENT#"))
				{
					String value = pro.getProperty(key);
					File local = new File(value.split("########")[0]);
					String remote = value.split("########")[1];
					Storage.Builder builder = new Storage.Builder(local, remote);
					rules.add(builder.getRule());
				}
			}
		}
		catch(IOException e){e.printStackTrace();}
	}

	
}
