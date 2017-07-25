package top.metime.updater.server.event;

import java.util.EventListener;

public interface TipMessageListener extends EventListener 
{
	public void onMessage(TipMessageEvent e);
}
