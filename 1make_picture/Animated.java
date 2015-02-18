import javax.swing.JComboBox;
import javax.swing.Timer;

public class Animated
{
	public final static int DEFAULT_DELAY = 500;
	public static final int NONE = -1;
	public static boolean is_paused;

	/* 1. Put the name of your new algorithm in the menu here. */

	// @formatter:off
	public static final String[] algorithms = {
		"Draw",
		"Left",
		"Right",
		"Up",
		"Down",
		"Zoom In",
		"Zoom Out",
		"Zoom",
		"Color Vert.",
		"Hamilton Cycles",
		"Vertex Spirals",
		"Perfect Matchings",
		"Delta-Wye-Delta"
		};
	// @formatter:on
	public int current_algorithm;
	static boolean animating;
	int delay;
	int n_times;

	/* 2. Declare an object whose type corresponds to your animation here. */

	Move move;
	Zoom zoom;
	ColorVertices colorVertices;
	Draw draw;
	HamiltonCycle ham;
	VertexSpiral vertexSpiral;
	AnimateMatchings matching;
	deltawye DYD;

	public Animated()
	{
		current_algorithm = 0;
		animating = false;
		is_paused = false;
		n_times = 0;
	}

	public static void make_menu(JComboBox algCombo)
	{
		int i;
		for (i = 0; i < algorithms.length; i++)
		{
			algCombo.addItem(algorithms[i]);
		}
	}

	public void change_algorithm(int new_algorithm)
	{
		if (animating)
			stop_algorithm();
		current_algorithm = new_algorithm;
		System.out.println("Current algorithm " + new_algorithm);
		start_algorithm();
	}

	public void start_algorithm()
	{
		is_paused = false;
		GraphDrawing gd = FullerenePanel.get_picture();
		System.out.println("START current algorithm " + current_algorithm);
		if (animating)
			stop_algorithm();
		n_times = 0;

		Fullerene f = CurrentFullerene.f;

		if (gd != null && f != null)
		{
			switch (current_algorithm)
			{
			/* Some algorithms require a real fullerene to start. */
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				FullerenePicture picture = new FullerenePicture(f, CurrentFullerene.picture.draw_option);
				picture.draw_fullerene();
				gd = FullerenePanel.get_picture();
			}

			/*
			 * 3. Call your constructor here and select your delay for the timer.
			 */

			delay = 300;
			switch (current_algorithm)
			{
			case 0:
				draw = new Draw(f);
				delay = 100;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				move = new Move();
				break;
			case 7:
				zoom = new Zoom();
				break;
			case 8:
				colorVertices = new ColorVertices(gd);
				break;
			case 9:
				delay = 2573;
				ham = new HamiltonCycle(gd, f);
				break;
			case 10:
				vertexSpiral = new VertexSpiral(gd);
				delay = 500;
				break;
			case 11:
				matching = new AnimateMatchings(gd);
				break;
			case 12:
				DYD = new deltawye(f, gd);
				delay = 600;
				break;
			default:
				;
				return;
			}
			animating = true;
			FUIGUI.timer = new Timer(delay, FUIGUI.listener);
			FUIGUI.timer.setInitialDelay(0);
			FUIGUI.timer.setCoalesce(true);
			FUIGUI.timer.start();
			next_step();
		}
	}

	public void next_step()
	{
		boolean ok;

		if (is_paused)
			return;

		/* Plug in your next step here. */
		System.out.println("Step " + n_times + " of " + current_algorithm);

		if (animating)
		{
			n_times++;
			switch (current_algorithm)
			{
			case 0:
				ok = draw.next_step();
				if (!ok)
					stop_algorithm();
				break;
			case 1:
				move.shift(-5, 0, 0);
				break;
			case 2:
				move.shift(5, 0, 0);
				break;
			case 3:
				move.shift(0, -5, 0);
				break;
			case 4:
				move.shift(0, 5, 0);
				break;
			case 5:
				move.shift(0, 0, -0.5);
				break;
			case 6:
				move.shift(0, 0, 0.5);
				break;
			case 7:
				zoom.next_step();
				break;
			case 8:
				colorVertices.next_step();
				break;
			case 9:
				while (!ham.next_step())
					;
				if (ham.done)
					stop_algorithm();
				break;
			case 10:
				vertexSpiral.next_step();
				break;
			case 11:
				if (!matching.next_step())
					stop_algorithm();
				break;
			case 12:
				if (!DYD.isdone())
					DYD.next_step();
				else
					stop_algorithm();
				break;
			default:
				;
			}
		}
	}

	public void stop_algorithm()
	{
		is_paused = false;
		System.out.println("STOP current algorithm " + current_algorithm);
		if (animating)
		{
			FUIGUI.timer.stop();
			animating = false;

			/* Get rid of your object here. */

			switch (current_algorithm)
			{
			case 0:
				draw = null;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				move = null;
				break;
			case 7:
				zoom = null;
				break;
			case 8:
				colorVertices = null;
				break;
			case 9:
				ham = null;
				break;
			case 10:
				vertexSpiral = null;
				break;
			case 11:
				matching.finishMatchingSolver();
				break;
			case 12:
				DYD.stop();
				DYD = null;
				break;
			default:
				;
			}
		}
	}

	/*
	 * Take the appropriate actions in response to a FUIGUI interrupt.
	 */
	public void handle_interrupt(Object source)
	{

		/*
		 * Pressing any button besides the pause button stops an animation.
		 */
		if (animating && source != FUIGUI.timer && source != FUIGUI.pauseButton)
		{
			stop_algorithm();
		}
		else if (source == FUIGUI.startButton)
		{
			start_algorithm();
		}

		if (source == FUIGUI.pauseButton)
		{
			if (animating)
				is_paused = !is_paused;
			else
				start_algorithm();
		}
		else if (source == FUIGUI.timer)
		{
			next_step();
		}
		else if (source == FUIGUI.algCombo)
		{
			change_algorithm(FUIGUI.algCombo.getSelectedIndex());
		}

		if (animating)
		{
			FUIGUI.startButton.setText("Stop");
			if (is_paused)
				FUIGUI.pauseButton.setText("Resume");
			else
				FUIGUI.pauseButton.setText("Pause");
		}
		else
		{
			FUIGUI.startButton.setText("Start");
			FUIGUI.pauseButton.setText("Begin");
		}
	}
}
