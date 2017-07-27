package top.metime.updater.server;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import top.metime.updater.server.event.ClientConnectedEvent;
import top.metime.updater.server.event.ClientDisconnectedEvent;
import top.metime.updater.server.event.StartedEvent;
import top.metime.updater.server.event.StoppedEvent;
import top.metime.updater.server.event.ThrowExceptionEvent;
import top.metime.updater.server.event.TipMessageEvent;
import top.metime.updater.server.net.Service;
import top.metime.updater.server.tools.Config;
import top.metime.updater.server.tools.Rules;
import top.metime.updater.server.view.AdvTray;
import top.metime.updater.server.view.ImageData;
import top.metime.updater.share.description.Rule;


public class Main
{
	private Service service;
	
	private int port;
	private int maxrate;
	private int maxcnt;
	private AdvTray tray;
	
	private ArrayList<Rule> rules = new ArrayList<>();

	public static void main(String[] args) throws IOException, AWTException
	{
		Main m = new Main();
		m.run();
	}
	
	
	private boolean isrun;
	//private int clientcount; 
	private void run() throws IOException, AWTException
	{
		tray = new AdvTray();
		tray.setImage(ImageData.RED);
		
		service = new Service();
		service.init();
		service.start();//启动线程
		loadConfig();
		service.load(new InetSocketAddress(port), maxrate, maxcnt, rules.toArray(new Rule[0]));
		
		MenuItem run = new MenuItem("Run");
		MenuItem stop = new MenuItem("Stop");
		MenuItem reload = new MenuItem("Reload");
		MenuItem quit = new MenuItem("Quit");
		MenuItem test = new MenuItem("test.....");
		
		tray.addMenuItem(run);
		tray.addMenuItem(stop);
		tray.addMenuItem(reload);
		tray.addMenuItem(quit);
		//tray.addMenuItem(test);
		
		test.addActionListener((ActionEvent e) -> 
		{
			if(isrun)
			{
				tray.displayMessage("Debug", "正在运行中！");
			}
			else
			{
				tray.displayMessage("Debug", "未在运行！");
				//service.startService();
			}
			
		});
		
		run.addActionListener((ActionEvent e) -> 
		{
			if(rules.size()==0)
			{
				tray.displayMessage("无效的操作", "配置文件中无可用规则，无法启动！");
			}
			else
			if(isrun)
			{
				tray.displayMessage("无效的操作", "程序正在运行中！");
			}
			else
			{
				tray.displayMessage("信息", "启动通知已发出！");
				service.startService();
			}
			
		});
		stop.addActionListener((ActionEvent e) -> 
		{
			if(isrun)
			{
				tray.displayMessage("信息", "停止通知已发出！");
				service.stopService();
			}
			else
			{
				tray.displayMessage("无效的操作", "程序未启动！");
			}
		});
		reload.addActionListener((ActionEvent e) -> 
		{
			loadConfig();
			if(isrun)
			{
				service.load(new InetSocketAddress(port), maxrate, maxcnt, rules.toArray(new Rule[0]));
				tray.displayMessage("信息", "载入："+this.rules.size()+"条规则\n重启后生效！");
			}
			else
			{
				service.load(new InetSocketAddress(port), maxrate, maxcnt, rules.toArray(new Rule[0]));
				tray.displayMessage("信息", "载入："+this.rules.size()+"条规则！");
			}
		});
		quit.addActionListener((ActionEvent e) -> 
		{
			if(isrun)
			{
				tray.displayMessage("无效的操作", "请先停止程序再退出！");
			}
			else
			{
				tray.hideTray();
				service.exit();
			}
		});
		
		service.addClientConnectedListener((ClientConnectedEvent e) ->
		{
			//tray.setTiptool("当前连接的客户端："+ ++clientcount);
			tray.setTiptool("正在监听的端口："+service.getPort()+"\n当前连接的客户端："+service.getClients().size());
			tray.displayMessage("调试", "IP地址："+e.getAddress().getAddress()+"\n端口："+e.getAddress().getPort()+"\n连接上来了！");
		});
		service.addClientDisconnectedListener((ClientDisconnectedEvent e)->
		{
			tray.setTiptool("正在监听的端口："+service.getPort()+"\n当前连接的客户端："+service.getClients().size());
			tray.displayMessage("调试", "IP地址："+e.getAddress().getAddress()+"\n端口："+e.getAddress().getPort()+"\n断开了连接！");
		});
		service.addStartedListener((StartedEvent e)->
		{
			tray.setImage(ImageData.GREEN);
			isrun=true;
			tray.displayMessage("信息", "现在已始开启监听"+e.port.getPort()+"！");
			tray.setTiptool("当前连接的客户端：0");
		});
		service.addStoppedListener((StoppedEvent e)->
		{
			tray.setImage(ImageData.RED);
			isrun=false;
			tray.displayMessage("重要", "现在已关闭\n原因："+e.getReson()+"！");
			tray.setTiptool("");
		});
		service.addThrowExceptionListener((ThrowExceptionEvent e)->
		{
			tray.displayMessage("异常", e.getException().getMessage()+"\n"+e.getException().toString());
		});
		service.addTipMessageListener((TipMessageEvent e)->
		{
			tray.displayMessage(e.getSummary(), e.getContent());
		});
		
		
		
		
		
		tray.showTray();
	}
	
	public void loadConfig()
	{
		Config cfg = Config.getInstance();
		port = cfg.port;
		maxrate = cfg.maxrate;
		maxcnt = cfg.maxcnt;
		
		Rules rules = Rules.getInstance();
		this.rules.clear();
		this.rules.addAll(rules.rules);
	}
	
	
	
}

