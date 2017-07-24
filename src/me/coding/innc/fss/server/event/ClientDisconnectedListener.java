package me.coding.innc.fss.server.event;

import java.util.EventListener;

public interface ClientDisconnectedListener extends EventListener
{
	public void onClientDisconnected(ClientDisconnectedEvent e);
}
