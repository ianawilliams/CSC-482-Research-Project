/*  Creator: Wendy Myrvold
 Last Update: Oct. 2005

 This routine keeps track of fuigui's defaults
 for drawing fullerenes.
 */
import javax.swing.JComboBox;

public class DrawingOption
{
	// @formatter:off
	final static String[] picture_options = {
		"Face  Center",
		"Vertex Center",
		"Edge Center ",
		"Primal ",
		"Dual ",
		"Primal+Dual ",
		"Primal+Dual Dots",
		"Dual+Primal Dots",
		"Primal Dots",
		"Dual Dots",
		"Default Colouring",
		"Black & White",
		"Pentagon Patches",
		"Site Group Colouring",
		"Vertex Numbers",
		"No Labels",
		"Num Spirals",
		"Site Groups",
		"Specified Face Center",
		"Specified Vertex Center",
		"Specified Edge Center"
		};
	// @formatter:on

	/* SG: Begin change */
	int click_center_option;
	static boolean click_option = false;
	/* SG: End change */

	static DrawingOption default_option = get_init_values();

	int center_option;
	boolean[] graph_component;
	int color_option;
	int label_option;

	Fullerene f;
	int face_size;
	int[] face;

	public DrawingOption()
	{
	}

	public static void update_default(int option)
	{
		//The default is to ignore mouse clicks.

		switch (option)
		{
		case 0:
		case 1:
		case 2:
			default_option.center_option = option;
			CurrentFullerene.picture.draw_option = new DrawingOption(CurrentFullerene.picture.draw_option.f);
			return;
		case 3:
			default_option.graph_component[0] = true;
			default_option.graph_component[1] = true;
			default_option.graph_component[2] = false;
			default_option.graph_component[3] = false;
			break;
		case 4:
			default_option.graph_component[0] = false;
			default_option.graph_component[1] = false;
			default_option.graph_component[2] = true;
			default_option.graph_component[3] = true;
			break;
		case 5:
			default_option.graph_component[0] = true;
			default_option.graph_component[1] = true;
			default_option.graph_component[2] = true;
			default_option.graph_component[3] = true;
			break;
		case 6:
			default_option.graph_component[0] = true;
			default_option.graph_component[1] = true;
			default_option.graph_component[2] = true;
			default_option.graph_component[3] = false;
			break;
		case 7:
			default_option.graph_component[0] = true;
			default_option.graph_component[1] = false;
			default_option.graph_component[2] = true;
			default_option.graph_component[3] = true;
			break;
		case 8:
			default_option.graph_component[0] = true;
			default_option.graph_component[1] = false;
			default_option.graph_component[2] = false;
			default_option.graph_component[3] = false;
			break;
		case 9:
			default_option.graph_component[0] = false;
			default_option.graph_component[1] = false;
			default_option.graph_component[2] = true;
			default_option.graph_component[3] = false;
			break;
		case 10:
		case 11:
		case 12:
		case 13:
			default_option.color_option = option - 10;
			break;
		case 14:
		case 15:
		case 16:
		case 17:
			default_option.label_option = option - 14;
			break;
		/* SG: Begin change */
		case 18:
			click_option = true;
			default_option.center_option = 0;
			default_option.click_center_option = 0;
			default_option.graph_component[0] = true;
			default_option.graph_component[1] = true;
			default_option.graph_component[2] = true;
			default_option.graph_component[3] = false;
			break;
		case 19:
			click_option = true;
			default_option.center_option = 1;
			default_option.click_center_option = 1;
			break;
		case 20:
			click_option = true;
			default_option.center_option = 2;
			default_option.click_center_option = 2;
			break;
		/* SG: End change */
		default:
		}
		/* Update the current fullerene. */

		CurrentFullerene.picture.draw_option.update();
	}

	public static void make_menu(JComboBox redrawCombo)
	{
		int i;
		for (i = 0; i < picture_options.length; i++)
		{
			redrawCombo.addItem(picture_options[i]);
		}
	}

	public static DrawingOption get_init_values()
	{
		DrawingOption init;
		init = new DrawingOption();
		init.center_option = 0;
		init.graph_component = new boolean[4];
		init.graph_component[0] = true;
		init.graph_component[1] = true;
		init.graph_component[2] = false;
		init.graph_component[3] = false;
		init.color_option = 0;
		init.label_option = 0;
		return (init);
	}

	public void update()
	{
		int i;

		center_option = default_option.center_option;
		graph_component = new boolean[4];
		for (i = 0; i < 4; i++)
		{
			graph_component[i] = default_option.graph_component[i];
		}
		color_option = default_option.color_option;
		label_option = default_option.label_option;
	}

	public DrawingOption(Fullerene g)
	{
		int i;

		f = g;
		
		if (g.hasPictureInfo())
		{
			face_size = g.getFaceSize();
			face = g.getFace();
		}
		else
		{
			switch (default_option.center_option)
			{
			case 1: /* vertex */
				face_size = 1;
				break;
			case 2: /* edge */
				face_size = 2;
				break;
			default: /* face */
				face_size = f.faces.degree[0];
			}
			face = new int[face_size];
			for (i = 0; i < face_size; i++)
			{
				face[i] = f.faces.Adj[0][i];
			}
		}
		update();
	}
}
