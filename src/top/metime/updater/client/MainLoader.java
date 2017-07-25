package top.metime.updater.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

public class MainLoader 
{
	URL remote;
	File mcf;
	
	private void main() throws MalformedURLException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		mcf = new File(".minecraft");
		File exf;
		if(!mcf.exists()||mcf.isFile())
		{
			exf = File.createTempFile("exec", "jar");
		}
		else
		{
			exf = new  File(mcf, "fss.jar");
			exf.createNewFile();
		}
		
		remote = new URL("https://innc.coding.me/fssLib/exe.jar");
		
		try
		{
			URLConnection con = remote.openConnection();
			con.setConnectTimeout(3000);
		
			InputStream in = con.getInputStream();
			FileOutputStream fos = new FileOutputStream(exf);
			
			int r;
			byte[] buf = new byte[1024];
			
			while ((r = in.read(buf)) != -1) 
			{
				fos.write(buf, 0, r);
				System.out.print("+");
			}
			System.out.println();
			
			fos.close();
			in.close();
		}
		catch(IOException e){e.printStackTrace();}
			
		
		
		URL url = exf.toURI().toURL();
		URLClassLoader mcl = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
		String main = "me.coding.innc.fss.clientlib.Main";
		@SuppressWarnings("unused")
		Class<?> mc = mcl.loadClass(main);
		mcl.close();
	}
	
	
	
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException 
	{
		MainLoader m = new MainLoader();
		m.main();

	}
	
}
