package me.coding.innc.fss.client.description;

import java.io.File;

public class LocalFileDescription 
{
	public File file;
	public me.coding.innc.fss.share.description.File fd;

	public LocalFileDescription(File f, me.coding.innc.fss.share.description.File fd)
	{
		this.fd = fd;
		this.file = f;
	}
}
