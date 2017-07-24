package me.coding.innc.fss.server.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.coding.innc.fss.server.excption.ProtoolException;
import me.coding.innc.fss.share.description.Rule;

public class Client implements Runnable
{
	private final static byte[] ACK = {0x56, 0x7F, 0x5E, 0x58, 0x2C, 0x33, 0x49, 0x20};
	
	private Socket socket;
	private DataInputStream netIn;
	private DataOutputStream netOut;
	private long delay;
	private Rule[] crs;
	
	public Client(Socket socket, long delay, Rule[] crs)
	{
		this.delay = delay;
		this.crs = crs;
		this.socket= socket;
		
		try 
		{
			socket.setSoTimeout(60000);
			netIn = new DataInputStream(socket.getInputStream());
			netOut = new DataOutputStream(socket.getOutputStream());
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	@Override
	public void run()
	{
		try 
		{
			RACK();//验证身份
			netOut.writeInt(crs.length);
			for(Rule per : crs)
			{
				sendRule(per);
			}
		}
		catch (IOException | ProtoolException e){e.printStackTrace();}
		finally
		{
			try 
			{
				socket.close();
			} 
			catch (IOException e1) {e1.printStackTrace();}
		}
	}
	
	private void sendRule(Rule rule) throws IOException, ProtoolException
	{
		HashMap<String, File> dict = rule.getDictionary();
		write(rule.getRemoteClientPath().getBytes());  //发送远程客户端用的路径
		write(rule.getRootDir().toString().getBytes());//发送远程客户端用的文件结构摘要
		
		while(RTOF())
		{
			readFile(dict);
		}
	}
	private void readFile(HashMap<String, File> dict) throws IOException
	{
		String key = new String(netIn.readUTF());//接收客户端发回来的Key
		File file = dict.get(key);//映射里面寻找Key
		
		FileInputStream fileIn = new FileInputStream(file);
		byte[] buffer = new byte[1024*4];
		int len = 0; 
		while((len = fileIn.read(buffer))!=-1)
		{
			try 
			{
				if(delay>0)
				{
					Thread.sleep(delay);//速度限制
				}
			} 
			catch (InterruptedException ex) {Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);}
			netOut.write(buffer, 0, len);
		}
		fileIn.close();
	}
	/*
	private byte[] read() throws IOException
	{
		
		long length = netIn.readLong();
		ByteArrayOutputStream bufed = new ByteArrayOutputStream();
		for(int c = 0;c<length;c++)
		{
			bufed.write(netIn.read());
		}
		return bufed.toByteArray();
	}
	*/
	private void write(byte[] b) throws IOException
	{
		write(new ByteArrayInputStream(b));
	}
	private void write(ByteArrayInputStream b) throws IOException
	{
		netOut.writeLong(b.available());
		byte[] buf = new byte[64];
		int len = 0;
		while((len=b.read(buf))!=-1)
		{
			netOut.write(buf, 0, len);
		}
	}
	
	
	
	private boolean RTOF() throws IOException
	{
		return netIn.readBoolean();
	}
	
	private boolean RACK() throws IOException, ProtoolException
	{
		byte[] inres = new byte[ACK.length];
		netIn.read(inres);
		
		if(Arrays.equals(inres, ACK))
		{
			return true;
		}
		else
		{
			throw new ProtoolException();
		}
	}
	
	
	/*
	private boolean REND() throws IOException, ProtoolException
	{
		byte[] inres = new byte[END.length];
		netIn.read(inres);
		
		if(Arrays.equals(inres, END))
		{
			return true;
		}
		else
		{
			throw new ProtoolException();
		}
	}
	
	private void ACK()
	{
		try 
		{
			netOut.write(ACK);
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	private void END()
	{
		try 
		{
			netOut.write(END);
		} 
		catch (IOException e) {e.printStackTrace();}
	}
*/
}
