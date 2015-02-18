public class Label
{
	AdjList g;
	int[] vertex_count;

	public Label(AdjList h)
	{
		g = h;
		vertex_count = new int[g.n];
	}

	public static void label(int option, Fullerene f)
	{
		label(option, f.p);
		label(option, f.d);
	}

	public static void label(int option, AdjList g)
	{
		Label vs;
		int i;

		switch (option)
		{
		default:
		case 0:
			for (i = 0; i < g.n; i++)
				g.label[i] = " " + i;
			break;
		case 1:
			for (i = 0; i < g.n; i++)
				g.label[i] = " ";
			break;
		case 2:
			vs = new Label(g);
			int num = vs.count_spiral();
			for (i = 0; i < g.n; i++)
				g.label[i] = " " + vs.vertex_count[i];
			break;
		case 3:
			vs = new Label(g);
			FindGroup group = new FindGroup(g);
			vs.count_site_auto(group);
			for (i = 0; i < g.n; i++)
				g.label[i] = " " + vs.vertex_count[i];

		}
	}

	public int count_spiral()
	{
		int f1, f2;
		int count, dir;
		int deg;
		int j;

		count = 0;
		for (f1 = 0; f1 < g.n; f1++)
			vertex_count[f1] = 0;
		for (f1 = 0; f1 < g.n; f1++)
		{
			deg = g.degree[f1];
			for (j = 0; j < deg; j++)
			{
				f2 = g.Adj[f1][j];
				{
					for (dir = -1; dir <= 1; dir += 2)
					{
						if (ok_spiral(f1, f2, dir) == 1)
						{
							count++;
							vertex_count[f1]++;
						}
					}
				}
			}
		}
		return (count);
	}

	public int ok_spiral(int f1, int f2, int direction)
	{
		int pos;
		int prev, current, next;
		int deg;
		boolean[] visited;
		int i, j, u;

		visited = new boolean[g.n];
		for (i = 0; i < g.n; i++)
			visited[i] = false;

		pos = 0;
		prev = -1;
		current = f1;
		next = -1;
		do
		{
			visited[current] = true;
			pos++;
			if (pos == 1)
			{
				next = f2;
			}
			else if (pos < g.n)
			{
				j = g.find_pos(prev, current);
				deg = g.degree[current];
				next = -1;
				for (i = 0; i < deg - 1; i++)
				{
					j += deg + direction;
					j %= deg;
					u = g.Adj[current][j];
					if (!visited[u])
					{
						next = u;
						break;
					}
				}
				if (next == -1)
					return (0);
			}
			prev = current;
			current = next;
		} while (pos < g.n);
		return (1);
	}

	public void count_site_auto(FindGroup group)
	{
		int i, j;
		for (i = 0; i < g.n; i++)
			vertex_count[i] = 0;
		for (i = 0; i < group.group_order; i++)
		{
			for (j = 0; j < g.n; j++)
			{
				if (group.automorphism[i][j] == j)
					vertex_count[j]++;
			}
		}
	}
}
