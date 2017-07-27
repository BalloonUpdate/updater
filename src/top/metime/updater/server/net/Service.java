package top.metime.updater.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import top.metime.updater.server.event.ClientConnectedEvent;
import top.metime.updater.server.event.ClientConnectedListener;
import top.metime.updater.server.event.ClientDisconnectedEvent;
import top.metime.updater.server.event.ClientDisconnectedListener;
import top.metime.updater.server.event.StartedEvent;
import top.metime.updater.server.event.StartedListener;
import top.metime.updater.server.event.StoppedEvent;
import top.metime.updater.server.event.StoppedListener;
import top.metime.updater.server.event.ThrowExceptionEvent;
import top.metime.updater.server.event.ThrowExceptionListener;
import top.metime.updater.server.event.TipMessageEvent;
import top.metime.updater.server.event.TipMessageListener;
import top.metime.updater.share.description.Rule;

public class Service extends Thread
{
	private ThreadPoolExecutor tpool;//线程池
	private ServerSocket serverSocket;//监听ServerSocket
	
	private long delay = 0;
	private InetSocketAddress port;
	private Rule[] rules;
	private int maxConnet;
	
	private ArrayList<StartedListener> startedListener = new ArrayList<>();
	private ArrayList<StoppedListener> stoppedListener = new ArrayList<>();
	private ArrayList<ClientConnectedListener> clientConnectedListener = new ArrayList<>();
	private ArrayList<ClientDisconnectedListener> clientDisconnectedListener = new ArrayList<>();
	private ArrayList<ThrowExceptionListener> throwExceptionListener = new ArrayList<>();
	private ArrayList<TipMessageListener> tipMessageListener = new ArrayList<>();
	
	private LinkedList<Client> runningClient = new LinkedList<>();
	
	public void init()//初始化
	{
		setName("MainService");//设置线程的名字
	}
	
	public void load(InetSocketAddress listenPort, int maxRate, int maxConet, Rule[] rs)//加载配置
	{
		port = listenPort;
		delay = maxRate<0?0:1000/(maxRate/4);
		maxConnet = maxConet;
		rules = rs;
	}
	
	
	public void addStartedListener(StartedListener el)//添加StartedListener监听器
	{
		startedListener.add(el);
	}
	public void addStoppedListener(StoppedListener el)//添加StoppedListener监听器
	{
		stoppedListener.add(el);
	}
	public void addClientConnectedListener(ClientConnectedListener el)//添加ClientConnectedListener监听器
	{
		clientConnectedListener.add(el);
	}
	public void addClientDisconnectedListener(ClientDisconnectedListener el)//添加ClientDisconnectedListener监听器
	{
		clientDisconnectedListener.add(el);
	}
	public void addThrowExceptionListener(ThrowExceptionListener el)//添加ThrowExceptionListener监听器
	{
		throwExceptionListener.add(el);
	}
	public void addTipMessageListener(TipMessageListener el)//添加TipMessageListener监听器
	{
		tipMessageListener.add(el);
	}
	
	public void removeStartedListener(StartedListener el)//移除StartedListener监听器
	{
		startedListener.remove(el);
	}
	public void removeStoppedListener(StoppedListener el)//移除StoppedListener监听器
	{
		stoppedListener.remove(el);
	}
	public void removeClientConnectedListener(ClientConnectedListener el)//移除ClientConnectedListener监听器
	{
		clientConnectedListener.remove(el);
	}
	public void removeClientDisconnectedListener(ClientDisconnectedListener el)//移除ClientDisconnectedListener监听器
	{
		clientDisconnectedListener.remove(el);
	}
	public void removeThrowExceptionListener(ThrowExceptionListener el)//移除ThrowExceptionListener监听器
	{
		throwExceptionListener.remove(el);
	}
	public void removeTipMessageListener(TipMessageListener el)//移除TipMessageListener监听器
	{
		tipMessageListener.remove(el);
	}
	
