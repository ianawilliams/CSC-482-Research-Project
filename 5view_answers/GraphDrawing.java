/*     
       Creator: Wendy Myrvold
       Last update: Oct. 2005

       This class takes a
       page- to place the picture on
       g- rotation system of a graph
       embed- (x,y)-coordinates of the vertices of the graph
       gcolor- colours for each vertex and edge
       and draws the picture of the embedded graph on the page.

       It is assumed that all edges are represented by straight lines
       between their vertices. If a vertex has a neighbour "NO_NEIGHBOUR"
       in the rotation system, no corresponding edge is drawn.
 */

import java.awt.Color;
import java.awt.Graphics;

public class GraphDrawing
{
	AdjList g;
	Embedding embed;
	Coloring gcolor;
	Graphics page;
	final public static double EPSILON = 0.005;

	public GraphDrawing()
	{
	}

	public GraphDrawing(FaceCenter dwd, Coloring c)
	{
		g = dwd.g;
		gcolor = c;
		embed = new Embedding(dwd);
	}

	public GraphDrawing dual_dots(Fullerene f, int option)
	{
		double level;
		XY mid;
		XY dot;
		int i, j, d;
		int u, v;

		GraphDrawing gd;
		gd = new GraphDrawing();
		gd.g = new AdjList(f.d.n);
		for (i = 0; i < f.d.n; i++)
		{
			gd.g.degree[i] = 0;
			gd.g.label[i] = f.d.label[i];
		}
		gd.gcolor = new Coloring(gd.g, option);
		gd.embed = new Embedding(gd.g);
		gd.embed.picture_border_level = embed.picture_border_level;

		System.out.println("originx " + Embedding.originx);
		System.out.println("originy " + Embedding.originy);
		System.out.println("primal offset_x " + embed.offset_x);
		System.out.println("primal offset_y " + embed.offset_y);
		System.out.println("dual offset_x " + gd.embed.offset_x);
		System.out.println("dual offset_y " + gd.embed.offset_y);
		System.out.println("scale " + gd.embed.scale);

		for (i = 0; i < f.d.n; i++)
		{
			centroid(i, f, gd.embed);
		}
		embed.set_xy();
		gd.embed.set_xy();
		embed.print("The primal embedding:");
		gd.embed.print("The initial dual embedding:");

		/* Make sure dual dots are inside their faces. */

		for (i = 0; i < f.d.n; i++)
		{
			System.out.println("Face " + i + "--------------------");
			int closest = -1;
			double closest_distance = 9999999;

			dot = new XY(gd.embed.x[i], gd.embed.y[i]);
			d = f.faces.degree[i];
			for (j = 0; j < d; j++)
			{
				u = f.faces.Adj[i][j];
				v = f.faces.Adj[i][(j + 1) % d];
				System.out.println("(" + u + ", " + v + "):");
				mid = closest_point(u, v, dot);
				if (closest < 0 || closest_distance > distance(dot, mid))
				{
					if (!is_floater_edge(u, v))
					{
						closest_distance = distance(dot, mid);
						closest = j;
					}
				}
			}
			u = f.faces.Adj[i][closest];
			v = f.faces.Adj[i][(closest + 1) % d];
			System.out.println("Face " + i + " is (" + u + ", " + v + ")");
			/*
			 * Now make sure we are on the rhs of the line segment.
			 */
			if (on_RHS(u, v, dot))
			{
				mid = closest_point(u, v, dot);
				dot.print("The dual point: ");
				mid.print("The closest point: ");
				level = embed.get_level(mid.x, mid.y);
				System.out.println("Dual level " + gd.embed.level[i] + " Closest point level " + level);
				if (Math.abs(level - gd.embed.level[i]) < 0.75)
				{
					gd.embed.set_level(i, level + 0.75);
					dot = new XY(gd.embed.x[i], gd.embed.y[i]);
					if (!on_RHS(u, v, dot))
						gd.embed.set_level(i, level - 0.75);
				}
			}
			/* Check if too close to the edge. */
			else
			{
				/* Put it beside the midpoint of the bad edge. */
				System.out.println("BAD");
				mid = midpoint(u, v);
				gd.embed.set_angle_level_xy(i, (int) mid.x, (int) mid.y);
				gd.embed.set_level(i, gd.embed.level[i] + 0.75);
				System.out.println("RESET Dual level " + gd.embed.level[i]);
			}
		}
		gd.embed.print("The final dual embedding:");
		return (gd);
	}

