/**
 * This animated algorithm zooms in then zooms out on the current fullerene.
 */
public class Zoom
{
	boolean zoom_in;
	double original_border;
	Move move;

	public Zoom()
	{
		zoom_in = true;
		move = new Move();
		original_border = move.picture[0].embed.picture_border_level;
	}

	public void next_step()
	{
		double s;

		double border = move.picture[0].embed.picture_border_level;

		if (zoom_in)
		{
			s = -0.5;
			if (border + s < 3)
			{
				s = 0.5;
				zoom_in = false;
			}
		}
		else
		{
			s = 0.5;
			if (border + s > original_border + 1)
			{
				s = -0.5;
				zoom_in = true;
			}
		}
		move.shift(0, 0, s);
	}
}
