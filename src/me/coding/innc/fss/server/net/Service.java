package me.coding.innc.fss.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.coding.innc.fss.server.event.ClientConnectedEvent;
import me.coding.innc.fss.server.event.ClientConnectedListener;
import me.coding.innc.fss.server.event.ClientDisconnectedEvent;
import me.coding.innc.fss.server.event.ClientDisconnectedListener;
import me.coding.innc.fss.server.event.StartedEvent;
import me.coding.innc.fss.server.event.StartedListener;
import me.coding.innc.fss.server.event.StoppedEvent;
import me.coding.innc.fss.server.event.StoppedListener;
import me.coding.innc.fss.server.event.ThrowExceptionEvent;
import me.coding.innc.fss.server.event.ThrowExceptionListener;
import me.coding.innc.fss.server.event.TipMessageEvent;
import me.coding.innc.fss.server.event.TipMessageListener;
import me.coding.innc.fss.share.description.Rule;

public class Service extends Thread
{
	private ThreadPoolExecutor tpool;//线程池
	private ServerSocket serverSocket;//监听ServerSocket
	
	private long delay = 0;
	private InetSocketAddress port;
	private Rule[] rules;
	
	private ArrayList<StartedListener> StartedListener;
	private ArrayList<StoppedListener> StoppedListener;
	private ArrayList<ClientConnectedListener> ClientConnectedListener;
	private ArrayList<ClientDisconnectedListener> ClientDisconnectedListener;
	private ArrayList<ThrowExceptionListener> ThrowExceptionListener;
	private ArrayList<TipMessageListener> TipMessageListener;
	
	
	public void init()//初始化
	{
		try 
		{
			setName("MainService");//设置线程的名字
			tpool = new ThreadPoolExecutor(5, 5, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>());//初始化线程池
			serverSocket = new ServerSocket();//初始化服务端套接字
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			triggeringThrowExceptionEvent(e);//触发[抛出异常]事件
		}
	}
	
	public void load(InetSocketAddress listenPort, int maxRate, int maxConet, Rule[] rs)//加载配置
	{
		port = listenPort;
		delay = maxRate<0?0:1000/(maxRate/4);
		tpool.setCorePoolSize(maxConet);
		tpool.setMaximumPoolSize(maxConet);
		rules = rs;
	}
	
	
	public void addStartedListener(StartedListener el)//添加StartedListener监听器
	{
		StartedListener.add(el);
	}
	public void addStoppedListener(StoppedListener el)//添加StoppedListener监听器
	{
		StoppedListener.add(el);
	}
	public void addClientConnectedListener(ClientConnectedListener el)//添加ClientConnectedListener监听器
	{
		ClientConnectedListener.add(el);
	}
	public void addClientDisconnectedListener(ClientDisconnectedListener el)//添加ClientDisconnectedListener监听器
	{
		ClientDisconnectedListener.add(el);
	}
	public void addThrowExceptionListener(ThrowExceptionListener el)//添加ThrowExceptionListener监听器
	{
		ThrowExceptionListener.add(el);
	}
	public void addTipMessageListener(TipMessageListener el)//添加TipMessageListener监听器
	{
		TipMessageListener.add(el);
	}
	
	public void removeStartedListener(StartedListener el)//移除StartedListener监听器
	{
		StartedListener.remove(el);
	}
	public void removeStoppedListener(StoppedListener el)//移除StoppedListener监听器
	{
		StoppedListener.remove(el);
	}
	public void removeClientConnectedListener(ClientConnectedListener el)//移除ClientConnectedListener监听器
	{
		ClientConnectedListener.remove(el);
	}
	public void removeClientDisconnectedListener(ClientDisconnectedListener el)//移除ClientDisconnectedListener监听器
	{
		ClientDisconnectedListener.remove(el);
	}
	public void removeThrowExceptionListener(ThrowExceptionListener el)//移除ThrowExceptionListener监听器
	{
		ThrowExceptionListener.remove(el);
	}
	public void removeTipMessageListener(TipMessageListener el)//移除TipMessageListener监听器
	{
		TipMessageListener.remove(el);
	}
	
	private void triggeringStartedEvent(InetSocketAddress port)//触发StartedListener事件
	{
		for(StartedListener per : StartedListener)
		{
			per.onStarted(new StartedEvent(this, port));
		}
	}
	private void triggeringStoppedEvent(String reson)//触发StoppedListener事件
	{
		for(StoppedListener per : StoppedListener)
		{
			per.onStopped(new StoppedEvent(this, reson));
		}
	}
	private void triggeringClientConnectedEvent(InetSocketAddress address)//触发ClientConnectedListener事件
	{
		for(ClientConnectedListener per : ClientConnectedListener)
		{
			per.onClientConnected(new ClientConnectedEvent(this, address));
		}
	}
	private void triggeringClientDisconnectedEvent(InetSocketAddress address)//触发ClientDisconnectedListener事件
	{
		for(ClientDisconnectedListener per : ClientDisconnectedListener)
		{
			per.onClientDisconnected(new ClientDisconnectedEvent(this, address));
		}
	}
	private void triggeringThrowExceptionEvent(Exception ex)//触发ThrowExceptionListener事件
	{
		for(ThrowExceptionListener per : ThrowExceptionListener)
		{
			per.onException(new ThrowExceptionEvent(this, ex));
		}
	}
	private void triggeringTipMessageEvent(String summary, String content)//触发TipMessageListener事件
	{
		for(TipMessageListener per : TipMessageListener)
		{
			per.onMessage(new TipMessageEvent(this, summary, content));
		}
	}
	
	
	public void run()
	{
		while(true)
		{
			try 
			{
				if(tpool.getCorePoolSize()==tpool.getPoolSize())//如果人满了
				{
					Thread.sleep(1000);
				}
				else
				{
					Socket socket = serverSocket.accept();
					tpool.execute(new Client(socket, delay, rules));
				}
			}
			catch (IOException e) 
			{
				if(!e.getMessage().contains("Socket closed"))
				{
					e.printStackTrace();
					triggeringThrowExceptionEvent(e);//触发[抛出异常]事件
				}
			} 
			catch (InterruptedException e) 
			{
				//e.printStackTrace();
				triggeringThrowExceptionEvent(e);//触发[抛出异常]事件
			}
		}
	}
	
	public void startService() 
	{
		try 
		{
			if(rules.length==0)//如果没有规则可以用
			{
				triggeringTipMessageEvent("提示", "没有可用规则，请检查配置文件！");//触发[消息提示]事件
			}
			else
			{
				serverSocket.bind(port);//绑定端口
				triggeringStartedEvent(port);//触发[已启动]事件
			}
			
			
		} 
		catch (IOException e) 
		{
			triggeringThrowExceptionEvent(e);//触发[抛出异常]事件
		}
		
	}
	
	public void stopService()
	{
		try
		{
			tpool.shutdownNow();//强制关掉线程池
			serverSocket.close();
			triggeringStoppedEvent("正常关闭");//触发[已停止]事件
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
