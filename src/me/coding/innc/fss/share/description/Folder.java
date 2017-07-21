package me.coding.innc.fss.share.description;

import java.util.Iterator;
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
		Iterator<Object> ite = array.iterator();
		while(ite.hasNext())
		{
			sublist.add((Storage)ite.next());
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
		JSONArray child = new JSONArray(getAllList());
		
		obj.put("name", getName());
		obj.put("child", child);
		
		return obj.toString();
		
	}

}
