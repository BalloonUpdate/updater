package top.metime.updater.client.description;

import java.io.File;

public class LocalFileDescription 
{
	public File file;
	public top.metime.updater.share.description.File fd;

	public LocalFileDescription(File f, top.metime.updater.share.description.File fd)
	{
		this.fd = fd;
		this.file = f;
	}
}
