package top.metime.updater.server.event;

import java.util.EventObject;

public class ThrowExceptionEvent extends EventObject 
{
	private static final long serialVersionUID = 1L;

	private Exception exception;
	
	public ThrowExceptionEvent(Object source, Exception ex) 
	{
		super(source);
		exception = ex;
	}
	
	public Exception getException()
	{
		return exception;
	}
}
