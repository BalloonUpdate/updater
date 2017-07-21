package me.coding.innc.fss.server.event;

import java.util.EventObject;

public class StoppedEvent extends EventObject 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String reson;
	public StoppedEvent(Object source, String reson) 
	{
		super(source);
		this.reson = reson;
	}
	public String getReson()
	{
		return reson;
	}
}
