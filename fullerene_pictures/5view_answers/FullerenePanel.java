/* Creator: Wendy Myrvold
 * Last modified: Oct. 2005
 * The fullerene panel can either contain a text message
 * (reporting errors) or else it can contain a layered
 * set of pictures of some graphs.
 */

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class FullerenePanel extends JPanel
{
	private static int width = 0, height = 0;
	private static String message;
	public static int n_picture;
	public static GraphDrawing[] picture;
	private static int default_scale = 16;
	public static Graphics page;

	public FullerenePanel()
	{
		setBackground(Color.cyan);
		n_picture = 0;
		message = "No Input File Selected.";
	}

	public static void set_message(String new_message)
	{
		message = new_message;
		n_picture = 0;
	}

	public static void set_scale(int s)
	{
		default_scale = s;
	}

	public static int get_scale(int s)
	{
		return (default_scale);
	}

	public void paintComponent(Graphics pg)
	{
		int i;
		int w, h;
		int min;

		page = pg;
		super.paintComponent(page);

		/* React to changes in size of the panel. */

		w = FUIGUI.fullereneArea.getWidth();
		h = FUIGUI.fullereneArea.getHeight();
		if (h != height || w != width)
		{
			width = w;
			height = h;
			Embedding.set_origin(w / 2, h / 2);
		}

		if (n_picture > 0)
		{
			if (height < width)
				min = height;
			else
				min = width;
			double rad = 1;
			for (i = 0; i < n_picture; i++)
				if (picture[i].embed.picture_border_level > rad)
					rad = picture[i].embed.picture_border_level;
			double s = min / (2.0 * rad);
			Embedding.set_scale(s);
			for (i = 0; i < n_picture; i++)
				picture[i].embed.set_xy();

			/* The background color is taken from the first drawing. */
			picture[0].clear_screen(page, width, height);
			for (i = 0; i < n_picture; i++)
			{
				picture[i].draw_graph(page);
			}
		}
		else
		{
			setBackground(Color.cyan);
			page.setColor(Color.black);
			page.drawString(message, 50, 50);
		}
	}

	public static void new_picture(GraphDrawing gd)
	{
		n_picture = 0;
		picture = new GraphDrawing[1];
		picture[n_picture] = gd;
		n_picture++;

	}

	public static void add_picture(GraphDrawing gd)
	{
		GraphDrawing[] new_picture;
		int i;

		new_picture = new GraphDrawing[n_picture + 1];
		for (i = 0; i < n_picture; i++)
			new_picture[i] = picture[i];
		new_picture[n_picture] = gd;
		picture = new_picture;
		n_picture++;
	}

	public static GraphDrawing[] get_all_picture()
	{
		GraphDrawing[] new_picture;

		int i;

		new_picture = new GraphDrawing[n_picture];

		for (i = 0; i < n_picture; i++)
			new_picture[i] = picture[i].copyof();

		return (new_picture);
	}

	public static GraphDrawing get_picture()
	{
		GraphDrawing gd;

		if (n_picture == 0)
			return (null);
		gd = picture[0].copyof();
		return (gd);
	}
}
