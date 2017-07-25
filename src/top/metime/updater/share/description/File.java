package top.metime.updater.share.description;

import org.json.JSONObject;

public class File extends Storage
{
	private final long length;
	private final String md5;
	
	public File(String name, long length, String md5)
	{
		this.name = name;
		this.length = length;
		this.md5 = md5;
	}
	public File(JSONObject ObjString)
	{
		name = ObjString.getString("name");
		length = ObjString.getLong("length");
		md5 = ObjString.getString("md5");
	}
	
	public long getLength()
	{
		return length;
	}
	
	public String getMD5()
	{
		return md5;
	}
	
	@Override
	public String toString() 
	{
		JSONObject obj = new JSONObject();
		
		obj.put("name", getName());
		obj.put("length", getLength());
		obj.put("md5", getMD5());
		
		return obj.toString();
	}
	
	@Override
	public JSONObject toJSONObject()
	{
		JSONObject obj = new JSONObject();
		
		obj.put("name", getName());
		obj.put("length", getLength());
		obj.put("md5", getMD5());
		
		return obj;
	}
	
}
