package top.metime.updater.server.event;

import java.net.InetSocketAddress;
import java.util.EventObject;

public class ClientConnectedEvent extends EventObject 
{

	private static final long serialVersionUID = 1L;
	
	private InetSocketAddress ISA;

	public ClientConnectedEvent(Object source, InetSocketAddress address) 
	{
		super(source);
		ISA = address;
	}
	
	public InetSocketAddress getAddress()
	{
		return ISA;
	}
}
