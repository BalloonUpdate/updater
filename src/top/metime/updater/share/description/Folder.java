package top.metime.updater.share.description;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Folder extends Storage
{
	private final LinkedList<Storage> sublist = new LinkedList<>();
	
	public Folder(String name)
	{
		this.name = name;
	}
	public Folder(JSONObject ObjString)
	{
		name = ObjString.getString("name");
		JSONArray array = ObjString.getJSONArray("child");
		
		for(int c=0;c<array.length();c++)
		{
			JSONObject obj = array.getJSONObject(c);
			//System.out.println(array.get(c));
			if(obj.has("child"))
			{
				sublist.add(new Folder(obj));
			}
			else
			{
				sublist.add(new File(obj));
			}
			
		}
	}
	
	public void append(Storage d)
	{
		sublist.add(d);
	}
	
	public LinkedList<Storage> getAllList()
	{
		return sublist;
	}
	
	@Override
	public String toString() 
	{
		JSONObject obj = new JSONObject();
		JSONArray child = new JSONArray();
		
		for(Storage per : getAllList())
		{
			child.put(per.toJSONObject());
		}
		
		obj.put("name", getName());
		obj.put("child", child);
		
		return obj.toString();
		
	}
	@Override
	public JSONObject toJSONObject() 
	{
		JSONObject obj = new JSONObject();
		JSONArray child = new JSONArray();
		
		for(Storage per : getAllList())
		{
			child.put(per.toJSONObject());
		}
		
		obj.put("name", getName());
		obj.put("child", child);
		
		return obj;
	}

}
