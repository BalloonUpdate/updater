package me.coding.innc.fss.server.event;

import java.util.EventObject;

public class StartedEvent extends EventObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int port;
	public StartedEvent(Object source, int port)
	{
		super(source);
		this.port = port;
	}
	
}
