/**
 * SpecifiedFaceCenter contains all the logic for the point and click interface
 * 
 * @author sgirn
 */
public class SpecifiedCenter
{
	/**
	 * Determines what pictureType it is to redraw
	 * 
	 * @param pictureType The pictureType to redraw
	 * @param xClick The x coordinate that was clicked
	 * @param yClick The y coordinate that was clicked
	 */
	public static void centerPictureOnClick(int pictureType, int xClick, int yClick)
	{
		/*
		 * WJM: Use the same variable for all 3 cases- it both uses less space and simplifies the
		 * code.
		 */

		int[] face;

		switch (pictureType)
		{
		//Specified Face Center
		case 0:
			face = findClosestFace(xClick, yClick);
			break;

		//Specified Vertex Center
		case 1:
			/*
			 * WJM Change this to return an array so that it is completely consistent with the other
			 * two options.
			 */
			face = findClosestVertex(xClick, yClick);
			break;

		//Specified Edge Center
		case 2:
			// Calculate which center-edge dot 
			// was the closest to the click
			// redraw according to that edge
			face = findClosestEdge(xClick, yClick);
			break;
		default:
			face = null;
		}

		/* WJM: Extract common code from the switch. */

		if (face == null)
		{
			System.out.println("Nothing selected.\n");
			return; //no picture to center
		}
		printVertices(face);

		/*
		 * WJM: Update the face for the current fullerene so that if other changes are made, then
		 * the picture stays consistent. Before I did this operations such as changing the labels on
		 * the vertices did not continue with the same drawing since the selected vertex/edge/face
		 * was never stored anywhere.
		 */

		CurrentFullerene.picture.draw_option.face = face;
		CurrentFullerene.picture.draw_option.face_size = face.length;
		CurrentFullerene.picture.draw_fullerene();
		FUIGUI.fullereneArea.repaint();
	}

	/**
	 * Compares all vertices on the graph and returns the closest vertex to the click.
	 * 
	 * @param xClick The horizontal click position
	 * @param yClick The vertical click position
	 * @return the number of the closest vertex
	 */
	public static int[] findClosestVertex(int xClick, int yClick)
	{
		int[] smallestVertex = new int[1];

		//get all vertices in the graph
		GraphDrawing gd = FUIGUI.fullereneArea.get_picture();
		if (gd == null)
		{
			return null;
		}
		AdjList vertices = gd.g;

		Embedding e = gd.embed;

		int hDistance; //the horizontal distance from the click
		int vDistance; //the vertical distance from the click
		double totalDistance; //the combined distance
		double minDistance = Double.MAX_VALUE; //the smallest distance

		//traverse through each vertex and "measure" distance from click
		for (int i = 0; i < vertices.n; i++)
		{
			hDistance = Math.abs(e.x[i] - xClick);
			vDistance = Math.abs(e.y[i] - yClick);
			//use Pythagorean Theorem here
			totalDistance = Math.sqrt(hDistance * hDistance + vDistance * vDistance);

			if (totalDistance < minDistance)
			{
				smallestVertex[0] = i;
				minDistance = totalDistance;
			}
		}

		if (smallestVertex[0] >= CurrentFullerene.f.p.n)
		{
			//case where smallestVertex is a floater
			//we grab the label of the floater
			String label = vertices.label[smallestVertex[0]].trim();
			smallestVertex[0] = new Integer(label).intValue();
		}
		return smallestVertex;
	}

	/**
	 * Uses the dual graph and its embedding to find the clicked face.
	 * 
	 * @param xClick The horizontal click position
	 * @param yClick The vertical click position
	 * @return the vertices composing the face that was clicked
	 */
	public static int[] findClosestFace(int xClick, int yClick)
	{
		GraphDrawing[] bothPictures = FUIGUI.fullereneArea.get_all_picture();
		if (bothPictures.length == 0)
		{
			return null;
		}
//		GraphDrawing gd = bothPictures[0];
		GraphDrawing dualgd = bothPictures[1];

		AdjList vertices = dualgd.g;
//		Embedding e = gd.embed;
		Embedding dualEmbedding = dualgd.embed;

		int smallestVertex = 0;
		int hDistance; //the horizontal distance from the click
		int vDistance; //the vertical distance from the click
		double totalDistance; //the combined distance
		double minDistance = Double.MAX_VALUE; //the smallest distance

		//traverse through each vertex and "measure" distance from click
		for (int i = 0; i < vertices.n; i++)
		{
			hDistance = Math.abs(dualEmbedding.x[i] - xClick);
			vDistance = Math.abs(dualEmbedding.y[i] - yClick);
			//use Pythagorean Theorem here
			totalDistance = Math.sqrt(hDistance * hDistance + vDistance * vDistance);

			if (totalDistance < minDistance)
			{
				smallestVertex = i;
				minDistance = totalDistance;
			}
		}
		return (CurrentFullerene.f.faces.Adj[smallestVertex]);
	}

	/**
	 * Uses the primal edges to determine closest edge center to the click.
	 * 
	 * @param xClick The horizontal click position
	 * @param yClick The vertical click position
	 * @return the vertices composing the closest edge center that was clicked
	 */
	public static int[] findClosestEdge(int xClick, int yClick)
	{
		GraphDrawing[] bothPictures = FUIGUI.fullereneArea.get_all_picture();
		if (bothPictures.length == 0)
		{
			return null;
		}

		GraphDrawing gd = bothPictures[0];

		Embedding e = gd.embed;
		AdjList vertices = gd.g;

		int[][] edges = vertices.Adj;
		int vertex1 = 0;
		int vertex2 = 0;
		double hDistance; //the horizontal distance from the click
		double vDistance; //the vertical distance from the click
		double totalDistance; //the combined distance
		double minDistance = Double.MAX_VALUE; //the smallest distance
		double xmidpoint = 0;
		double ymidpoint = 0;

		//traverse through each vertex and "measure" distance from click
		for (int i = 0; i < edges.length; i++)
		{
			int[] neighbours = edges[i];
			for (int j = 0; j < neighbours.length; j++)
			{
				xmidpoint = ((double) (e.x[i] + e.x[neighbours[j]])) / 2.0;
				ymidpoint = ((double) (e.y[i] + e.y[neighbours[j]])) / 2.0;

				hDistance = xmidpoint - xClick;
				vDistance = ymidpoint - yClick;
				//use Pythagorean Theorem here
				totalDistance = Math.sqrt(hDistance * hDistance + vDistance * vDistance);

				if (totalDistance < minDistance)
				{
					vertex1 = i;
					vertex2 = neighbours[j];
					minDistance = totalDistance;
				}

			}
		}

		if (vertex1 >= CurrentFullerene.f.p.n)
		{
			//vertex1 was a floater so use the label instead
			vertex1 = new Integer(vertices.label[vertex1].trim()).intValue();
		}

		if (vertex2 >= CurrentFullerene.f.p.n)
		{
			//vertex2 was a floater so use the label instead
			vertex2 = new Integer(vertices.label[vertex2].trim()).intValue();
		}

		int[] edge = { vertex1, vertex2 };
		return edge;

	}

	/**
	 * Debugging method to print vertices that we're recentering
	 * 
	 * @param vertices
	 */
	private static void printVertices(int[] vertices)
	{
		System.out.print("Recentering: ");
		for (int i = 0; i < vertices.length; i++)
			System.out.print(vertices[i] + " ");
		System.out.println();
	}
}
