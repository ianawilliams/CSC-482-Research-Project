import java.awt.Color;

public class Patches
{

	// @formatter:off
	final static Color[] patch_color = {
//		Color.yellow,
//		Color.blue,
//		Color.blue,
//		Color.blue,
//		Color.yellow,
//		Color.blue,
//		Color.blue};
//		new Color(255, 0, 255), // hot pink.
//		new Color(255, 0, 204), // Pinkish red.
		Color.yellow,
		new Color(204, 0, 204), // reddish purple.  
		new Color(153, 0, 204), // bluish purple. 
		new Color(0, 0, 204), // dark blue.      
		Color.yellow,
		new Color(204, 0, 255), // grape.          
		new Color(0, 0, 255) // royal blue.
	};
	// @formatter:on

	Fullerene f;
	Coloring gcolor;
	AdjList arc_count;
	int[] vertex_count;

	public Patches(Fullerene g, Coloring c)
	{
		int pos;
		int u, v;
		int i, j, d;

		f = g;
		gcolor = c;
		vertex_count = new int[f.p.n];
		arc_count = new AdjList(f.p.n);
		for (i = 0; i < f.p.n; i++)
		{
			vertex_count[i] = 0;
			d = f.p.degree[i];
			arc_count.add_vertex(i, d);
			for (j = 0; j < d; j++)
				arc_count.Adj[i][j] = 0;
		}
		for (i = 0; i < f.faces.n; i++)
		{
			d = f.faces.degree[i];
			if (d == 5)
			{
				for (j = 0; j < d; j++)
				{
					u = f.faces.Adj[i][j];
					v = f.faces.Adj[i][(j + 1) % d];
					vertex_count[u]++;
					pos = f.p.find_pos(v, u);

//					System.out.println("pos " + pos + "Face edge " + u + " " + v);

					arc_count.Adj[u][pos]++;
				}
			}
		}
	}

	public void change_color()
	{
		int d;
		int i, j, u, v, total;

		for (i = 0; i < f.p.n; i++)
		{
			gcolor.vertex_color[i] = patch_color[vertex_count[i]];
		}
		for (u = 0; u < f.p.n; u++)
		{
			d = f.p.degree[u];
			for (j = 0; j < d; j++)
			{
				v = f.p.Adj[u][j];
				if (u < v)
				{
					total = arc_count.Adj[u][f.p.find_pos(v, u)] + arc_count.Adj[v][f.p.find_pos(u, v)];
					gcolor.arc_color[u][f.p.find_pos(v, u)] = patch_color[4 + total];
					gcolor.arc_color[v][f.p.find_pos(u, v)] = patch_color[4 + total];
				}
			}
		}
	}
}
