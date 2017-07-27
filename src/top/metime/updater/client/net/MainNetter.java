package top.metime.updater.client.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.json.JSONObject;

import top.metime.updater.client.description.LocalFileDescription;
import top.metime.updater.client.tools.DeleteFolder;
import top.metime.updater.client.view.Window;
import top.metime.updater.server.excption.ProtocolException;
import top.metime.updater.server.tools.MD5;
import top.metime.updater.share.description.Folder;
import top.metime.updater.share.description.Storage;

public class MainNetter 
{
	private static final byte[] ACK = { 86, 127, 94, 88, 44, 51, 73, 32 };
	private String host;
	private int port;
	private Socket socket;
	private DataInputStream netIn;
	private DataOutputStream netOut;
	private int rulesc;
	private LinkedList<LocalFileDescription> dll = new LinkedList<>();
	private Window w;

	public MainNetter(String host, int port, Window w)
	{
		this.host = host;
		this.port = port;
		this.w = w;
	}

	private void netToFile(File file, String key, long length) throws IOException
	{
		this.w.setPro(0);
		this.netOut.writeUTF(key);
		System.out.print(file.getAbsolutePath() + "被请求下载！ ");
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
	
		byte[] buf = new byte[4096];
		int rcount = (int)(length / buf.length);
		int dyv = (int)(length % buf.length);
		int cp = 0;
		for (int c = 0; c < rcount; c++)
		{
			this.netIn.readFully(buf);
			this.w.setPro(++cp * 100 / rcount);
		
			fos.write(buf, 0, buf.length);
		}
	
		for (int c = 0; c < dyv; c++)
		{
			fos.write(this.netIn.readByte());
		}
		fos.close();
	}

	private void download() throws IOException
	{
		for (LocalFileDescription per : this.dll)
		{
			this.w.adddlist(per.fd.getName() + "     -     " + per.fd.getLength() / 1024L + "Kb     -     MD5:  " + per.fd.getMD5().toUpperCase());
		}

		int a = 0;
		for (LocalFileDescription per : this.dll)
		{
			netOut.writeBoolean(true);
			a++;
			w.setTitle("当前进度 "+(a+"/" + dll.size()));
			netToFile(per.file, per.fd.getMD5(), per.fd.getLength());
			System.out.println("    >>>>>>   当前进度"+(a) + "/" + dll.size() + "|" + per.fd.getName()+"   下载完毕！");
			w.removedlist(per.fd.getName() + "     -     " + per.fd.getLength() / 1024L + "Kb     -     MD5:  " + per.fd.getMD5().toUpperCase());
		}
		this.netOut.writeBoolean(false);
		this.dll.clear();
	}

	public void start() throws UnknownHostException, IOException
	{
		this.w.setbstr("正在连接服务器。。。");
		try
		{
			this.socket = new Socket(this.host, this.port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.w.destory();
			JOptionPane.showMessageDialog(null, "连接失败！", "", 0);
			Runtime.getRuntime().exit(0);
		}
		
		
		
		this.w.setbstr("已建立连接，初始化必要信息。。。");
		this.socket.setSoTimeout(6000);
		this.netIn = new DataInputStream(this.socket.getInputStream());
		this.netOut = new DataOutputStream(this.socket.getOutputStream());
	
		ACK();//验证身份
		try 
		{
			RACK();//验证身份
		} catch (ProtocolException e) 
		{
			e.printStackTrace();
			this.w.destory();
			JOptionPane.showMessageDialog(null, "请检查是端口是否正确！", "协议错误！", 0);
			Runtime.getRuntime().exit(0);
		}
		
		this.rulesc = this.netIn.readInt();//获取总规则数
		System.out.println("握手完毕：规则数：" + this.rulesc);
	
		for (int c = 0; c < this.rulesc; c++)
		{
			this.w.setbstr("正在交换数据，(当前：" + (c + 1) + "/" + this.rulesc + ")");
			String path = new String(read());//读取对应的路径
			File root = new File(path);
			
			String Structure = new String(read());//读取远程客户端用的文件结构摘要
			//System.out.println(Structure);
			Folder droot = new Folder(new JSONObject(Structure));
			
			w.setbstr("正在对比文件，(当前：" + (c + 1) + "/" + this.rulesc + ")");
			wle(droot, root);
			w.setbstr("正在下载文件，(当前：" + (c + 1) + "/" + this.rulesc + ")");
			download();
		}
	
		this.socket.close();
		System.out.print("Socket关闭");
	}

	private void wle(Folder dd, File dir) throws IOException
	{
		if (dir.exists())
		{
			for (File perl : dir.listFiles())
			{
				if (perl.isFile())
				{
					boolean flag = false;
					for (Storage per : dd.getAllList())
					{
						if ((per instanceof top.metime.updater.share.description.File))
						{
							if (per.getName().equals(perl.getName()))
							{
								flag = true;
							}
						}
					}
					if (!flag)
					{
						perl.delete();
					}
				}
				else
				{
					boolean flag = false;
					for (Storage per : dd.getAllList())
					{
						if ((per instanceof Folder))
						{
							if (per.getName().equals(perl.getName()))
							{
								flag = true;
							}
						}
					}
					if (!flag)
					{
						DeleteFolder.delfolder(perl);
					}
				}
			}
		}
		else
		{
			dir.mkdirs();
		}
	
		for (Storage per : dd.getAllList())
		{
			if ((per instanceof Folder))
			{
				Folder d = (Folder)per;
				File ldir = new File(dir, d.getName());
				ldir.mkdirs();
				//System.out.println(ldir.getAbsolutePath() + "++++++");
				wle(d, ldir);
			}
			else if ((per instanceof top.metime.updater.share.description.File))
			{
				top.metime.updater.share.description.File fd = (top.metime.updater.share.description.File)per;
				eqFile(fd, new File(dir, fd.getName()));
			}
		}
	}

	private void eqFile(top.metime.updater.share.description.File fd, File f) throws IOException
	{
		if (f.exists())
		{
			if (f.isFile())
			{
				if (!MD5.getMD5(f).equals(fd.getMD5()))
				{
					DeleteFolder.delfolder(f);
					this.dll.add(new LocalFileDescription(f, fd));
				}
			}
			else
			{
				DeleteFolder.delfolder(f);
				this.dll.add(new LocalFileDescription(f, fd));
			}
		}
		else
		{
			this.dll.add(new LocalFileDescription(f, fd));
		}
	}

	private void ACK() throws IOException
	{
		this.netOut.write(ACK);
	}
	private boolean RACK() throws IOException, ProtocolException//接收一个ACK
	{
		byte[] inres = new byte[ACK.length];
		netIn.read(inres);
		
		if(Arrays.equals(inres, ACK))
		{
			return true;
		}
		else
		{
			throw new ProtocolException();
		}
	}

	private byte[] read() throws IOException
	{
		long length = this.netIn.readLong();
		ByteArrayOutputStream bufed = new ByteArrayOutputStream();
		for (int c = 0; c < length; c++)
		{
			bufed.write(this.netIn.read());
		}
		return bufed.toByteArray();
	}
}
