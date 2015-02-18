import java.util.Arrays;

/*  
 Creator: Wendy Myrvold
 Last updated : Jan. 2006

 Find the angles/levels for an embedding of a fullerene
 with a selected center which can be
 a face, a vertex (treated as a face of size 1), or
 an edge (treated as a face of size 2).

 */
public class FaceCenter
{
	AdjList g;
	int[] level;
	int[] angle;

	private static boolean DEBUG = false;

	public FaceCenter(AdjList h)
	{
		int i;

		g = h;
		level = new int[g.n];
		angle = new int[g.n];

		for (i = 0; i < g.n; i++)
		{
			level[i] = -1;
		}
	}

	public void glitch_fix(Queue q)
	{
		/*
		 * If a vertex is adjacent to two vertices at a lower level or it is adjacent to three at a
		 * lower level or its three neighbours are at the same level as itself then this is a glitch
		 * which needs fixing. We will move these vertices to an odd level which is one higher than
		 * their current level.
		 */
		int[] count;
		int[] fix = new int[g.n];
		int i;

		if (DEBUG)
			System.out.println("Fixing Glitches -------------------");

		int last_level = level[q.queue[g.n - 1]];

		for (i = 0; i < g.n; i++)
		{
			count = edge_count(i);
			if (count[0] == 2 || count[0] == 3)
			{
				fix[i] = -1;
			}
			else if (count[1] == 3)
			{
				if (!down2_adj(i))
				{
					if (level[i] < last_level)
						fix[i] = 1;
				}
			}
			else
				fix[i] = 0;

			if (DEBUG)
				if (fix[i] != 0)
					System.out.println("Glitch vertex " + i + "   " + count[0] + "   " + count[1] + "   " + count[2] + " fix " + fix[i]);
		}
		for (i = 0; i < g.n; i++)
		{
			level[i] += fix[i];
		}
	}

	public boolean down2_adj(int v)
	{
		int j, u;
		int d;

		d = g.degree[v];
		for (j = 0; j < d; j++)
		{
			u = g.Adj[v][j];
			if (num_down_edges(u) == 2)
				return (true);

		}
		return (false);
	}

	/*
	 * Bubble sort the queue to ensure sorted by levels after the glitch fixing relabelling.
	 */
	public void bubble_sort(Queue q)
	{
		int i;

		for (i = 1; i < g.n; i++)
		{
			bubble_down(i, q);
		}
	}

	public void bubble_down(int start, Queue q)
	{
		while (start > 0 && level[q.queue[start]] < level[q.queue[start - 1]])
		{
			q.swap(start, start - 1);
			start--;
		}
	}

	public void label(Queue q)
	{
		int u, v, d;
		int j;

		int new_label;

		/* If the caller forgets to do this, this causes a crash. */
		Arrays.fill(level, -1);

		if (q.size() == 1)
			new_label = 0;
		else
			new_label = 2;

		for (v = 0; v < q.size(); v++)
		{
			u = q.queue[v];
			level[u] = new_label;
		}

		/* Label as k+2 vertices in faces with k labelled ones. */
		/*
		 * Modified Dec. 17, 2005: start traversal of faces of a vertex with the neighbour which was
		 * first placed in the queue. This should allow starting on an arbitrary face/vertex/edge.
		 * The original version worked on the initial fullerene as an artifact of the vertex
		 * labelling but now it should work all the time.
		 */

		while (q.size() > 0)
		{
			v = q.del_front();
			d = g.degree[v];

			/* Find neighbour with smallest index in the queue. */

			int minj = 0;
			int minu = g.Adj[v][0];

			for (j = 1; j < d; j++)
			{
				u = g.Adj[v][j];
				if (q.myPosition[u] < q.myPosition[minu])
					minj = j;
			}

			/*
			 * Now start with that vertex when going around the faces.
			 */

			for (j = 0; j < d; j++)
			{
				u = g.Adj[v][(minj + j) % d];
				label_face(v, u, q);
			}
		}
	}

	public void label_face(int v, int u, Queue q)
	{
		int current, prev, next;
		int j, d;

		prev = v;
		current = u;

		do
		{
			if (level[current] < 0)
			{
				level[current] = level[v] + 2;
				q.add_rear(current);
			}
			j = g.find_pos(prev, current);
			d = g.degree[current];
			j = (j + d - 1) % d;
			next = g.Adj[current][j];
			prev = current;
			current = next;
		} while (current != v);
	}

	int num_down_edges(int v)
	{
		int[] count;

		count = edge_count(v);
		return (count[0]);
	}

