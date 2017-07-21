package me.coding.innc.fss.server.net.server;

import me.coding.innc.fss.share.description.Rule;

public class ServiceStarter 
{
	private int listenPort;
	private int maxRate;
	private int maxConnection;
	private Rule[] rules;
	private int rulesc;
	
	public ServiceStarter(int listenPort, int maxRate, int maxConnection, Rule[] rules, int rulesc)
	{
		this.listenPort = listenPort;
		this.maxRate = maxRate;
		this.maxConnection = maxConnection;
		this.rules = rules;
		this.rulesc = rulesc;
	}
	
	int getListenPort()
	{
		return listenPort;
	}
	
	int getDelay()
	{
		return maxRate<0?0:1000/(maxRate/4);
	}
	
	int getMaxConnection()
	{
		return maxConnection;
	}
	
	Rule[] getcorrespondingRoots()
	{
		return rules;
	}
	
	int getRulesCount()
	{
		return rulesc;
	}
}