	private void triggeringStartedEvent(InetSocketAddress port)//触发StartedListener事件
	{
		for(StartedListener per : startedListener)
		{
			per.onStarted(new StartedEvent(this, port));
		}
	}
	private void triggeringStoppedEvent(String reson)//触发StoppedListener事件
	{
		for(StoppedListener per : stoppedListener)
		{
			per.onStopped(new StoppedEvent(this, reson));
		}
	}
	private void triggeringClientConnectedEvent(InetSocketAddress address)//触发ClientConnectedListener事件
	{
		for(ClientConnectedListener per : clientConnectedListener)
		{
			per.onClientConnected(new ClientConnectedEvent(this, address));
		}
	}
	private void triggeringClientDisconnectedEvent(InetSocketAddress address)//触发ClientDisconnectedListener事件
	{
		for(ClientDisconnectedListener per : clientDisconnectedListener)
		{
			per.onClientDisconnected(new ClientDisconnectedEvent(this, address));
		}
	}
	private void triggeringThrowExceptionEvent(Exception ex)//触发ThrowExceptionListener事件
	{
		for(ThrowExceptionListener per : throwExceptionListener)
		{
			per.onException(new ThrowExceptionEvent(this, ex));
		}
	}
	private void triggeringTipMessageEvent(String summary, String content)//触发TipMessageListener事件
	{
		for(TipMessageListener per : tipMessageListener)
		{
			per.onMessage(new TipMessageEvent(this, summary, content));
		}
	}
	
	private boolean close = false;
	public void run()
	{
		while(true)
		{
			try 
			{
				if(close)
				{
					return;
				}
				if(tpool==null||serverSocket==null)//如果人满了
				{
					Thread.sleep(1000);
				}
				else
				{
					Socket socket = serverSocket.accept();
					Client client = new Client(socket, delay, rules, (ClientDisconnectedEvent e)->{triggeringClientDisconnectedEvent(e.getAddress());runningClient.remove(e.getSource());});
					runningClient.add(client);
					tpool.execute(client);
					triggeringClientConnectedEvent(new InetSocketAddress(socket.getInetAddress().getHostAddress(), socket.getPort()));
				}
			}
			catch (IOException e) 
			{
				if(!e.getMessage().equalsIgnoreCase("socket closed"))
				{
					e.printStackTrace();
					triggeringThrowExceptionEvent(e);//触发[抛出异常]事件
				}
			} 
			catch (InterruptedException e) 
			{
				if(!e.getMessage().equals("sleep interrupted"))
				{
					e.printStackTrace();
					triggeringThrowExceptionEvent(e);//触发[抛出异常]事件
				}
			}
		}
		
	}
	
	public void startService() 
	{
		if(rules.length==0)//如果没有规则可以用
		{
			triggeringTipMessageEvent("提示", "没有可用规则，请检查配置文件！");//触发[消息提示]事件
		}
		else
		{
			try 
			{
				tpool = new ThreadPoolExecutor(maxConnet, maxConnet, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>());//初始化线程池
				serverSocket = new ServerSocket(port.getPort());//初始化服务端套接字
				triggeringStartedEvent(port);//触发[已启动]事件
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				triggeringThrowExceptionEvent(e);//触发[抛出异常]事件
				
			}
		}
	}
	
	public void stopService()
	{
		try
		{
			tpool.shutdownNow();//强制关掉线程池
			serverSocket.close();//关掉ServerSocket监听
			tpool=null;
			serverSocket=null;
			triggeringStoppedEvent("正常关闭");//触发[已停止]事件
		}
		catch(IOException e)
		{
			e.printStackTrace();
			triggeringThrowExceptionEvent(e);//触发[抛出异常]事件
		}
	}
	
	public LinkedList<Client> getClients()
	{
		return runningClient;
	}
	
	public int getPort()
	{
		return port.getPort();
	}
	
	public void exit()
	{
		close=true;
	}
}
