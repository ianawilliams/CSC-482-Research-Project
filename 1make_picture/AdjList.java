/* 
   Data structures for a graph.
   Can be used both for a fullerene and for its dual graph.
   As used in the code, these are rotation systems which
   give the clockwise order of the neighbours of each vertex.

   Creator: Wendy Myrvold
   Last Update: Oct. 2005.
 */
public class AdjList
{
	/* Can be used for quick deletion of edges. */
	public static final int NO_NEIGHBOUR = -1;
	int n; /* Desired number of vertices. */
	int[] degree; /* Degree of each vertex including -1 entries. */
	int[][] Adj; /* Rotation system. */
	String[] label; /* String printed with each vertex. */

	public AdjList(int nv)
	{
		int i;
		n = nv;
		degree = new int[n];
		Adj = new int[n][];
		label = new String[n];
		for (i = 0; i < nv; i++)
			label[i] = " " + i;
	}

	public AdjList(int nv, int deg)
	{
		int i;
		n = nv;
		degree = new int[n];
		Adj = new int[n][];
		label = new String[n];
		for (i = 0; i < nv; i++)
		{
			label[i] = " " + i;
			add_vertex(i, deg);
		}
	}

	public AdjList copyof()
	{
		int u, j;

		AdjList g;
		g = new AdjList(n);
		for (u = 0; u < n; u++)
		{
			g.label[u] = label[u];
			g.degree[u] = degree[u];
			g.Adj[u] = new int[degree[u]];
			for (j = 0; j < degree[u]; j++)
				g.Adj[u][j] = Adj[u][j];
		}
		return (g);
	}

	public boolean is_edge(int u, int v)
	{
		int pos;

		pos = find_pos(u, v);
		if (pos < 0)
			return (false);
		else
			return (true);
	}

	public void add_vertex(int u, int d)
	{
		int i;
		degree[u] = d;
		Adj[u] = new int[d];
		for (i = 0; i < d; i++)
			Adj[u][i] = NO_NEIGHBOUR;
	}

	public void add_arc(int u, int v, int pos)
	{
		Adj[u][pos] = v;
	}

	public void add_edge(int posu, int u, int posv, int v)
	{
		Adj[u][posu] = v;
		Adj[v][posv] = u;
	}

	public int find_pos(int u, int v)
	{
		int d, i;

		d = degree[v];
		for (i = 0; i < d; i++)
		{
			if (Adj[v][i] == u)
			{
				return (i);
			}
		}
		return (-1);
	}

	public void print_graph(String header)
	{
		int i, j, u;
		System.out.println(header);
		System.out.println("Number of vertices " + n);
		for (i = 0; i < n; i++)
		{
			System.out.print(fmt(i) + " : ");
			for (j = 0; j < degree[i]; j++)
			{
				u = Adj[i][j];
				System.out.print(fmt(u) + " ");
			}
			System.out.println();
		}
	}

	public static String fmt(int k)
	{
		String s;
		s = k + " ";
		while (s.length() < 4)
			s = " " + s;
		return (s);
	}

	public static String fmt3(int k)
	{
		String s;
		s = k + " ";
		while (s.length() < 3)
			s = " " + s;
		return (s);
	}

	/* Make a bigger one that is otherwise the same as the current one. */

	public AdjList make_bigger(int num_to_add)
	{
		AdjList bigger;
		int i, j;

		bigger = new AdjList(n + num_to_add);

		for (i = 0; i < n; i++)
		{
			bigger.degree[i] = degree[i];
			bigger.label[i] = label[i];
			bigger.label[i] = label[i];
			// Next three lines modified by JENNI to
			// allow for invisible vertices - nov 2005
			int d = degree[i];
			if (d < 0)
				d = 0;
			bigger.Adj[i] = new int[d];

			for (j = 0; j < degree[i]; j++)
			{
				bigger.Adj[i][j] = Adj[i][j];
			}
		}
		return (bigger);
	}

	public void printUpper()
	{
		int[][] G;
		int i, j, d;
		int u;

		G = new int[n][n];
		for (i = 0; i < n; i++)
		{
			d = degree[i];
			for (j = 0; j < d; j++)
			{
				u = Adj[i][j];
				G[i][u] = 1;
				G[u][i] = 1;
			}
		}
		System.out.println("" + n);
		for (i = 0; i < n; i++)
		{
			for (j = i + 1; j < n; j++)
			{
				System.out.print(G[i][j]);
			}
			System.out.println();
		}
	}