	int[] edge_count(int v)
	{
		int u, j, degree;
		int[] count = new int[3];

		degree = g.degree[v];
		for (j = 0; j < degree; j++)
		{
			u = g.Adj[v][j];
			if (level[u] < level[v])
				count[0]++;
			else if (level[u] == level[v])
				count[1]++;
			else if (level[u] > level[v])
				count[2]++;
		}
		return (count);
	}

	public void find_angle(Queue q)
	{
		int pos;
		int angle_add;
		int num;
		int a;
		int v;
		int i;

		pos = 0;
		num = count_level(pos, q);
		while (num > 0)
		{
			v = q.queue[pos];

			/*
			 * Evenly space even level vertices but ignore the ones on the odd levels for now.
			 */

			if (level[v] % 2 == 0)
			{
				angle_add = 360 / num;
				a = 270;
				for (i = 0; i < num; i++)
				{
					v = q.queue[pos];
					angle[v] = a;
					// BUG FIX August 3, 2010
					// The last vertex was not evenly spaced due to truncation at
					// every step. I changed the code so that the truncation does not
					// accumulate.
					a = 270 + ((i + 1) * 360) / num; // Changed Aug. 3, 2010
					a %= 360;
					pos++;
				}
			}
			else
				pos += num;

			num = count_level(pos, q);
		}
	}

	public int count_level(int pos, Queue q)
	{
		int num;
		int label;

		if (pos >= g.n)
			return (0); /* no more left. */

		label = level[q.queue[pos]];
		num = 0;
		while (pos < g.n && level[q.queue[pos]] == label)
		{
			num++;
			pos++;
		}
		return (num);
	}

	public void move_outer_layer(Queue q)
	{
		int pos;
		int num;
		int v;
		int i;
		int incr = 0;

		pos = 0;
		num = count_level(pos, q);
		while (num > 0)
		{
			if (num <= 6 + g.n / 10 && pos > 3 * g.n / 4)
				incr += 2;
			for (i = 0; i < num; i++)
			{
				v = q.queue[pos];
				level[v] += incr;
				pos++;
			}
			num = count_level(pos, q);
		}
	}

	public void move_out(Queue q)
	{
		int pos;
		int num;
		int i, v;
		int incr;
		int previous_level;

		incr = 0;
		pos = 0;
		v = q.queue[pos];
		previous_level = level[v] - 2;

		num = count_level(pos, q);
		while (num > 0)
		{
			v = q.queue[pos];
			if (level[v] - previous_level < 2)
				incr++;
			previous_level = level[v];
			if (pos + num == g.n)
				incr += 2;

			for (i = 0; i < num; i++)
			{
				v = q.queue[pos];
				level[v] += incr;
				pos++;
			}
			num = count_level(pos, q);
		}
	}

	public void find_rotation(Queue q)
	{
		int pos;
		int num;
		int a;
		int v, p;
		int i;
		int mina;
		int minp;

		pos = 0;
		num = count_level(pos, q);

		while (num > 0)
		{
			int current_level = level[q.queue[pos]];
			if (current_level >= 4 && current_level % 2 == 0)
			{
				mina = 0;
				minp = 5 * 360 * num;
				for (a = 0; a < 360; a++)
				{
					p = penalty(pos, num, q, a);
					if (p < minp)
					{
						minp = p;
						mina = a;
					}
				}
				for (i = 0; i < num; i++)
				{
					v = q.queue[pos];
					angle[v] += mina;
					angle[v] %= 360;
					pos++;
				}
			}
			else if (current_level % 2 == 1)
			{
				for (i = 0; i < num; i++)
				{
					v = q.queue[pos];

					/* Find angle for v */
					angle[v] = glitch_angle(v, q);

					pos++;
				}
			}
			else
			{
				pos += num;
			}
			num = count_level(pos, q);
		}
	}

