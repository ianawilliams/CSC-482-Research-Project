import java.awt.Color;

public class FindGroup
{
	AdjList g;
	AdjList canG;
	int[] canp;
	int group_order;
	int[][] automorphism;
	int[] dir; /* Keep track of cw/ccw for automorphism. */

	public FindGroup(AdjList h)
	{
		g = h;
		canonicalForm();
		getGroup();
		fixGroup();
	}

	public void change_color(Coloring gcolor)
	{
		AdjList fix, mirror;
		int i, j, u, n_auto;
		int sum;
		/*
		 * Get counts of fixed points and fixed with cw flip for the vertices.
		 */
		int[] vfix, vmirror;

		fix = g.copyof();
		mirror = g.copyof();
		vfix = new int[g.n];
		vmirror = new int[g.n];

		for (i = 0; i < g.n; i++)
		{
			for (j = 0; j < g.degree[i]; j++)
			{
				fix.Adj[i][j] = 0;
				mirror.Adj[i][j] = 0;
			}
		}
		for (i = 0; i < g.n; i++)
		{
			for (n_auto = 0; n_auto < group_order; n_auto++)
			{
				if (automorphism[n_auto][i] == i)
				{
					if (dir[n_auto] == 1)
						vfix[i]++;
					else
						vmirror[i]++;
				}
			}
		}
		for (i = 0; i < g.n; i++)
		{
			for (j = 0; j < gcolor.g.degree[i]; j++)
			{
				u = g.Adj[i][j];
				for (n_auto = 0; n_auto < group_order; n_auto++)
				{
					if (automorphism[n_auto][i] == i && automorphism[n_auto][u] == u)
						fix.Adj[i][j]++;
					else if (automorphism[n_auto][i] == u && automorphism[n_auto][u] == i)
						mirror.Adj[i][j]++;
				}

			}
		}
		for (i = 0; i < g.n; i++)
		{
			if (vfix[i] > 1 && vmirror[i] > 0)
			{
				gcolor.color_vertex(i, Color.red);
			}
			else if (vfix[i] > 1)
			{
				gcolor.color_vertex(i, Color.black);
			}
			else if (vmirror[i] > 0)
			{
				gcolor.color_vertex(i, Color.blue);
			}
			else
			{
				gcolor.color_vertex(i, Color.yellow);
			}
		}
		for (i = 0; i < g.n; i++)
		{
			for (j = 0; j < gcolor.g.degree[i]; j++)
			{
				u = g.Adj[i][j];
				sum = fix.Adj[i][j] + mirror.Adj[i][j];
				switch (sum)
				{
				case 4:
					gcolor.arc_color[i][j] = Color.red;
					break;
				case 2:
					if (fix.Adj[i][j] == 2)
					{
						gcolor.arc_color[i][j] = Color.blue;
					}
					else
					{
						for (n_auto = 0; n_auto < group_order; n_auto++)
						{
							if (automorphism[n_auto][i] == u && automorphism[n_auto][u] == i)
							{
								int n1, n2;
								int pos1, pos2;
								pos1 = g.find_pos(u, i);
								pos2 = g.find_pos(i, u);
								pos1++;
								pos2++;
								pos1 %= g.degree[i];
								pos2 %= g.degree[u];
								n1 = g.Adj[i][pos1];
								n2 = g.Adj[u][pos2];
								if (automorphism[n_auto][n1] == n2)
								{
									gcolor.arc_color[i][j] = Color.black;
									/*
									 * new Color(250,250,80);
									 */
								}
								else
								{
									gcolor.arc_color[i][j] = new Color(204, 0, 255);
								}
								n_auto = group_order;
							}
						}
					}
					break;
				default:
					gcolor.arc_color[i][j] = Color.yellow;
				}

			}
		}
	}

