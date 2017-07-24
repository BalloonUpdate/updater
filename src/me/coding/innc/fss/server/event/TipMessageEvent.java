package me.coding.innc.fss.server.event;

import java.util.EventObject;

public class TipMessageEvent  extends EventObject 
{
	private static final long serialVersionUID = 1L;

	private String summary;
	private String content;
	
	public TipMessageEvent(Object source, String summary, String content) 
	{
		super(source);
		this.summary = summary;
		this.content = content;
	}
	
	public String getSummary() 
	{
		return summary;
	}

	public String getContent() 
	{
		return content;
	}
}
