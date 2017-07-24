package me.coding.innc.fss.server.event;

import java.util.EventListener;

public interface ClientConnectedListener extends EventListener 
{
	public void onClientConnected(ClientConnectedEvent e);
}
