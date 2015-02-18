public class Move
{
	int n_picture;
	GraphDrawing[] picture;

	public Move()
	{
		picture = FullerenePanel.get_all_picture();
		n_picture = picture.length;
	}

	public void shift(int x, int y, double zoom)
	{
		int i;

		for (i = 0; i < n_picture; i++)
		{
			picture[i].embed.offset_x += x;
			picture[i].embed.offset_y += y;
			picture[i].embed.picture_border_level += zoom;
		}
		if (n_picture > 0)
			FUIGUI.fullereneArea.new_picture(picture[0]);

		for (i = 1; i < n_picture; i++)
			FUIGUI.fullereneArea.add_picture(picture[i]);
	}
}
