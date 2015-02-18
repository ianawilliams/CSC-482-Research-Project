/**
 * AnimateMatchings.java
 * @author Bette Bultena
 * @version 1.0.0
 */

import java.awt.Color;
import java.awt.Point;

/**
 * AnimateMatchings uses a backtracking thread to run through the process of finding all the perfect
 * matchings for a given Graphic Fullerene. It colours the matching edges blue, the non-matching
 * edges red, and the undecided edges white. When a match is found, the fullerene flashes white, and
 * the thread pauses for a while. A list of edges is sent to Standard out. The animation relies on
 * an other thread that pauses the "steps" which demonstrates how the algorithm makes its decisions.
 */

public class AnimateMatchings implements Runnable
{
	private Point[] edges; // all the edges
	private int[] edgeColour; // current colour: blue, red, or white.
	int numEdges, numVertices;
	GraphDrawing gd;
	Coloring flashColours; // all white
	Coloring theseColours; // current colours that will be displayed
	private Thread solverThread = null;
	private boolean threadSuspended = false;
	private boolean threadStop = false;
	private static final int sleepTime = 2000; // time to pause when a solution is found
	private static final int flashTime = 50; // A beacon to the user to flag a solution

	/**
	 * Initializes the edge colours to white, sets up the animation thread, and starts it. It is
	 * assumed that the fullerene is a primal graph representation. At this time, there is no
	 * consideration for the dual graph. However, with some minor modifications, this can be done in
	 * a future version.
	 * 
	 * @gd The physical drawing of the current fullerene.
	 */
	public AnimateMatchings(GraphDrawing gd)
	{
		this.gd = gd;
		numVertices = gd.g.n;
		numEdges = numVertices * 3 / 2;
		// If a fullerene wasn't 3-regular, we would do this using the degree list.
		edges = new Point[numEdges];
		edgeColour = new int[numEdges];
		for (int i = 0; i < numEdges; i++)
		{
			edgeColour[i] = 0;
		}
		int[][] adjacencies = gd.g.Adj;
		// Paint all the edges white on the fullerene picture.
		theseColours = gd.gcolor;
		// edges are identified as arcs from lower index vertex to higher index vertex.
		int edgeIndex = 0;
		for (int i = 0; i < numVertices; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				if (i < adjacencies[i][j])
				{
					edges[edgeIndex++] = new Point(i, adjacencies[i][j]);
					theseColours.color_edge(i, adjacencies[i][j], Color.white);
				}
			}
		}
		flashColours = theseColours.copyof(); // good to notify the user that a matching was found.
		FUIGUI.fullereneArea.new_picture(gd);
		solverThread = new Thread(this);
		solverThread.setPriority(4);
		solverThread.start(); // this automatically calls the run method
	}

	/**
	 * The required method for the thread to start.
	 */
	public void run()
	{
		if (!threadStop)
		{
			match(0, 0);
		}
		finish();
	}

	/**
	 * Stops the thread, so it can clean up.
	 */
	public void finishMatchingSolver()
	{
		threadStop = true;
		threadSuspended = false;
		synchronized (this)
		{
			notify();
		}
		finish();
	}

	/**
	 * Tells the suspended thread to continue solving until the next step in the algorithm.
	 */
	public boolean next_step()
	{
		if (!threadStop)
		{
			threadSuspended = false;
			synchronized (this)
			{
				notify();
			}
			return true;
		}
		else
		{
			return false; // solver has stopped.
		}
	}

	/*
	 * Destroys the solver thread..
	 */
	private void finish()
	{
		solverThread = null;
	}

	/*
	 * Suspends the solver thread to wait for the next step that releases the lock on this object.
	 */
	private void pause()
	{
		try
		{
			threadSuspended = true;
			synchronized (this)
			{
				while (threadSuspended)
				{
					wait();
				}
			}
		}
		catch (InterruptedException e)
		{
			System.out.println("Matching solver thread interrupted from pausing");
		}
	}

	/*
	 * Runs through the possibility of painting all current white edges adjacent to a vertex blue.
	 * For each blue decision, the neighbour vertex edges are painted red. Gets the first white edge
	 * in the edges list and recurses on the lower indexed vertex. Returns when there are no more
	 * white edges. If the max number of blue edges (numVertices/2) has been attained, this is a
	 * successful matching and the program pauses for "sleepTime" before backtracking to find the
	 * next solution. The recursion stops when all combinations have been tried, or the thread has
	 * been commanded to stop. param currVertex: The vertex with at least one white edge to try.
	 * param blueEdges: The current number of blue edges so far.
	 */
	private void match(int currVertex, int blueEdges)
	{
		if (threadStop)
		{
			System.out.println("The thread has been told to unwind");
			return; // Pop the recursion stack but leave edges painted their current colours.
		}
		int[] currEdges;
		int[] leadingEdges;
		int nextWhiteEdge;
		// All the white arcs leading from currVertex will be tried.
		leadingEdges = returnWhiteArcsFrom(currVertex);
		// If there is one edge, it might be the last one.
		if (leadingEdges.length == 1 && blueEdges + 1 == numVertices / 2)
		{
			/*
			 * Found a solution.
			 */
			edgeColour[leadingEdges[0]] = 1;
			theseColours.color_edge(edges[leadingEdges[0]].x, edges[leadingEdges[0]].y, Color.blue);
			FUIGUI.fullereneArea.new_picture(gd);
			// flash to let the user know that this is a matching....
			gd.gcolor = flashColours;
			FUIGUI.fullereneArea.new_picture(gd);
			try
			{
				Thread.sleep(flashTime);
			}
			catch (InterruptedException e)
			{
				System.out.println("Matching solver thread interruped in sleep.");
			}
			gd.gcolor = theseColours;
			FUIGUI.fullereneArea.new_picture(gd);
			try
			{
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e)
			{
				System.out.println("Matching solver thread interrupted in sleep");
			}
			System.out.println(this);
			// STEP:
			pause();
			// backtrack and continue if the user hasn't stopped the animation.
			if (!threadStop)
			{
				edgeColour[leadingEdges[0]] = 0;
				theseColours.color_edge(edges[leadingEdges[0]].x, edges[leadingEdges[0]].y, Color.white);
				return;
			}
		}
		if (leadingEdges.length == 0)
		{
			return;
		}
		// carry on......
		// paint everyone but the first edge red.
		for (int i = 1; i < leadingEdges.length; i++)
		{
			edgeColour[leadingEdges[i]] = -1;
			theseColours.color_edge(edges[leadingEdges[i]].x, edges[leadingEdges[i]].y, Color.red);
		}
		// pick one edge at a time to paint blue
		for (int branch = 0; branch < leadingEdges.length; branch++)
		{
			edgeColour[leadingEdges[branch]] = 1;
			theseColours.color_edge(edges[leadingEdges[branch]].x, edges[leadingEdges[branch]].y, Color.blue);
			FUIGUI.fullereneArea.new_picture(gd);
			// STEP:
			pause();
			// get the edges that must be painted red as a result of the blue one.
			currEdges = returnWhiteIncidentEdges(edges[leadingEdges[branch]].y);
			for (int i = 0; i < currEdges.length; i++)
			{
				edgeColour[currEdges[i]] = -1;
				theseColours.color_edge(edges[currEdges[i]].x, edges[currEdges[i]].y, Color.red);
			}
			FUIGUI.fullereneArea.new_picture(gd);
			// STEP:
			pause();
			nextWhiteEdge = findNextWhiteEdge();
			if (nextWhiteEdge == -1)
			{
				// we're done but we don't have enough blue edges.
				if (!threadStop)
				{
					edgeColour[leadingEdges[branch]] = -1;
					theseColours.color_edge(edges[leadingEdges[branch]].x, edges[leadingEdges[branch]].y, Color.red);
					for (int i = 0; i < currEdges.length; i++)
					{
						edgeColour[currEdges[i]] = 0;
						theseColours.color_edge(edges[currEdges[i]].x, edges[currEdges[i]].y, Color.white);
					}
					FUIGUI.fullereneArea.new_picture(gd);
					// STEP:
					pause();
					// backtracking
					continue;
				}
				else
				{
					break; // user call to stop.
				}
			}
			// recurse on the next white edge
			match(edges[nextWhiteEdge].x, blueEdges + 1);
			// out of the recursion....
			// backtrack on this blue edge
			if (!threadStop)
			{
				for (int i = 0; i < currEdges.length; i++)
				{
					edgeColour[currEdges[i]] = 0;
					theseColours.color_edge(edges[currEdges[i]].x, edges[currEdges[i]].y, Color.white);
				}
				edgeColour[leadingEdges[branch]] = -1;
				theseColours.color_edge(edges[leadingEdges[branch]].x, edges[leadingEdges[branch]].y, Color.red);
				FUIGUI.fullereneArea.new_picture(gd);
				// STEP:
				pause();
			}
		} // end of forloop through the one leading vertex.

		// Make all the leading edges white again, and recurse
		if (!threadStop)
		{
			for (int i = 0; i < leadingEdges.length; i++)
			{
				edgeColour[leadingEdges[i]] = 0;
				theseColours.color_edge(edges[leadingEdges[i]].x, edges[leadingEdges[i]].y, Color.white);
			}
		}
	}

	/**
	 * Returns the indices of the edges are that are incident to the given vertex.
	 * 
	 * @param v The vertex
	 * @return a compact int array.
	 */
	private int[] returnWhiteIncidentEdges(int v)
	{
		int num = 0;
		int maxEdges = 3; // there are a max of 4 incident edges to a fullerene edge.
		int[] tmpCollect = new int[maxEdges];
		for (int i = 0; i < numEdges; i++)
		{
			if (edgeColour[i] == 0)
			{
				if (edges[i].x == v || edges[i].y == v)
				{
					tmpCollect[num++] = i;
				}
			}
		}
		int[] collect = new int[num];
		for (int i = 0; i < num; i++)
		{
			collect[i] = tmpCollect[i];
		}
		return collect;
	}

	/**
	 * Returns the indices of the arcs that originate from the given vertex.
	 * 
	 * @param v The vertex
	 * @return a compact array.
	 */
	private int[] returnWhiteArcsFrom(int v)
	{
		int num = 0;
		int maxEdges = 3;
		int[] tmpCollect = new int[maxEdges];
		for (int i = 0; i < numEdges; i++)
		{
			if (edgeColour[i] == 0)
			{
				if (edges[i].x == v)
				{
					tmpCollect[num++] = i;
				}
			}
		}
		int[] collect = new int[num];
		for (int i = 0; i < num; i++)
		{
			collect[i] = tmpCollect[i];
		}
		return collect;
	}

	/**
	 * Returns the index of the first white edge, or -1 if there are none.
	 */
	private int findNextWhiteEdge()
	{
		for (int i = 0; i < numEdges; i++)
		{
			if (edgeColour[i] == 0)
			{
				return i;
			}
		}
		return -1; // no white edge left.
	}

	/**
	 * Returns a string representation of all blue edges. Handy for listing the edges contained in a
	 * perfect matching.
	 * 
	 * @return A string representation of all blue edges.
	 */
	public String toString()
	{
		String s = "Perfect Matching:\n";
		for (int i = 0; i < edges.length; i++)
		{
			if (edgeColour[i] == 1)
			{
				s += edges[i].x + "-" + edges[i].y + " ";
			}
		}
		return s;
	}
}
