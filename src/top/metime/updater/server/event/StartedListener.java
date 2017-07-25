package top.metime.updater.server.event;

import java.util.EventListener;

public interface StartedListener extends EventListener 
{
	public void onStarted(StartedEvent e);
}
