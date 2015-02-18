//*************************************************************
// The outer-most container of the FUIGUI GUI.
//*************************************************************

import java.awt.Component;
import java.awt.Frame;

import javax.swing.JApplet;

public class FUIApplet extends JApplet
{
	public static Frame applet_frame;

	public void init()
	{
		// Called when the applet has been loaded
		setSize(900, 650);
		FUIGUI panel = new FUIGUI(getWidth(), getHeight());
		getContentPane().add(panel);
	}

	public void start()
	{
		applet_frame = findParentFrame();
		// Called when the applet should start its execution
	}

	public void stop()
	{
		// Called when the applet should stop its execution
	}

	private Frame findParentFrame()
	{
		Component c = getParent();
		while (true)
		{
			if (c instanceof Frame)
			{
				return ((Frame) c);
			}
			else
			{
				c = c.getParent();
			}
		}
	}
}
