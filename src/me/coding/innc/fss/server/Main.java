package me.coding.innc.fss.server;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.coding.innc.fss.server.event.StartedEvent;
import me.coding.innc.fss.server.event.StoppedEvent;
import me.coding.innc.fss.server.net.Service;
import me.coding.innc.fss.server.tools.Config;
import me.coding.innc.fss.server.tools.Rules;
import me.coding.innc.fss.server.view.AdvTray;
import me.coding.innc.fss.server.view.Tray;
import me.coding.innc.fss.share.description.Rule;


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
	
	private void run() throws IOException, AWTException
	{
		tray = new AdvTray();
		
		service = new Service(port, maxrate, maxcnt, al.toArray(new Rule[0]));
		service.start();//启动线程
		
		
		MenuItem run = new MenuItem("Run");
		MenuItem stop = new MenuItem("Stop");
		MenuItem reload = new MenuItem("Reload");
		MenuItem quit = new MenuItem("Quit");
		
		run.addActionListener((ActionEvent e) -> 
		{
			if(rules.size()==0)
			{
				tray.displayMessage("严重", "配置文件中无可用规则，无法启动！");
			}
			else
			{
				service.startService();
			}
			
		});
		stop.addActionListener((ActionEvent e) -> 
		{
			
		});
		reload.addActionListener((ActionEvent e) -> 
		{
			
		});
		quit.addActionListener((ActionEvent e) -> 
		{
			
		});
		
		
		tray.setStopListener((ActionEvent e) ->
		{
			service.stopService();
		});
		
		tray.setReloadListener((ActionEvent e) -> 
		{
			init();
			service.reload(new ServiceStarter(port, maxrate, maxcnt, al.toArray(new Rule[0]), al.size()));
		});
		
		tray.setQuitListener((ActionEvent e) -> 
		{
			service.exit();
		});
		
		
		
		service.addStartedListener((StartedEvent e) -> 
		{
			tray.setRunned("正在监听"+e.port, "监听"+e.port+"端口中...");
		});
		
		service.addStoppedListener((StoppedEvent e) -> 
		{
			tray.setStopped("已停止，原因："+e.getReson(), "服务未在运行");
		});
	}
	
	public void init()
	{
		Config cfg = Config.getInstance();
		port = cfg.port;
		maxrate = cfg.maxrate;
		maxcnt = cfg.maxcnt;
		
		Rules rules = Rules.getInstance();
		al.clear();
		al.addAll(rules.rules);
		
		tray.displayMessage("", "载入："+al.size()+"条规则", MessageType.NONE);
	}
	
	
	
}

