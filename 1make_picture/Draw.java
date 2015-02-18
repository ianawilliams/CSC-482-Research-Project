/*     
 *    Illustrate the steps of the drawing algorithm with an 
 *    animated algorithm.
 */

import java.awt.Color;

public class Draw
{
	Fullerene f;
	GraphDrawing gd;
	AdjList level_graph;
	DrawingOption draw_option;
	Coloring c;
	FaceCenter dwd;
	Queue q;
	int phase;

	int pos;
	int num;
	int a;
	int mina;
	int minp;
	boolean do_next_level;
	double current_level;

	public Draw(Fullerene h)
	{
		f = h;
		draw_option = new DrawingOption(f);
		dwd = new FaceCenter(f.p);
		q = new Queue(f.p.n);
		init_redraw(draw_option.face_size, draw_option.face, f);
		c = new Coloring(f.p);
		gd = new GraphDrawing(dwd, c);
		current_level = 4;
		modify_coloring();
		gd.g = level_graph;
		FUIGUI.fullereneArea.new_picture(gd);
		phase = 1;
		pos = 0;
		num = 0;
		do_next_level = true;
	}

	public boolean next_step()
	{
		boolean not_done;

		gd.g = f.p;
		not_done = true;
		switch (phase)
		{
		case 1:
			find_rotation();
			break;
		case 2:
			dwd.move_to_middle();
			phase = 3;
			break;
		case 3:
			last_step();
			not_done = false;
		default:
		}
		gd = new GraphDrawing(dwd, gd.gcolor);
		gd.g = level_graph;
		FUIGUI.fullereneArea.new_picture(gd);
		return (not_done);
	}

	public void last_step()
	{
		int i, j;
		int u, v;

		/* Make all the edges red. */

		for (i = 0; i < gd.g.n; i++)
		{
			for (j = 0; j < gd.g.degree[i]; j++)
			{
				u = gd.g.Adj[i][j];
				gd.gcolor.color_edge(i, u, Color.red);
			}
		}

		/*
		 * Move an highlight last edge if it is an edge centered picture.
		 */
		if (draw_option.face_size == 2)
		{
			u = q.queue[0];
			v = q.queue[1];
			dwd.level[u] = 1;
			dwd.level[v] = 1;
			gd.gcolor.color_edge(u, v, Color.blue);
		}
	}

	public void find_rotation()
	{
		int v, p, i;

		if (do_next_level)
		{
			pos += num;
			num = dwd.count_level(pos, q);
			if (num <= 0)
				current_level++;
			else
				current_level = dwd.level[q.queue[pos]];
			modify_coloring();
			if (num <= 0)
			{
				phase = 2;
				return;
			}
			if (dwd.level[q.queue[pos]] < 4)
			{
				return;
			}
			a = 0;
			mina = 0;
			minp = 5 * 360 * num;
			do_next_level = false;
		}
		else
		{
			do
			{
				int t = pos;
				for (i = 0; i < num; i++)
				{
					v = q.queue[t];
					dwd.angle[v]++;
					dwd.angle[v] %= 360;
					t++;
				}
				p = dwd.penalty(pos, num, q, 0);
				System.out.println("Rotation " + a + " penalty : " + p);
				if (a < 360)
				{
					if (p < minp)
					{
						minp = p;
						mina = a;
					}
				}
				else
				{
					t = pos;
					for (i = 0; i < num; i++)
					{
						v = q.queue[t];
						dwd.angle[v] += mina;
						dwd.angle[v] %= 360;
						t++;
					}
					p = dwd.penalty(pos, num, q, 0);
					do_next_level = true;

					/*
					 * Rotate around until you are back to the place with the minimum penalty.
					 */

//			     	if ((a % 360) == mina)
//			     	{
//						do_next_level=true;
//			     	}
				}
				a++;
			} while ((a % 4) != 0 && !do_next_level);
		}
	}

	/*
	 * You can call with a face or a single vertex as a "face" for the center of the picture.
	 */

	public void init_redraw(int fsize, int[] face, Fullerene f)
	{
		int i;

		for (i = 0; i < fsize; i++)
		{
			q.add_rear(face[i]);
		}
		dwd.label(q);
		dwd.find_angle(q);
		dwd.move_outer_layer(q);
	}

	public void modify_coloring()
	{
		double lu, lv;
		int cu, cv;
		int v, j, u;

		level_graph = f.p.copyof();

		for (v = 0; v < f.p.n; v++)
		{
			for (j = 0; j < f.p.degree[v]; j++)
			{
				u = f.p.Adj[v][j];
				if (u != -1)
				{
					lu = gd.embed.level[u];
					lv = gd.embed.level[v];
					if (lu < 0)
						lu = f.p.n;
					if (lv < 0)
						lv = f.p.n;
					cu = compare(lu, current_level);
					cv = compare(lv, current_level);
					if (cu < 0 && cv < 0)
					{
						gd.gcolor.color_edge(u, v, Color.red);
					}
					else if (cu == 1 || cv == 1)
					{
						level_graph.Adj[v][j] = AdjList.NO_NEIGHBOUR;
						level_graph.Adj[u][f.p.find_pos(v, u)] = AdjList.NO_NEIGHBOUR;
						gd.gcolor.color_edge(u, v, Color.black);
					}
					else if (cu == 0 && cv == 0)
					{
						gd.gcolor.color_edge(u, v, Color.blue);
					}
					else
					{
						gd.gcolor.color_edge(u, v, Color.yellow);
					}
				}
			}
		}
	}

	public static int compare(double x1, double x2)
	{
		if (x1 < x2 - 0.001)
			return (-1);
		if (x1 > x2 + 0.001)
			return (1);
		return (0);
	}
}
