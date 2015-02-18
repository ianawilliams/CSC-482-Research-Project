/* Represents a colouring of an embedding.
 * Created by Wendy Myrvold
 * Last updated: Oct. 2005.
 * Each vertex and "arc" has a colour.
 * If the adjacency list g.Adj[u][j]= v then the colour
 * of arc (u, v) is stored in arc_color[u][j].
 */
import java.awt.Color;

public class Coloring
{

	/*
	 * Default vertex colour is a light yellow, and default arc colour is light red. The default
	 * background colour is a light cyan. Marsha selected these as they gave nicer screen shots for
	 * overhead slides than the default red/yellow/cyan which java has.
	 */

	// @formatter:off
	final static Color[][] default_color = {
		{
			new Color(200, 250, 250), // light cyan background.
			new Color(250, 250, 80), // orange vertices
			new Color(250, 70, 40) // red edges
		},
		{
			Color.white, // background
			new Color(138, 138, 138), // vertices-dark gray
			new Color(192, 192, 192) // edges-light gray
		},
		{
			new Color(200, 250, 250), // light cyan background.
			new Color(127, 112, 250), // vertices- blue
			new Color(231, 132, 250) // edges-light purple
		},
		{
			new Color(200, 250, 250), // light cyan background.
			new Color(250, 70, 40), // red vertices
			new Color(231, 132, 250) // edges-light purple
		},
		{
			new Color(200, 250, 250), // light cyan background.
			new Color(231, 132, 250), // vertices-light purple
			Color.yellow // yellow edges
		}
		};
	// @formatter:on

	AdjList g; /* Rotation system of the embedding. */
	Color[] vertex_color; /* Color of each vertex. */
	Color[][] arc_color; /* Colors of the edges. */
	Color bg_color; /* Background color for the drawing. */

	public Coloring(AdjList h)
	{
		int option = 0;
		this.color(h, default_color[option][0], default_color[option][1], default_color[option][2]);
	}

	public Coloring(AdjList h, int option)
	{
		this.color(h, default_color[option][0], default_color[option][1], default_color[option][2]);
	}

	public Coloring(AdjList h, Color bg, Color vc, Color ec)
	{
		this.color(h, bg, vc, ec);
	}

	public void color(AdjList h, Color bg, Color vc, Color ec)
	{
		int i, j, d;

		g = h;
		bg_color = bg;
		vertex_color = new Color[g.n];
		arc_color = new Color[g.n][];

		for (i = 0; i < g.n; i++)
		{
			vertex_color[i] = vc;
			d = g.degree[i];
			if (d < 0)
				d = 0;
			arc_color[i] = new Color[d];
			for (j = 0; j < d; j++)
			{
				arc_color[i][j] = ec;
			}
		}
	}

	public Coloring copyof()
	{
		int i, j, d;

		Coloring c;
		c = new Coloring(this.g);
		for (i = 0; i < g.n; i++)
		{
			c.vertex_color[i] = vertex_color[i];
			d = g.degree[i];
			for (j = 0; j < d; j++)
			{
				c.arc_color[i][j] = arc_color[i][j];
			}
		}
		c.bg_color = bg_color;
		return (c);
	}

	public void set_bg(Color c)
	{
		bg_color = c;
	}

	public void color_vertex(int u, Color c)
	{
		vertex_color[u] = c;
	}

	public Color vertex_color(int u)
	{
		return (vertex_color[u]);
	}

	public void color_edge(int u, int v, Color c)
	{
		int pos;

		pos = g.find_pos(v, u);
		if (pos >= 0)
			arc_color[u][pos] = c;

		pos = g.find_pos(u, v);
		if (pos >= 0)
			arc_color[v][pos] = c;
	}

	public void color_arc(int u, int v, Color c)
	{
		arc_color[u][g.find_pos(v, u)] = c;
	}

	public Color edge_color(int u, int v)
	{
		if (u < v)
			return (arc_color(u, v));
		else
			return (arc_color(v, u));
	}

	public Color arc_color(int u, int v)
	{
		return (arc_color[u][g.find_pos(v, u)]);
	}

	/* Make a bigger one that is otherwise the same as the current one. */
	/* Its new AdjList is h. */

	public Coloring make_bigger(AdjList h)
	{
		Coloring new_color;
		int i, j;

		new_color = new Coloring(h);
		new_color.bg_color = bg_color;
		for (i = 0; i < g.n; i++)
		{
			new_color.vertex_color[i] = vertex_color[i];
			for (j = 0; j < g.degree[i]; j++)
				new_color.arc_color[i][j] = arc_color[i][j];
		}
		return (new_color);
	}
}
