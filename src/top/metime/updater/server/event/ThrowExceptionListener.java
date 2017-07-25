package top.metime.updater.server.event;

import java.util.EventListener;

public interface ThrowExceptionListener extends EventListener 
{
	public void onException(ThrowExceptionEvent e);
}
