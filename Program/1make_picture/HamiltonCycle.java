import java.awt.Color;

public class HamiltonCycle
{
	boolean done;

	GraphDrawing gd;
	Fullerene f;
	int n;
	int[] cycle;
	int[] used;
	int level;

	public HamiltonCycle(GraphDrawing p, Fullerene g)
	{
		int u;

		gd = p;
		f = g;
		n = f.p.n;
		cycle = new int[n];
		used = new int[n];
		cycle[0] = 0;
		used[0] = 1;
		for (u = 1; u < n; u++)
			cycle[u] = -1;
		level = 1;
		done = false;
	}

	public boolean next_step()
	{
//		int initial_level;
		boolean ok;
		int pos;

		int u;
		int w;
		int x, y, z;

//		System.out.print("Level " + level + " : ");

//		print_cycle();
//		initial_level = level;

		/* Color Ham. cycle edges black and the other ones red. */

		if (level == n)
		{
			if (f.p.is_edge(cycle[0], cycle[level - 1]) && cycle[1] < cycle[n - 1])
			{
				gd.gcolor = new Coloring(gd.g, 4);

				/*
				 * Colour edges being sure to handle floater edges appropriately.
				 */
				for (w = 0; w < n; w++)
				{
					x = cycle[w];
					y = cycle[(w + 1) % n];
					pos = f.p.find_pos(y, x);
					z = gd.g.Adj[x][pos];
					gd.gcolor.color_edge(x, z, Color.blue);
					if (y != z)
					{
						pos = f.p.find_pos(x, y);
						z = gd.g.Adj[y][pos];
						gd.gcolor.color_edge(y, z, Color.blue);
					}
				}
				FUIGUI.fullereneArea.new_picture(gd);
				// System.out.println("Found Hamilton cycle.");
				level--;
				used[cycle[level]] = 0;
				cycle[level] = -1;
				level--;
				return (true);
			}
			level--;
			used[cycle[level]] = 0;
			cycle[level] = -1;
			level--;
			return (false);
		}
		u = cycle[level];
		if (u >= 0)
			used[u] = 0;
		for (u = cycle[level] + 1; u < n; u++)
		{
			if (f.p.is_edge(cycle[level - 1], u) && used[u] == 0)
			{
				// System.out.println("Try " + u + " at level "
				// 			+ level);
				used[u] = 1;
				cycle[level] = u;
				level++;
				ok = next_step();
				//      if (ok)
				// 		System.out.println("OK Initial level " 
				// 		+ initial_level + " level " + level); 
				if (ok)
					return (true);
				else
					return (false);
			}
		}
		cycle[level] = -1;
		level--;
		if (level == 1)
		{
			done = true;
			return (true);
		}
		else
			return (false);
	}

	public void print_cycle()
	{
		int i;
		for (i = 0; i < level; i++)
			System.out.print(cycle[i] + " ");
		System.out.println();
	}
}