	/*
	 * We are only interested in relative distance so there is no need to take a square root here.
	 */
	public XY closest_point(int u, int v, XY dot)
	{
		XY uxy, vxy;
		XY close;
		double m, b, c, ix, iy;

		uxy = new XY(embed.findx(u), embed.findy(u));
		vxy = new XY(embed.findx(v), embed.findy(v));
		if (Math.abs(uxy.x - vxy.x) < EPSILON)
		{
			close = new XY(uxy.x, dot.y);
		}
		else if (Math.abs(uxy.y - vxy.y) < EPSILON)
		{
			close = new XY(dot.x, uxy.y);
		}
		else
		{
			/* The line segment (u, v) is y= m x + b. */

//			System.out.println("Find closest point:");
//			vxy.print("vxy ");
//			uxy.print("uxy ");

			m = (vxy.y - uxy.y) / (vxy.x - uxy.x);
			b = vxy.y - m * vxy.x;

			/* The perpendicular line is y= - * 1/m x + c */

			c = dot.y + (1 / m) * dot.x;

//			System.out.println("1. y=  " + m + " x + " + b); 
//			System.out.println("2. y=  " + (-1/m) + " x + " + c); 

			/* Compute the intersection. */

			ix = (c - b) / (m + (1 / m));
			iy = m * ix + b;
			close = new XY(ix, iy);
		}

		/*
		 * The closest point must lie on the line segment- otherwise select the closest endpoint of
		 * the segment.
		 */
		close.between_fix(uxy, vxy);

		close.print("The closest point: ");
		return (close);
	}

	public boolean on_RHS(int u, int v, XY dot)
	{
		XY uxy, vxy;
		XY nvxy, ndot; /* Normalize so u is at origin. */
		double a1, a2;
		double diff;

		uxy = new XY(embed.findx(u), embed.findy(u));
		vxy = new XY(embed.findx(v), embed.findy(v));
		nvxy = new XY(vxy.x - uxy.x, vxy.y - uxy.y);
		ndot = new XY(dot.x - uxy.x, dot.y - uxy.y);
		a1 = Embedding.finda((double) nvxy.x, (double) nvxy.y);
		a2 = Embedding.finda((double) ndot.x, (double) ndot.y);
		uxy.print("Vertex u ");
		vxy.print("Vertex v ");
		dot.print("Dot      ");
//		nvxy.print("Norm   v ");
//		ndot.print("Norm Dot");
//		System.out.println("a1 " + a1 + " a2 " + a2);
		diff = a2 - a1;
		if (diff < 0)
			diff += 360;
		if (diff < 180)
			return (true);
		else
			return (false);
	}

	public double distance(XY dot, XY mid)
	{
		double dx, dy, d;

		dx = dot.x - mid.x;
		dx *= dx;
		dy = dot.y - mid.y;
		dy *= dy;
		d = dx + dy;
		return (d);
	}

	public XY midpoint(int u, int v)
	{
		XY mid = new XY();

		mid.x += embed.findx(u);
		mid.x += embed.findx(v);
		mid.y += embed.findy(u);
		mid.y += embed.findy(v);
		mid.x /= 2;
		mid.y /= 2;
		return (mid);
	}

	public void centroid(int face_num, Fullerene f, Embedding dual_embed)
	{
		int x, y;
		int j, u;
		int d;
		int count;

		count = 0;
		d = f.faces.degree[face_num];
		x = 0;
		y = 0;
		for (j = 0; j < d; j++)
		{
			u = f.faces.Adj[face_num][j];
			if (is_border(j, d, f, face_num))
			{
				x += embed.findx(u);
				y += embed.findy(u);
				count++;
			}
			else
				System.out.println("Face" + face_num + ": Ignore " + u);
		}
		x /= count;
		y /= count;
		dual_embed.set_angle_level_xy(face_num, x, y);
	}

	public boolean is_border(int j, int d, Fullerene f, int face_num)
	{
		int u, v, w;
		int i;
		boolean diff_levels;
		double min_level;

		min_level = 999999999;
		diff_levels = false;
		for (i = 0; i < d && !diff_levels; i++)
		{
			v = f.faces.Adj[face_num][i];
			w = f.faces.Adj[face_num][(i + 1) % d];
			if (Math.abs(embed.level[v] - embed.level[w]) > EPSILON)
				diff_levels = true;
			if (embed.level[v] < min_level)
				min_level = embed.level[v];
		}
		if (!diff_levels)
			return (true);

		u = f.faces.Adj[face_num][(j - 1 + d) % d];
		v = f.faces.Adj[face_num][j];
		w = f.faces.Adj[face_num][(j + 1) % d];

		if (Math.abs(embed.level[v] - min_level) > EPSILON)
			return (false);
		if (Math.abs(embed.level[u] - embed.level[v]) > EPSILON)
			return (true);
		if (Math.abs(embed.level[w] - embed.level[v]) > EPSILON)
			return (true);
		return (false);
	}

