package me.coding.innc.fss.client.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.json.JSONObject;

import me.coding.innc.fss.client.description.LocalFileDescription;
import me.coding.innc.fss.client.tools.DeleteFolder;
import me.coding.innc.fss.client.view.Window;
import me.coding.innc.fss.server.tools.MD5;
import me.coding.innc.fss.share.description.Folder;
import me.coding.innc.fss.share.description.Storage;

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
		System.out.println(file.getAbsolutePath() + "+++++");
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
			this.w.adddlist(per.fd.getName() + " - " + per.fd.getLength() / 1024L + "Kb - MD5:" + per.fd.getMD5());
		}

		int a = 0;
		for (LocalFileDescription per : this.dll)
		{
			this.netOut.writeBoolean(true);
			System.out.println(++a + "/" + this.dll.size() + "|" + per.fd.getName());
			netToFile(per.file, per.fd.getMD5(), per.fd.getLength());
			this.w.removedlist(per.fd.getName() + " - " + per.fd.getLength() / 1024L + "Kb - MD5:" + per.fd.getMD5());
		}
		this.netOut.writeBoolean(false);
		this.dll.clear();
	}

	public void start() throws UnknownHostException, IOException
	{
		this.w.setbstr("正在连接服务器。。。。");
		try
		{
			this.socket = new Socket(this.host, this.port);
		}
		catch (IOException e)
		{
			this.w.destory();
			JOptionPane.showMessageDialog(null, "连接失败！", "", 0);
			Runtime.getRuntime().exit(0);
		}
		this.w.setbstr("已连接，获取头信息。。。。");
		this.socket.setSoTimeout(6000);
		this.netIn = new DataInputStream(this.socket.getInputStream());
		this.netOut = new DataOutputStream(this.socket.getOutputStream());
	
		ACK();
		this.rulesc = this.netIn.readInt();//获取总规则数
		System.out.println("握手完毕：规则数：" + this.rulesc);
	
		for (int c = 0; c < this.rulesc; c++)
		{
			this.w.setbstr("正在交换数据，(当前：" + (c + 1) + "/" + this.rulesc + ")");
			String path = new String(read());//读取对应的路径
			File root = new File(path);
			
			String Structure = new String(read());//读取远程客户端用的文件结构摘要
			Folder droot = new Folder(new JSONObject(Structure));
			
			this.w.setbstr("正在对比文件，(当前：" + (c + 1) + "/" + this.rulesc + ")");
			wle(droot, root);
			this.w.setbstr("正在下载文件，(当前：" + (c + 1) + "/" + this.rulesc + ")");
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
						if ((per instanceof me.coding.innc.fss.share.description.File))
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
				System.out.println(ldir.getAbsolutePath() + "++++++");
				wle(d, ldir);
			}
			else if ((per instanceof me.coding.innc.fss.share.description.File))
			{
				me.coding.innc.fss.share.description.File fd = (me.coding.innc.fss.share.description.File)per;
				eqFile(fd, new File(dir, fd.getName()));
			}
		}
	}

	private void eqFile(me.coding.innc.fss.share.description.File fd, File f) throws IOException
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
