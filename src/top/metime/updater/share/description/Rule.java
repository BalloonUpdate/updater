package top.metime.updater.share.description;

import java.io.File;
import java.util.HashMap;

public class Rule
{
	private HashMap<String, File> dict;
	private final Folder dd;
	private final String remotePath;
	
	public Rule(Folder dd, HashMap<String, File> dict, String remotePath)
	{
		this.dd = dd;
		this.dict = dict;
		this.remotePath = remotePath;
	}
	
	public Folder getRootDir()
	{
		return dd;
	}
	
	public HashMap<String, File> getDictionary()
	{
		return dict;
	}
	
	public String getRemoteClientPath()
	{
		return remotePath;
	}
}
