package uk.co.markfrimston.mindMap;

import java.awt.*;

public class MessageDialog extends Dialog
{
	private String message;
	
	public MessageDialog(Dialog owner, String message)
	{
		super(owner);
		init(message);
	}
	
	public MessageDialog(Dialog owner, String message, String title)
	{
		super(owner, title);
		init(message);
	}

	public MessageDialog(Frame owner, String message, String title)
	{
		super(owner,title);
		init(message);
	}
	
	public MessageDialog(Frame owner, String message)
	{
		super(owner);
		init(message);
	}
	
	protected void init(String message)
	{
		this.message = message;
	}
}