	/* Reverse the sense of clockwise for the rotation system. */

	public void flipG()
	{
		int i;

		for (i = 0; i < n; i++)
			flipVertex(i);
	}

	/* Keep it normalized if normalized already */
	public void flipVertex(int v)
	{
		int left, right, t;

		left = 1;
		right = degree[v] - 1;
		while (left < right)
		{
			t = Adj[v][left];
			Adj[v][left] = Adj[v][right];
			Adj[v][right] = t;
			left++;
			right--;
		}
	}

	public int compare(AdjList h)
	{
		int i, j;

		if (n < h.n)
			return (-1);
		if (n > h.n)
			return (1);

		for (i = 0; i < n; i++)
		{
			if (degree[i] < h.degree[i])
				return (-1);
			if (degree[i] > h.degree[i])
				return (1);

			for (j = 0; j < degree[i]; j++)
			{
				if (Adj[i][j] < h.Adj[i][j])
					return (-1);
				if (Adj[i][j] > h.Adj[i][j])
					return (1);
			}
		}
		return (0);
	}

	public void normalizeVertex(int v)
	{
		int[] u;
		int min, min_pos;
		int i;

		u = new int[degree[v]];
		min = n + 1;
		min_pos = -1;
		for (i = 0; i < degree[v]; i++)
		{
			u[i] = Adj[v][i];
			if (u[i] < min)
			{
				min = u[i];
				min_pos = i;
			}
		}
		if (min_pos == 0)
			return;
		for (i = 0; i < degree[v]; i++)
		{
			Adj[v][i] = u[(i + min_pos) % degree[v]];
		}
	}

	public void normalize()
	{
		int i;

		for (i = 0; i < n; i++)
			normalizeVertex(i);
	}

	public AdjList relabel(int[] p)
	{
		int i, j;
		int u;
		AdjList h;

		h = new AdjList(n);

		for (i = 0; i < n; i++)
		{
			u = p[i];
			h.degree[u] = degree[i];
			h.Adj[u] = new int[h.degree[u]];
			for (j = 0; j < degree[i]; j++)
			{
				h.Adj[u][j] = p[Adj[i][j]];
			}
		}
		h.normalize();
		return (h);
	}

	// insert an edge (increasing the degree of both vertices) ADDED BY JENNI
	// NOV 2005
	public void insert_edge(int posu, int u, int posv, int v)
	{
		// add v to u's list
		// System.out.println("----Adding v="+v+" to u="+u+"'s list in position "+posu);
		int[] newAdju = new int[degree[u] + 1];
		for (int i = 0; i < posu; i++)
			newAdju[i] = Adj[u][i];
		newAdju[posu] = v;
		for (int i = posu + 1; i < degree[u] + 1; i++)
			newAdju[i] = Adj[u][i - 1];
		degree[u]++;
		Adj[u] = newAdju;

		// add u to v's list
		// System.out.println("----Adding u="+u+" to v="+v+"'s list in position "+posv);
		int[] newAdjv = new int[degree[v] + 1];
		for (int i = 0; i < posv; i++)
			newAdjv[i] = Adj[v][i];
		newAdjv[posv] = u;
		for (int i = posv + 1; i < degree[v] + 1; i++)
			newAdjv[i] = Adj[v][i - 1];
		degree[v]++;
		Adj[v] = newAdjv;
	}

	// remove an edge (decreasing the degree of both vertices) ADDED BY JENNI
	// NOV 2005
	public void remove_edge(int u, int v)
	{
		// remove v from u's list
		int[] newAdju = new int[degree[u] - 1];
		int posu = find_pos(v, u);
		// System.out.println("----Removing v="+v+" from u="+u+"'s list, position "+posu);
		for (int i = 0; i < posu; i++)
			newAdju[i] = Adj[u][i];
		for (int i = posu + 1; i < degree[u]; i++)
		{
			newAdju[i - 1] = Adj[u][i];
		}
		degree[u]--;
		Adj[u] = newAdju;

		// remove u from v's list
		int[] newAdjv = new int[degree[v] - 1];
		int posv = find_pos(u, v);
		// System.out.println("----Removing u="+u+" from v="+v+"'s list, position "+posv);
		for (int i = 0; i < posv; i++)
			newAdjv[i] = Adj[v][i];
		for (int i = posv + 1; i < degree[v]; i++)
			newAdjv[i - 1] = Adj[v][i];
		degree[v]--;
		Adj[v] = newAdjv;
	}

}
