package top.metime.updater.server.event;

import java.util.EventListener;

public interface ClientDisconnectedListener extends EventListener
{
	public void onClientDisconnected(ClientDisconnectedEvent e);
}