	public GraphDrawing old_dual_dots(Fullerene f, int option)
	{
		GraphDrawing gd;

		gd = new GraphDrawing();

		double new_angle, new_level;
		double min_angle, min_level;
		double max_angle, max_level;
		double biggest_level = 0;
		double compare_level = 0;
		double diff1;
		double diff2;
		int i, j, u;

		gd.g = new AdjList(f.d.n);
		for (i = 0; i < f.d.n; i++)
		{
			gd.g.degree[i] = 0;
			gd.g.label[i] = f.d.label[i];
		}
		gd.gcolor = new Coloring(gd.g, option);
		gd.embed = new Embedding(gd.g);
		gd.embed.picture_border_level = embed.picture_border_level;
		for (i = 0; i < f.d.n; i++)
		{
			max_level = -1;
			min_level = -1;
			for (j = 0; j < f.faces.degree[i]; j++)
			{
				u = f.faces.Adj[i][j];
				if (min_level < 0 || embed.level[u] < min_level)
					min_level = embed.level[u];
				if (embed.level[u] > max_level)
					max_level = embed.level[u];
			}

			max_angle = -1;
			min_angle = 361;

			if (max_level < 2.5)
				compare_level = max_level;
			else
				compare_level = min_level;

			for (j = 0; j < f.faces.degree[i]; j++)
			{
				u = f.faces.Adj[i][j];

				/* Ignore angle of level 0 vertices. */
				if (embed.level[u] > 0.5)
				{
					if (Draw.compare(compare_level, embed.level[u]) == 0)
					{
						if (embed.angle[u] < min_angle)
							min_angle = embed.angle[u];
						if (embed.angle[u] > max_angle)
							max_angle = embed.angle[u];
					}
				}
			}
			if (max_level > biggest_level)
				biggest_level = max_level;

			/*
			 * If the face straddles the zero axis then these calculations may not have identified
			 * yet the bounding angles for the face.
			 */

			if (max_angle - min_angle > 200)
			{
				min_angle = 999999;
				max_angle = -1;
				for (j = 0; j < f.faces.degree[i]; j++)
				{
					u = f.faces.Adj[i][j];
					if (embed.angle[u] < min_angle && embed.angle[u] > 180)
						min_angle = embed.angle[u];
					if (embed.angle[u] > max_angle && embed.angle[u] < 180)
						max_angle = embed.angle[u];
				}
				max_angle = max_angle + 360;
			}

			new_level = min_level + 1.0;
			if (Draw.compare(max_level, 2.0) == 0)
			{
				if (Draw.compare(min_level, 2.0) == 0)
					new_level = 0;
				else
					new_level = max_level - 1;
			}
//			if (i == f.d.n - 1)
//			{
//				new_level = min_level + 1.5;
//			}

			diff1 = max_angle - min_angle;
			diff2 = 360 - diff1;
			if (diff1 < diff2)
			{
				new_angle = min_angle + diff1 / 2;
			}
			else
			{
				new_angle = max_angle + diff2 / 2;
			}
			new_angle = new_angle % 360;

//			if (i == f.d.n - 1 && new_level < 2)
//			{
//				new_angle = 315;
//				new_level = biggest_level + 4;
//			}
//
//			System.out.println("Face " + i + " level " + min_level + " " + max_level + " " + new_level);
//			System.out.println("Face " + i + " angle " + min_angle + " " + max_angle + " " + new_angle);

			gd.embed.set_angle_level(i, new_angle, new_level);
		}
		return (gd);
	}

	public GraphDrawing copyof()
	{
		GraphDrawing gd;
		gd = new GraphDrawing();
		gd.g = g.copyof();
		gd.embed = embed.copyof();
		gd.gcolor = gcolor.copyof();
		return (gd);
	}

	public GraphDrawing(AdjList h, Embedding e, Coloring c)
	{
		g = h;
		embed = e;
		gcolor = c;
	}

	public void draw_vertex(int v)
	{
		int x, y;
		int r;
		Color c;

		x = embed.x[v];
		y = embed.y[v];

		/*
		 * Allow artificial vertices to be included (defined as those with vertex number >= n so
		 * that these might be used in the future to draw edges which include bends.
		 */
		if (v < g.n)
			r = embed.radius;
		else
			r = 1;

		c = gcolor.vertex_color(v);

		page.setColor(c);
		page.fillOval(x - r, y - r, 2 * r, 2 * r);
		page.setColor(Color.black);
		page.drawString(g.label[v], x, y);
	}

	public void undraw_vertex(int v)
	{
		int x, y;
		int r;
		Color c;

		x = embed.x[v];
		y = embed.y[v];
		if (v < g.n)
			r = embed.radius;
		else
			r = 1;
		c = gcolor.bg_color;

		page.setColor(c);
		page.fillOval(x - r, y - r, 2 * r, 2 * r);
		page.drawString(g.label[v], x, y);
	}