	/* Re-express group in terms of original labelling. */
	public void fixGroup()
	{
		int[] inv_canp = new int[g.n];
		int[] new_auto;
		int i, j;

		for (i = 0; i < g.n; i++)
		{
			inv_canp[canp[i]] = i;
		}
		for (i = 0; i < group_order; i++)
		{
			new_auto = new int[g.n];
			for (j = 0; j < g.n; j++)
			{
				new_auto[inv_canp[j]] = inv_canp[automorphism[i][j]];
			}
			automorphism[i] = new_auto;
		}
	}

	public void printGroup()
	{
		int i, j;
		for (i = 0; i < group_order; i++)
		{
			System.out.print(AdjList.fmt3(i) + ": ");
			for (j = 0; j < g.n; j++)
			{
				System.out.print(AdjList.fmt3(automorphism[i][j]));
			}
			System.out.println();
		}
	}

	public void getGroup()
	{
		AdjList H;
		AdjList fG;
		int root;
		int direction;
		int first_child;
		int[] p;
		int pos, d;
		int cmp;

		automorphism = new int[group_order][];
		dir = new int[group_order];
		int auto_count = 0;

		fG = canG.copyof();
		fG.flipG();

		for (direction = -1; direction <= 1; direction += 2)
		{
			for (root = 0; root < canG.n; root++)
			{
				d = canG.degree[root];
				for (pos = 0; pos < d; pos++)
				{
					first_child = canG.Adj[root][pos];
					if (direction == -1)
					{
						p = special_BFS(root, first_child, 1, fG);
						H = fG.relabel(p);
					}
					else
					{
						p = special_BFS(root, first_child, 1, canG);
						H = canG.relabel(p);
					}
					cmp = H.compare(canG);
					if (cmp == 0)
					{
						dir[auto_count] = direction;
						automorphism[auto_count] = p;
						auto_count++;
					}
				}
			}
		}
	}

	public void canonicalForm()
	{
		AdjList H, fG;
		int root;
		int direction;
		int first_child;
		int[] p;
		int pos, d;
		int cmp;

		group_order = 0;

		/* Get a BFS labelled graph for comparison purposes. */

		p = special_BFS(0, 1, 1, g);
		canG = g.relabel(p);
		canp = p;
		fG = g.copyof();
		fG.flipG();

		for (direction = -1; direction <= 1; direction += 2)
		{
			for (root = 0; root < g.n; root++)
			{
				d = g.degree[root];
				for (pos = 0; pos < d; pos++)
				{
					first_child = g.Adj[root][pos];
					if (direction == -1)
					{
						p = special_BFS(root, first_child, 1, fG);
						H = fG.relabel(p);
					}
					else
					{
						p = special_BFS(root, first_child, 1, g);
						H = g.relabel(p);
					}
					cmp = H.compare(canG);
					/*
					 * commented out since we need to know relationship to original graph labelling.
					 */
//					if (cmp < 0) 
//					{
//						group_order= 1; 
//						canG=H.copyof();
//					}
//					else 
					if (cmp == 0)
					{
						group_order++;
					}
				}
			}
		}
	}

	int[] special_BFS(int root, int first_child, int direction, AdjList G)
	{
		int[] BFI = new int[G.n];
		int[] parent = new int[G.n];
		Queue q;
		int i, u, v;
		int pos, d;

		q = new Queue(G.n);

		for (i = 0; i < G.n; i++)
			parent[i] = -1;

		parent[root] = root;
		BFI[root] = 0;

		d = G.degree[root];
		pos = G.find_pos(first_child, root);
		for (i = 0; i < d; i++)
		{
			u = G.Adj[root][pos];
			parent[u] = root;
			q.add_rear(u);
			BFI[u] = i + 1;
			pos = (pos + direction + d) % d;
		}
		while (q.size() > 0)
		{
			v = q.del_front();
			d = G.degree[v];
			pos = G.find_pos(parent[v], v);
			for (i = 0; i < d; i++)
			{
				u = G.Adj[v][pos];
				if (parent[u] == -1)
				{
					parent[u] = v;
					q.add_rear(u);
					BFI[u] = q.qrear;
				}
				pos = (pos + direction + d) % d;
			}
		}
		return (BFI);
	}
}
