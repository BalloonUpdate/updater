package me.coding.innc.fss.server.event;

import java.util.EventListener;

public interface StartedListener extends EventListener 
{
	public void onStarted(StartedEvent e);
}
