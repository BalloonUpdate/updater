package top.metime.updater.server.event;

import java.util.EventListener;

public interface StoppedListener extends EventListener 
{
	public void onStopped(StoppedEvent e);
}
