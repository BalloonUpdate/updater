package me.coding.innc.fss.server.event;

import java.util.EventListener;

public interface ThrowExceptionListener extends EventListener 
{
	public void onException(ThrowExceptionEvent e);
}