	public void draw_edge(int u, int v)
	{
		int j;
		Color c;
		j = g.find_pos(v, u);
		c = gcolor.arc_color[u][j];
		heavy_line(c, embed.x[u], embed.y[u], embed.x[v], embed.y[v]);
		draw_vertex(u);
		draw_vertex(v);
	}

	public void undraw_edge(int u, int v)
	{
		Color c;

		c = gcolor.bg_color;
		heavy_line(c, embed.x[u], embed.y[u], embed.x[v], embed.y[v]);
		draw_vertex(u);
		draw_vertex(v);
	}

	public void heavy_line(Color c, int xu, int yu, int xv, int yv)
	{
		page.setColor(c);
		page.drawLine(xu, yu, xv, yv);
		//for thickness:
		page.drawLine(xu - 1, yu, xv - 1, yv);
		page.drawLine(xu + 1, yu, xv + 1, yv);
		page.drawLine(xu, yu - 1, xv, yv - 1);
		page.drawLine(xu, yu + 1, xv, yv + 1);
	}

	public void draw_graph(Graphics pg)
	{
		int j, v;
		int u;

		page = pg;

		for (u = 0; u < g.n; u++)
		{
			for (j = 0; j < g.degree[u]; j++)
			{
				v = g.Adj[u][j];
				if (v != AdjList.NO_NEIGHBOUR)
				{
					draw_edge(u, v);
				}
			}
			if (g.degree[u] == 0)
				draw_vertex(u);
		}
	}

	public void clear_screen(Graphics page, int width, int height)
	{
		page.setColor(gcolor.bg_color);
		page.fillRect(0, 0, width, height);
	}

	public boolean is_floater_edge(int u, int v)
	{
		double diff;

		diff = angle_difference(u, v);
		if (diff >= 80 && embed.level[u] > 2.5 && embed.level[v] > 2.5)
			return (true);
		else
			return (false);
	}

	public GraphDrawing add_floaters()
	{
		int u, v, w, j;
		double new_angle, new_level;
		int fake_count;

		GraphDrawing floaters;

		/* How many new vertices do we need? */

		fake_count = 0;
		for (v = 0; v < g.n; v++)
		{
			for (j = 0; j < g.degree[v]; j++)
			{
				u = g.Adj[v][j];
				if (u != AdjList.NO_NEIGHBOUR)
				{
					if (is_floater_edge(u, v))
					{
						fake_count++;
					}

				}
			}
		}
		floaters = make_bigger(fake_count);
		floaters.embed.picture_border_level += 2;
		fake_count = 0;
		for (v = 0; v < g.n; v++)
		{
			for (j = 0; j < g.degree[v]; j++)
			{
				u = g.Adj[v][j];
				if (u != AdjList.NO_NEIGHBOUR)
				{
					if (is_floater_edge(u, v))
					{
						floaters.g.Adj[v][j] = AdjList.NO_NEIGHBOUR;
						w = g.n + fake_count;
						floaters.g.Adj[v][j] = w;
						floaters.g.add_vertex(w, 1);
						floaters.g.Adj[w][0] = v;
						floaters.g.label[w] = " " + u;
						floaters.gcolor.arc_color[w] = new Color[1];
						floaters.gcolor.arc_color[w][0] = gcolor.arc_color[v][j];
						floaters.gcolor.vertex_color[w] = Color.green;
						if (j == 0)
							new_angle = embed.angle[v] + 360 - 10;
						else if (j == 1)
							new_angle = (embed.angle[v] + 360 - 5);
						else
							new_angle = embed.angle[v] + 5;
						new_angle %= 360;
						new_level = embed.level[v] + 2;
						floaters.embed.set_angle_level(w, new_angle, new_level);
						fake_count++;
					}

				}
			}
		}
		return (floaters);
	}

	public double angle_difference(int u, int v)
	{
		double uv, vu;

		uv = directed_angle_difference(u, v); // from u to v
		vu = directed_angle_difference(v, u); // from v to u
		if (uv <= vu)
			return (uv);
		else
			return (vu);
	}

	public double directed_angle_difference(int u, int v)
	{
		double diff;

		diff = embed.angle[v] - embed.angle[u];
		if (diff < 0)
			diff += 360;
		return (diff);
	}

	public GraphDrawing make_bigger(int num_to_add)
	{
		GraphDrawing new_gd;
		new_gd = new GraphDrawing();
		new_gd.g = g.make_bigger(num_to_add);
		new_gd.embed = embed.make_bigger(new_gd.g);
		new_gd.gcolor = gcolor.make_bigger(new_gd.g);
		return (new_gd);
	}
}
