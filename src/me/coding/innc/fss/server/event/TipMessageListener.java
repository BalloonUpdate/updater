package me.coding.innc.fss.server.event;

import java.util.EventListener;

public interface TipMessageListener extends EventListener 
{
	public void onMessage(TipMessageEvent e);
}
