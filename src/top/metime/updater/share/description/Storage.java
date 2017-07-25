package top.metime.updater.share.description;

import java.io.File;
import java.util.HashMap;

import org.json.JSONObject;

import top.metime.updater.server.tools.MD5;

public abstract class Storage
{
	protected String name;
	
	public String getName()
	{
		return name;
	}
	
	public abstract JSONObject toJSONObject();
	
	public static class Builder 
	{
		private HashMap<String, File> dict = new HashMap<String, File>();
		private Rule desc;
		
		public Builder(File serv_local, String client_remote)
		{
			Folder root = new Folder(serv_local.getName());
			wle(serv_local, root);
			desc = new Rule(root, dict, client_remote);
		}
		
		private void wle(File directory, Folder parent)
		{
			for(File per : directory.listFiles())
			{
				if(per.isFile())
				{
					if(per.length()>0)
					{
						String md5 = MD5.getMD5(per);
						parent.append(new top.metime.updater.share.description.File(per.getName(), per.length(), md5));
						dict.put(md5, per);
					}
				}
				else
				{
					Folder sub = new Folder(per.getName());
					parent.append(sub);
					wle(per, sub);
				}
			}
		}
		
		public Rule getRule()
		{
			return desc;
		}
	}
}
