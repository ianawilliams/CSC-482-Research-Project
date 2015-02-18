/* Created by Wendy Myrvold.
   Last update: Oct. 2005.

   The data structures here give x,y-coordinates for
   the position of each vertex in a rotation system.
   The pictures are assumed to be centered at (0,0)
   but then (originx, originy) is add to put it at
   the center of the screen. Increasing the scale makes
   a bigger picture. The radius is the radius of a vertex.

   The angles on the axes are like this in java:

       270
        |
   180--+-- 0
        |
        90

   Each point is thought of as an angle w.r.t. these axes
   in the range from 0 .. 359 
   and also a level which is 0, 1, 2, ... 
   Level 0 is at the origin.
   The other levels represent equally spaced circles with
   center at the origin. 
  
   The (angle, level) are converted to (x,y) 
   coordinates for the picture.
 */
public class Embedding
{
	public static double scale = 20;
	public static int default_radius = 4;
	public static int originx = 350;
	public static int originy = 300;

	/*
	 * The picture_border_level is the level where the edge of the fullerene panel window will be.
	 */

	AdjList g;
	double[] angle;
	double[] level;
	int[] x;
	int[] y;
	int radius;

	public double picture_border_level;
	public int offset_x, offset_y;

	public Embedding(AdjList h)
	{
		g = h;
		angle = new double[g.n];
		level = new double[g.n];
		x = new int[g.n];
		y = new int[g.n];
	}

	public Embedding(FaceCenter dwd)
	{
		int max_level = 0;
		int i;

		g = dwd.g;
		angle = new double[g.n];
		level = new double[g.n];
		x = new int[g.n];
		y = new int[g.n];
		for (i = 0; i < g.n; i++)
		{
			set_angle_level(i, dwd.angle[i], dwd.level[i]);
			if (dwd.level[i] > max_level)
				max_level = dwd.level[i];
		}
		radius = default_radius;
		picture_border_level = max_level + 2;
		offset_x = 0;
		offset_y = 0;
		scale = 20;
	}

	public Embedding copyof()
	{
		Embedding embed;
		int i;

		embed = new Embedding(g);
		for (i = 0; i < g.n; i++)
		{
			embed.angle[i] = angle[i];
			embed.level[i] = level[i];
			embed.x[i] = x[i];
			embed.y[i] = y[i];
		}
		embed.radius = radius;
		embed.picture_border_level = picture_border_level;
		embed.offset_x = offset_x;
		embed.offset_y = offset_y;
		return (embed);
	}

	public void set_angle(int u, double a)
	{
		angle[u] = a;
		x[u] = findx(u);
		y[u] = findy(u);
	}

	public void set_level(int u, double l)
	{
		level[u] = l;
		x[u] = findx(u);
		y[u] = findy(u);
	}

	public void set_angle_level(int u, double a, double l)
	{
		angle[u] = a;
		level[u] = l;
		x[u] = findx(u);
		y[u] = findy(u);
	}

	//ADDED BY JENNI NOV 2005
	public double get_level(double xcoord, double ycoord)
	{

		double x, y;

		x = xcoord - originx - offset_x;
		y = ycoord - originy - offset_y;
		return (findl(x, y));
	}

	public void set_angle_level_xy(int u, int xcoord, int ycoord)
	{
		x[u] = xcoord - originx - offset_x;
		y[u] = ycoord - originy - offset_y;
		angle[u] = finda(u);
		level[u] = findl(u);
	}

	public void set_xy()
	{
		int u;
		for (u = 0; u < g.n; u++)
		{
			set_xy(u);
		}
	}

	public void set_xy(int u)
	{
		x[u] = findx(u);
		y[u] = findy(u);
	}

	public static void set_scale(double s)
	{
		scale = s;
	}

	public static void set_origin(int newx, int newy)
	{
		originx = newx;
		originy = newy;
	}

	public void set_radius(int r)
	{
		radius = r;
	}

	public int findx(int u)
	{
		int xv;
		double dx;

		double level_scale = scale_it(level[u]);
		dx = level_scale * Math.cos(Math.toRadians(angle[u]));
		dx += originx + offset_x;
		xv = (int) dx;
		return (xv);
	}

	public int findy(int u)
	{
		int yv;
		double dy;

		double level_scale = scale_it(level[u]);
		dy = level_scale * Math.sin(Math.toRadians(angle[u]));
		dy += originy + offset_y;
		yv = (int) dy;
		return (yv);
	}

	//ADDED BY JENNI NOV 2005
	// Modified by Wendy so angle can be found without setting it.
	public double finda(int u)
	{
		return finda(x[u], y[u]);

	}

	public static double finda(double x, double y)
	{
		double a = Math.atan((double) Math.abs(y) / (double) Math.abs(x));
		a = Math.toDegrees(a);
		if (x >= 0 && y >= 0)
			;
		else if (x >= 0)
			a = 360 - a;
		else if (y >= 0)
			a = 180 - a;
		else
			a = 180 + a;
		return a;
	}

	//ADDED BY JENNI NOV 2005
	public double findl(int u)
	{
		return (findl((double) x[u], (double) y[u]));
	}

	public static double findl(double x, double y)
	{
		return unscale_it(Math.sqrt(x * x + y * y));
	}

	//ADDED BY JENNI NOV 2005
	//ADDED BY JENNI NOV 2005
	public static double unscale_it(double level)
	{
		return level / scale;
	}

	public double scale_it(double level)
	{
		double sv = scale * level;
		return (sv);
	}

	/* Make a bigger one that is otherwise the same as the current one. */
	/* Its new adjacency list is h. */

	public Embedding make_bigger(AdjList h)
	{
		Embedding new_embed;
		int i;

		new_embed = new Embedding(h);
		for (i = 0; i < g.n; i++)
		{
			new_embed.angle[i] = angle[i];
			new_embed.level[i] = level[i];
			new_embed.x[i] = x[i];
			new_embed.y[i] = y[i];
		}
		new_embed.radius = radius;
		new_embed.picture_border_level = this.picture_border_level;
		new_embed.offset_x = offset_x;
		new_embed.offset_y = offset_y;
		return (new_embed);
	}

	public void print(String s)
	{
		int i;
		System.out.println(s);
		for (i = 0; i < g.n; i++)
			System.out.println("Vertex " + i + " a " + angle[i] + " l " + level[i] + " x " + x[i] + " y " + y[i]);

	}
}
