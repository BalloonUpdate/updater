package me.coding.innc.fss.server.event;

import java.util.EventListener;

public interface StoppedListener extends EventListener 
{
	public void onStopped(StoppedEvent e);
}
