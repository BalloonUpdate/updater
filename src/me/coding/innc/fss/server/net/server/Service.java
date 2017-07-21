package me.coding.innc.fss.server.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.coding.innc.fss.server.event.StartedEvent;
import me.coding.innc.fss.server.event.StartedListener;
import me.coding.innc.fss.server.event.StoppedEvent;
import me.coding.innc.fss.server.event.StoppedListener;
import me.coding.innc.fss.server.net.client.Client;
import me.coding.innc.fss.share.description.Rule;

public class Service extends Thread
{
	private ThreadPoolExecutor tpool;
	private ServerSocket serverSocket;
	
	private long delay = 0;
	private int port = 0;
	private Rule[] rules;
	
	private StartedListener started;
	private StoppedListener stopped;
	
	public Service (int listenPort, int maxRate, int maxConet, Rule[] rules)
	{
		load(listenPort, maxRate, maxConet, rules);
		tpool = new ThreadPoolExecutor(5, 5, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
		this.setName("MainService");
	}
	
	public void load(int listenPort, int maxRate, int maxConet, Rule[] rules)
	{
		port = listenPort;
		delay = maxRate<0?0:1000/(maxRate/4);
		tpool.setCorePoolSize(maxConet);
		tpool.setMaximumPoolSize(maxConet);
		this.rules = rules;
	}
	
	//添加监听器
	public void addStartedListener(StartedListener el)
	{
		started = el;
	}
	
	public void addStoppedListener(StoppedListener el)
	{
		stopped = el;
	}
	
	public void run()
	{
		while(true)
		{
			try 
			{
				if(serverSocket==null || tpool.getCorePoolSize()==tpool.getPoolSize())
				{
					Thread.sleep(1000);
					continue;
				}

				Socket socket = serverSocket.accept();
				Client c = new Client(socket, delay, rules);
				tpool.execute(c);
			}
			catch (IOException e) 
			{
				if(!e.getMessage().contains("Socket closed"))
				{
					e.printStackTrace();
				}
			} 
			catch (InterruptedException e) 
			{
				return;
				//e.printStackTrace();
			}
		}
	}
	
	public void startService() 
	{
		try 
		{
			if(rules.length==0)
			{
				return;
			}
			
			if(serverSocket==null)
			{
				serverSocket = new ServerSocket(port);
				started.onStarted(new StartedEvent(this, port));
			}
			
		} 
		catch (IOException e) 
		{
			stopped.onStopped(new StoppedEvent(this, "IO出现异常："+e.toString()));
		}
		
	}
	
	public void stopService()
	{
		try
		{
			tpool.shutdownNow();//强制关掉
			if(serverSocket!=null)
			{
				serverSocket.close();
				serverSocket = null;
				stopped.onStopped(new StoppedEvent(this, null));
			}
			
		}
		catch(IOException e)
		{
			stopped.onStopped(new StoppedEvent(this, "IO出现异常："+e.toString()));
			e.printStackTrace();
		}
		
	}
	
	public void exit()
	{
		tpool.shutdownNow();
		stopService();
		this.interrupt();
	}
	
	
	
	
	
	
	
	
	
}
