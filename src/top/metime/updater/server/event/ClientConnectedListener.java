package top.metime.updater.server.event;

import java.util.EventListener;

public interface ClientConnectedListener extends EventListener 
{
	public void onClientConnected(ClientConnectedEvent e);
}
