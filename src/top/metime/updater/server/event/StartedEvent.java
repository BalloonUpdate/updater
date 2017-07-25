package top.metime.updater.server.event;

import java.net.InetSocketAddress;
import java.util.EventObject;

public class StartedEvent extends EventObject
{
	private static final long serialVersionUID = 1L;
	
	public InetSocketAddress port;
	
	public StartedEvent(Object source, InetSocketAddress port2)
	{
		super(source);
		this.port = port2;
	}
	
}