	/*
	 * If a vertex is adjacent to two vertices at a lower level or it is adjacent to three at a
	 * lower level or its three neighbours are at the same level as itself then this is a glitch
	 * which needs fixing. We will move these vertices to an odd level which is one higher than
	 * their current level.
	 */
	int glitch_angle(int v, Queue q)
	{
		int nd, d;
		int[] down_v;
		int i, j, u;
		int d1, d2, a1, a2;
		int[] p;
		int a;

		d = g.degree[v];
		down_v = new int[d];
		nd = 0;
		for (i = 0; i < d; i++)
		{
			u = g.Adj[v][i];
			if (level[u] < level[v])
			{
				down_v[nd] = u;
				nd++;
			}
		}
		switch (nd)
		{

		case 1:
			a = angle[down_v[0]];
			break;
		case 2:
			a1 = angle[down_v[0]];
			a2 = angle[down_v[1]];
			d1 = (a1 - a2 + 360) % 360;
			d2 = (a2 - a1 + 360) % 360;
			if (d1 < d2)
				a = (a2 + d1 / 2) % 360;
			else
				a = (a1 + d2 / 2) % 360;
			break;
		case 3:
			p = new int[nd];
			for (i = 0; i < nd; i++)
				p[i] = q.myPosition[down_v[i]];
			j = mid_of_3(p);
			a = angle[down_v[j]];
			break;
		default:
			if (DEBUG)
				System.out.println("*** Error- glitch with no down edges.");
			a = 0;
		}
		return (a);
	}

	int mid_of_3(int[] p)
	{
		int i, j;
		int[] n_smaller = new int[3];
		for (i = 0; i < 3; i++)
		{
			for (j = 0; j < 3; j++)
			{
				if (p[j] < p[i])
					n_smaller[i]++;
			}
		}
		for (i = 0; i < 3; i++)
		{
			if (n_smaller[i] == 1)
				return (i);
		}
		return (-1);
	}

	public int penalty(int pos, int num, Queue q, int incr)
	{
		int i, j;
		int ad;
		int u, v;
		int a1, a2;
		int d1, d2;
		int diff;
		int p, d;
		int max;
		int maxp; /* Try to fix edge start. */

		p = 0;
		maxp = 0;
		for (i = 0; i < num; i++)
		{
			v = q.queue[pos];

			/*
			 * Try taking the average difference from v to its neighbours as the penalty.
			 */
			ad = 0;
			d = g.degree[v];
			a1 = (angle[v] + incr) % 360;
			for (j = 0; j < d; j++)
			{
				u = g.Adj[v][j];
				if (level[u] < level[v])
				{
					ad++;
					a2 = angle[u];
					d1 = (a1 - a2 + 360) % 360;
					d2 = (a2 - a1 + 360) % 360;
					if (d1 < d2)
						diff = d1;
					else
						diff = d2;
					p += diff;
					if (diff > maxp)
						maxp = diff;
				}

			}
			if (ad == 2) /* Try to get it centered over the two. */
			{
				max = 0;
				for (j = 0; j < d; j++)
				{
					u = g.Adj[v][j];
					if (level[u] < level[v])
					{
						a2 = angle[u];
						d1 = (a1 - a2 + 360) % 360;
						d2 = (a2 - a1 + 360) % 360;
						if (d1 < d2)
							diff = d1;
						else
							diff = d2;
						p -= diff;
						if (diff > max)
							max = diff;
					}
				}
				p += 2 * max;
			}
			pos++;
		}
		return (p + maxp);
	}

	/* Center vertices with two lower level neighbours. */
	public void move_to_middle()
	{
		int j;
		int ad;
		int u, v;
		int a1, a2;
		int d1, d2;
		int d;

		for (v = 0; v < g.n; v++)
		{
			ad = 0;
			d = g.degree[v];
			for (j = 0; j < d; j++)
			{
				u = g.Adj[v][j];
				if (level[u] < level[v])
				{
					ad++;
				}
			}
			if (ad == 2) /* Get it exactly centered over the two. */
			{
				a1 = -1;
				a2 = -1;
				for (j = 0; j < d; j++)
				{
					u = g.Adj[v][j];
					if (level[u] < level[v])
					{
						if (a1 < 0)
							a1 = angle[u];
						else
							a2 = angle[u];
					}
				}
				d1 = (a1 - a2 + 360) % 360;
				d2 = (a2 - a1 + 360) % 360;
				if (d1 < d2)
				{
					angle[v] = (a2 + d1 / 2) % 360;
				}
				else
				{
					angle[v] = (a1 + d2 / 2) % 360;
				}
			}
		}
	}

	/*
	 * You can call with a face or a single vertex as a "face" for the center of the picture.
	 */

	public void redraw(int fsize, int[] face, Fullerene f)
	{
		Queue q;
		int i;

		q = new Queue(f.p.n);

		for (i = 0; i < fsize; i++)
		{
			q.add_rear(face[i]);
		}
		label(q);
		glitch_fix(q);
		bubble_sort(q);
		find_angle(q);
		find_rotation(q);
		move_out(q);
		/* Move starting edge in a bit. */
		if (fsize == 2)
		{
			level[q.queue[0]] = 1;
			level[q.queue[1]] = 1;
		}
	}
}
