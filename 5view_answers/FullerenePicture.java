/*  Creator: Wendy Myrvold
 Last Update: Oct. 2005

 This routine creates a fullerene picture which
 matches the current drawing options.

 */
import java.awt.Color;

public class FullerenePicture
{
	Fullerene f;
	DrawingOption draw_option;

	public FullerenePicture(Fullerene g)
	{
		f = g;
		/* Uncommented for debugging */
		f.p.print_graph("The primal graph:");
		f.faces.print_graph("The faces are:");
		f.d.print_graph("The dual graph:");

		/* Fill in current defaults. */

		draw_option = new DrawingOption(f);
	}

	/* SG: Begin change */
	public FullerenePicture(Fullerene g, DrawingOption d)
	{
		f = g;
		draw_option = d;
	}

	/* SG: End change */

	public void draw_fullerene()
	{
		GraphDrawing p1, d1;
		Coloring gcolor;
		FaceCenter dwd;
		Patches patch;
		int opt = 0;
		int i;

		/* Assign labels to the vertices. */

		Label.label(draw_option.label_option, f);

		/* Get coordinates for primal vertices. */

		dwd = new FaceCenter(f.p);
		dwd.redraw(draw_option.face_size, draw_option.face, f);

		/* Get a coloring for the primal. */

		/*
		 * System.out.println("Drawing color option: " + draw_option.color_option);
		 */
		if (draw_option.color_option == 2)
		{
			gcolor = new Coloring(f.p);
			patch = new Patches(f, gcolor);
			patch.change_color();
		}
		else if (draw_option.color_option == 3)
		{
			gcolor = new Coloring(f.p);
			FindGroup group = new FindGroup(f.p);
			group.change_color(gcolor);
		}
		else
		{
			gcolor = new Coloring(f.p, draw_option.color_option);
		}

		/* Get a graph drawing. */

		p1 = new GraphDrawing(dwd, gcolor);

		/* Include vertices and edges and add floaters as required. */

		if (draw_option.graph_component[1])
		{
			p1 = p1.add_floaters();
		}

		/* Make a new graph drawing with just the vertices. */

		else
		{
			p1.g = new AdjList(f.p.n, 0);
			for (i = 0; i < f.p.n; i++)
				p1.g.label[i] = f.p.label[i];
			p1.gcolor = new Coloring(f.p, draw_option.color_option);

			/* Yellow does not show up well without the edges. */

			if (draw_option.color_option != 1)
				for (i = 0; i < f.p.n; i++)
					p1.gcolor.vertex_color[i] = Color.red;
			p1.embed.set_radius(3);
		}

		/*
		 * Side note: We need coordinates of p1 to get the dual coordinates (even when primal is not
		 * shown on the screen).
		 */

		d1 = null;

		/* Add the dual vertices. */

		if (draw_option.graph_component[2] || draw_option.graph_component[3])
		{
			switch (draw_option.color_option)
			{
			case 0:
				opt = 2;
				break;
			case 1:
				opt = 1;
				break;
			case 2:
				opt = 3;
				break;
			case 3:
				opt = 3;
				break;
			default:
				opt = 2;
			}
			d1 = p1.dual_dots(f, opt);
			d1.embed.set_radius(3);
		}

		/* Add the dual edges. */

		if (draw_option.graph_component[3])
		{
			d1.g = f.d.copyof();
			d1.gcolor = new Coloring(d1.g, opt);

			d1 = d1.add_floaters();
			if (!draw_option.graph_component[1])
				d1.embed.set_radius(4);
		}
		if ((draw_option.graph_component[2] || draw_option.graph_component[3]) && draw_option.color_option == 3)
		{
			FindGroup dual_group = new FindGroup(f.d);
			dual_group.change_color(d1.gcolor);
		}

		if (draw_option.graph_component[0] || draw_option.graph_component[1])
		{
			FullerenePanel.new_picture(p1);
			if (d1 != null)
				FullerenePanel.add_picture(d1);
		}
		else
		{
			if (d1 != null)
				FullerenePanel.new_picture(d1);
		}
	}
}
