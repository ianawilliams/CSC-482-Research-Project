import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.StringTokenizer;

/* Assume the adjacency list defining a planar embedding of the graph is given */
public class deltawye
{

	private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	private static StringTokenizer reader;
	int n;
	int[][] G_adj;
	int[][] Aedge;
	int[][] Ledge;
	int[] Lvertex;
	Graph myGraph;
	boolean whatamidoing = false; //debug - tell me what is happening in the constructor.
	boolean debug = false; //debug - tell me what is happening with reductions.
	boolean updateAux = true; //maintain auxiliary edge info as the algorithm progresses.
	boolean printnops = true; //print number of operations
	boolean printAuxUpdate = false; //print when aux is updated
	boolean printAuxUpdateLength = false; //print number of digits of new aux info
	boolean scaleM = true; //scale the multiplier
	boolean scaleEdges = true; //scale the auxiliary edge info
	boolean verbose = false; //print verbose info about each graph
	int ngraphs = 0;
	int labelsum;
	long start, stop, elapsed;
	long tstart, tstop, telapsed;

	/*
	 * constructor for fuigui - take in a Fullerene and use it to animate the algorithm
	 */
	public deltawye(Fullerene f, GraphDrawing gd)
	{
		n = f.p.n;
		G_adj = new int[n][n + 1]; //Adjacency list of graph for eaah vertex, i, G[i][0] = degree(i)
		for (int i = 0; i < n; i++)
		{
			G_adj[i][0] = f.p.degree[i];
			for (int j = 1; j <= G_adj[i][0]; j++)
				G_adj[i][j] = f.p.Adj[i][j - 1];
		}

		//make new adjacency list for the graphdrawing object -> so that dyd reduction can be shown
		AdjList myg = f.p.copyof();
		gd.gcolor.g = myg;
		gd.embed.g = myg;
		gd.g = myg;

		//BFS labels
		Ledge = new int[n][n]; //edge labels - using special BFS
		Lvertex = new int[n]; //vertex labels - using special BFS
		for (int i = 0; i < n; i++)
			Lvertex[i] = -1;
		BFSLabel(G_adj, Ledge, Lvertex, n);

		//create "Graph"
		myGraph = new Graph(n, G_adj, Ledge, Lvertex, gd, myg);

		//count spanning trees
		start = System.currentTimeMillis();
		myGraph.initialize();
		//myGraph.reduce();
	}

	public void next_step()
	{
		myGraph.next_step();
	}

	public boolean isdone()
	{
		return myGraph.done;
	}

	public void stop()
	{
		System.out.println("Graph " + ngraphs);
		System.out.println("          Number of Vertices: " + n);
		if (isdone())
		{
			BigInteger nspanningtrees = myGraph.M[0];
			stop = System.currentTimeMillis();
			elapsed = stop - start;

			//print what happened
			System.out.println("    Number of Spanning Trees: " + nspanningtrees.toString());
			System.out.println("        Number of Operations: " + myGraph.getNops());
			System.out.println("                   Time (ms): " + elapsed);
		}
		else
		{
			System.out.println("    Number of Spanning Trees: NOT FINISHED!");
		}
	}

	/*
	 * don't need this constructor for fuigui public deltawye() { //Graph data tstart =
	 * System.currentTimeMillis(); n = readN(); while(n != 0) { ngraphs++; G_adj = new int[n][n+1];
	 * //Adjacency list of graph for eaah vertex, i, G[i][0] = degree(i)
	 * 
	 * //BFS labels Ledge = new int[n][n]; //edge labels - using special BFS Lvertex = new int[n];
	 * //vertex labels - using special BFS for(int i=0;i <n;i++) Lvertex[i] = -1;
	 * 
	 * if(whatamidoing) System.out.println("Reading graph "+ngraphs); readGraph(G_adj,n); //number
	 * of vertices in G if(whatamidoing) System.out.println("Done Reading graph "+ngraphs);
	 * if(whatamidoing) System.out.println("Labelling graph "+ngraphs);
	 * BFSLabel(G_adj,Ledge,Lvertex,n); if(whatamidoing)
	 * System.out.println("Done Labelling graph "+ngraphs);
	 * 
	 * if(whatamidoing) System.out.println("Creating graph "+ngraphs); myGraph = new
	 * Graph(n,G_adj,Ledge,Lvertex); if(whatamidoing)
	 * System.out.println("Done Creating graph "+ngraphs); //myGraph.print(); //myGraph.reduce();
	 * //myGraph.print(); //printGraphUT(G_adj,n); //printGraph(G,n); //printAux(Aedge,n);
	 * //printLabels(Ledge,Lvertex,n); //printGraphUT(G,n); if(whatamidoing)
	 * System.out.println("About to start reduction... "+ngraphs); start =
	 * System.currentTimeMillis(); BigInteger nspanningtrees = myGraph.getNumSpanningTrees(); stop =
	 * System.currentTimeMillis(); elapsed = stop - start; if(verbose) {
	 * System.out.println("Graph "+ngraphs); System.out.println(" Number of Vertices:
	 * "+n); System.out.println(" Number of Spanning Trees:
	 * "+nspanningtrees.toString()); System.out.println(" Number of Operations:
	 * "+myGraph.getNops()); } else { int nspaces,itemp,i; BigInteger BItemp; long ltemp;
	 * 
	 * if(ngraphs==1) System.out.println(" # Vertices # Spanning Trees # reduction operations time
	 * (ms)");
	 * 
	 * nspaces = 13; itemp = n; while(itemp>0) { itemp = itemp/10; nspaces--; } for(i=0;i
	 * <nspaces;i++) System.out.print(" "); System.out.print(n); System.out.print(" ");
	 * 
	 * BItemp = new BigInteger(nspanningtrees.toString()); nspaces = 19;
	 * while(BItemp.compareTo(BigInteger.ZERO)==1) { BItemp = BItemp.divide(new BigInteger("10"));
	 * nspaces--; } for(i=0;i <nspaces;i++) System.out.print(" ");
	 * System.out.print(nspanningtrees.toString()); System.out.print(" ");
	 * 
	 * itemp = myGraph.getNops(); nspaces = 25; while(itemp>0) { itemp = itemp/10; nspaces--; }
	 * for(i=0;i <nspaces;i++) System.out.print(" "); System.out.print(myGraph.getNops());
	 * 
	 * ltemp = elapsed; nspaces = 12; while(itemp>0) { ltemp = ltemp/10; nspaces--; } for(i=0;i
	 * <nspaces;i++) System.out.print(" "); System.out.print(elapsed);
	 * 
	 * System.out.println(); } n = readN(); } tstop = System.currentTimeMillis(); telapsed = tstop -
	 * tstart; System.out.println(ngraphs + " graphs processed in "+telapsed+" ms."); }
	 */

	//wrote this before the graph classes. Should be inside the Graph class.
	// But I don't have time to move it, it works fine.
	public void BFSLabel(int[][] G, int[][] Ledge, int[] Lvertex, int n)
	{
		int[] Q = new int[n];
		int lastinQ = 0;
		for (int i = 0; i < n; i++)
			Q[i] = -1;
		int currentv = 0;
		int first_with_label = 0;
		int current_label = 0;
		labelsum = 0;
		Lvertex[currentv] = 0;

		while (currentv != -1)
		{
			while (currentv != -1)
			{
				if (current_label < Lvertex[currentv] + 1)
				{
					current_label = Lvertex[currentv] + 1;
					first_with_label = currentv;
				}
				int degree = G[currentv][0];
				for (int i = 1; i <= degree; i++)
				{ //for each vertex adjacent to currentv
					int nextv = G[currentv][i];
					if (Ledge[currentv][nextv] == 0)
					{
						Ledge[currentv][nextv] = current_label;
						Ledge[nextv][currentv] = current_label;
						labelsum += current_label;
					}
					if (Lvertex[nextv] == -1)
					{
						Lvertex[nextv] = current_label + 1;
						labelsum += current_label + 1;
						Q[lastinQ] = nextv; //add nextv to Q
						lastinQ = nextv; //nextv is now the last in Q
					}
				}
				currentv = Q[currentv];
				if (currentv != -1 && Lvertex[currentv] >= current_label)
				{
					currentv = first_with_label;
					break;
				}
			}
			while (currentv != -1)
			{
				/*
				 * now all vertices adjacent to and edges incident to currentv are labeled So now
				 * need to walk the faces of all edges labeled with current_label to label all the
				 * unlabeled vertices with current_label+1.
				 */
				int degree = G[currentv][0];
				for (int i = 1; i <= degree; i++)
				{ // For each vertex u adjacent to currentv
					int p = currentv;
					int u = G[p][i];
					int v = -1;
					while (true)
					{
						int degree_u = G[u][0];
						for (int j = 1; j <= degree_u; j++)
						{ //Find the vertex v after p in u's adjacency list.
							if (G[u][j] == p)
							{
								if (j < degree_u)
									v = G[u][j + 1];
								else
									v = G[u][1];
							}
						}//end for
						if (v == currentv)
							break;
						/*
						 * Mark edge (u,v) with current_label+1 if it is not already labelled and
						 * mark vertex v with current_label+1 if it is not already labelled
						 */
						if (Ledge[u][v] == 0)
						{
							Ledge[u][v] = current_label + 1;
							Ledge[v][u] = current_label + 1;
							labelsum += current_label + 1;
							//System.out.println("Labelling edge (" + u + "," +
							// v + ") with " + (current_label+1) + "(current
							// label is " + current_label + ")");
						}
						if (Lvertex[v] == -1)
						{
							Lvertex[v] = current_label + 1;
							labelsum += current_label + 1;
							//System.out.println("Labelling vertex " + v + "
							// with " + (current_label+1));
							//If v gets labelled then it needs to also be added to Q
							Q[lastinQ] = v; //add nextv to Q
							lastinQ = v; //nextv is now the last in Q
						}
						p = u;
						u = v;
					}//end while(1)
				}
				currentv = Q[currentv];
				if (currentv != -1 && Lvertex[currentv] >= current_label)
				{
					break;
				}
			}
		}
	}

	//Read functions/Print functions
	public static int readN()
	{
		return readInt();
	}

	public static void readGraph(int[][] G, int n)
	{
		for (int i = 0; i < n; i++)
		{
			G[i][0] = readInt();
			for (int j = 1; j <= G[i][0]; j++)
			{
				G[i][j] = readInt();
			}
		}
	}

	public static void printGraph(int[][] G, int n)
	{
		System.out.println(n);
		for (int i = 0; i < n; i++)
		{
			System.out.print(i + ": ");
			for (int j = 1; j <= G[i][0]; j++)
			{
				System.out.print(G[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static void printAux(int[][] Aedge, int n)
	{
		System.out.println("aux:");
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				System.out.print(Aedge[i][j]);
			}
			System.out.println();
		}
	}

	public static void printLabels(int[][] Ledge, int[] Lvertex, int n)
	{
		System.out.println("Edge Labels:");
		for (int i = 0; i < n; i++)
		{
			System.out.print(i + "(" + Lvertex[i] + ")" + ": ");
			for (int j = 0; j < n; j++)
			{
				if (Ledge[i][j] > 0)
					System.out.print(j + "(" + Ledge[i][j] + ")" + " ");
			}
			System.out.println();
		}
	}

	public static void printGraphUT(int[][] G, int n)
	{
		int[][] G_UT = new int[n][n];
		for (int i = 0; i < n; i++)
		{
			for (int j = 1; j <= G[i][0]; j++)
			{
				G_UT[i][G[i][j]] = 1;
			}
		}
		System.out.print(n + " ");
		for (int i = 0; i < n; i++)
		{
			for (int j = i + 1; j < n; j++)
			{
				System.out.print(G_UT[i][j]);
			}
		}
		System.out.println();
	}

	public static int readInt()
	{
		String token = getNextInputToken(true);
		int value;
		try
		{
			value = Integer.parseInt(token);
		}
		catch (Exception exception)
		{
			value = 0;
			//System.out.println ("Error reading int data, MIN_VALUE value returned.");
			//System.exit(0);
		}
		return value;
	}

	private static String getNextInputToken(boolean skip)
	{
		final String delimiters = " \t\n\r\f";
		String token = null;
		try
		{
			if (reader == null)
				reader = new StringTokenizer(in.readLine(), delimiters, true);
			while (token == null || ((delimiters.indexOf(token) >= 0) && skip))
			{
				while (!reader.hasMoreTokens())
					reader = new StringTokenizer(in.readLine(), delimiters, true);
				token = reader.nextToken();
			}
		}
		catch (Exception exception)
		{
			//System.out.println("Error getting next token.");
			//System.exit(0);
			//token = null;
		}
		return token;
	}

	private class Graph
	{
		int nvertices;

		Vertex[] vertices;
		int nfaces;
		Face[] faces;
		VertexList leaf = new VertexList();
		VertexList series = new VertexList();
		VertexList wye = new VertexList();
		FaceList loop = new FaceList();
		FaceList parallel = new FaceList();
		FaceList delta = new FaceList();
		BigInteger[] M;
		int nops = 0;
		public boolean done = false;
		GraphDrawing mygd;
		AdjList myg;
		int stage;
		Vertex workingv;
		Face workingf;
		String workingop = "";
		Vertex[] workingverts;
		Arc[] workingarcs;

		//Input: int[][] G with G[i][0] containing the degree of vertex i
		//and G[i][j] containing the j'th adjacent vertex to i in a planar embedding
		//of G for j = 1..degree[i] Ledge[][] contains the label for each edge
		//according to the special BFS labeling.
		public Graph(int n, int[][] G, int[][] Ledge, int[] Lvertex, GraphDrawing gd, AdjList g)
		{
			mygd = gd;
			myg = g;
			stage = 1;
			int u, v, degree_u;
			boolean[] created = new boolean[n];
			Arc[][] arcs = new Arc[n][n];
			nvertices = 0;
			vertices = new Vertex[labelsum];
			nfaces = 0;
			faces = new Face[labelsum];
			M = new BigInteger[2];
			M[0] = new BigInteger("1");
			M[1] = new BigInteger("1");
			for (int i = 0; i < n; i++)
			{
				u = i;
				degree_u = G[u][0];
				if (!created[u])
				{
					//create vertex u
					created[u] = true;
					vertices[u] = new Vertex(u, degree_u, Lvertex[u]);
					nvertices++;
				}
				for (int j = 1; j <= degree_u; j++)
				{
					v = G[i][j];
					if (!created[v])
					{
						//create vertex v
						created[v] = true;
						vertices[v] = new Vertex(v, G[v][0], Lvertex[v]);
						nvertices++;
					}
					Arc uv = new Arc(vertices[u], vertices[v], Ledge[u][v]); //create arc (u,v)
					arcs[u][v] = uv;
					vertices[u].addArc(uv); //add arc(u,v) to u's arc list
				}
			}
			for (int i = 0; i < n; i++)
			{
				u = i;
				degree_u = G[i][0];
				for (int j = 1; j <= degree_u; j++)
				{
					v = G[i][j];
					arcs[u][v].setRev(arcs[v][u]);
					//make revArc of arc (u,v) = arc (v,u)
				}
			}
			//print();
			getFaces();
		}

		private void getFaces()
		{
			Vertex u;
			Arc orig_uv_vertex, orig_uv_face, uv;
			Face f;
			for (int i = 0; i < n; i++)
			{ //for each vertex
				u = vertices[i];
				orig_uv_vertex = u.getFirstArc();
				uv = orig_uv_vertex;
				while (true)
				{
					//walk face starting with arc uv if it has not been walked already
					if (!uv.isFaceWalked())
					{
						f = new Face(nfaces);
						faces[nfaces] = f;
						f.setFirstArc(uv);
						nfaces++;
						orig_uv_face = uv;
						while (true)
						{
							if (uv.isFaceWalked())
							{
								System.out.println("Error: Edge in face has already been traversed!");
								System.exit(0);
							}
							f.addArc(uv);
							uv = uv.getRev().getNext(); //get next edge on face
							if (uv == orig_uv_face)
								break; //if we are back to the beginning then stop.
						}
					}
					uv = uv.getNext(); //get next vertex adjacent to u
					if (uv == orig_uv_vertex)
						break;
				}
			}
		}

		public void print()
		{
			System.out.println("n = " + nvertices);
			for (int i = 0; i < nvertices; i++)
			{
				vertices[i].print();
			}
			for (int i = 0; i < nfaces; i++)
			{
				faces[i].print();
			}
			System.out.println("M=" + M[0].toString() + "/" + M[1].toString());
		}

		//probably useless...
		/*
		 * private AdjList make_g() { AdjList newg = new AdjList(nvertices); for(int i=0;i
		 * <nvertices;i++) { add_vertex(i,vertices[i].getDegree()); Arc[] arcs =
		 * vertices[i].getArcs(); for(int j=0;j <arcs.length;j++) {
		 * add_arc(vertices[i].getNumber(),arcs[j].getEnd().getNumber(),j); } } }
		 */

		public BigInteger getNumSpanningTrees()
		{
			initialize();
			reduce();
			return M[0];
		}

		public int getNops()
		{
			return nops;
		}

		public void initialize()
		{
			done = false;
			stage = 1;
			//first set up the initial configuration of the graphs.
			Vertex v;
			Face f;
			for (int i = 0; i < nvertices; i++)
			{
				v = vertices[i];
				switch (v.getDegree())
				{
				case 1:
					leaf.addVertex(v);
					break;
				case 2:
					series.addVertex(v);
					break;
				case 3:
					wye.addVertex(v);
					break;
				}
			}
			for (int i = 0; i < nfaces; i++)
			{
				f = faces[i];
				switch (f.getDegree())
				{
				case 1:
					loop.addFace(f);
					break;
				case 2:
					parallel.addFace(f);
					break;
				case 3:
					delta.addFace(f);
					break;
				}
			}
		}

		public void reduce()
		{
			while (leaf.getSize() > 0 || loop.getSize() > 0 || parallel.getSize() > 0 || series.getSize() > 0 || delta.getSize() > 0
					|| wye.getSize() > 0)
			{
				next_step();
			}
		}

		private void next_step()
		{
			//Order of reduction:
			//1. leaf
			//2. loop
			//3. parallel
			//4. series
			//5. deltawye
			//6. wyedelta
			//Important that 3 is before 4 and that 1-4 are before 5,6
			//System.out.println("DYD stage "+stage);
			switch (stage)
			{
			case 1:
				if (leaf.getSize() > 0 || loop.getSize() > 0 || parallel.getSize() > 0 || series.getSize() > 0 || delta.getSize() > 0
						|| wye.getSize() > 0)
				{
					if (leaf.getSize() > 0)
					{
						workingop = "leaf";
						workingv = leaf.getFirst();
						workingverts = hilightvertices(workingv);
						workingarcs = hilightarcs(workingv);
					}
					else if (parallel.getSize() > 0)
					{
						workingop = "parallel";
						workingf = parallel.getFirst();
						workingverts = hilightvertices(workingf);
						workingarcs = hilightarcs(workingf);
					}
					else if (series.getSize() > 0)
					{
						workingop = "series";
						workingv = series.getFirst();
						workingverts = hilightvertices(workingv);
						workingarcs = hilightarcs(workingv);
					}
					else if (delta.getSize() > 0)
					{
						workingop = "delta";
						workingf = delta.getFirst();
						workingverts = hilightvertices(workingf);
						workingarcs = hilightarcs(workingf);
					}
					else if (wye.getSize() > 0)
					{
						workingop = "wye";
						workingv = wye.getFirst();
						workingverts = hilightvertices(workingv);
						workingarcs = hilightarcs(workingv);
					}
					if (nops % 10 == 0 && printnops)
						System.out.println(nops + " reduction operations performed.");
				}
				else
				{
					done = true;
				}
				stage = 2;
				break;
			case 2:
				if (workingop == "leaf")
				{
					Rleaf(workingv); //LEAF REMOVAL
				}
				else if (workingop == "parallel")
				{
					Rparallel(workingf); //PARALLEL REDUCTION
				}
				else if (workingop == "series")
				{
					Rseries(workingv); //SERIES REDUCTION
				}
				else if (workingop == "delta")
				{
					Rdeltawye(workingf); //DELTA WYE REDUCTION
				}
				else if (workingop == "wye")
				{
					Rwyedelta(workingv); //WYE DELTA REDUCTION
				}
				stage = 3;
				break;
			case 3:
				unhilight(workingverts);
				unhilight(workingarcs);
				stage = 1;
				break;
			}

		}

		/*
		 * //returns true if the reduction is actually performed, false if it is not performed
		 * because of invalid labeling. private boolean Rloop(Face f) { //loop removal should never
		 * happen because parallel reduction always happens before series reduction.
		 * System.out.println("Error, loop removal is happening, not tested!!!"); System.exit(0);
		 * Arc uu = f.getFirstArc(); Arc uuR = uu.getRev(); Vertex u = uu.getStart(); if(debug) {
		 * System.out.println(); System.out.println("Loop Removeal: v="+u.getNumber()); }
		 * uu.removeEdge(); switchlists(f,0,1); switchlists(u,u.getDegree(),u.getDegree()+2);
		 * nops++; return true; }
		 */

		//returns true if the reduction is actually performed, false if it is
		// not performed because of invalid labeling.
		private boolean Rleaf(Vertex v)
		{
			Arc[] arcs = v.getArcs();
			Vertex u;
			Arc uv, vu;
			Face F;
			if (debug)
			{
				System.out.println();
				System.out.println("Leaf Removal: v=" + v.getNumber());
			}
			if (v.getDegree() != 1 || arcs.length != 1)
			{
				System.out.println("Problem in Rleaf - v has degree " + v.getDegree());
				System.exit(0);
			}
			vu = arcs[0];
			uv = vu.getRev();
			u = uv.getStart();

			//get the face and adjust the first arc
			F = vu.getFace();
			// F.adjustFirstArc(uv,vu);

			//remove the leaf
			uv.removeEdge();

			//Change edges of gd object
			//firstfirst uncolour the old edges
			unhilight(workingarcs);
			workingarcs = new Arc[0]; //get rid of all working arcs

			//now remove edge uv
			myg.remove_edge(u.getNumber(), v.getNumber());

			//remove vertex v
			myg.degree[v.getNumber()] = -1;

			//update multiplier according to: M = m*a
			if (updateAux)
			{
				BigInteger[] a = uv.getAux();
				M[0] = M[0].multiply(a[0]);
				M[1] = M[1].multiply(a[1]);
				if (scaleM)
					scale(M);
				if (printAuxUpdate)
					System.out.println("leaf: M = " + M[0].toString() + " / " + M[1].toString());
				if (printAuxUpdateLength)
					System.out.println("leaf: M has " + M[0].toString().length() + " / " + M[1].toString().length() + " digits.");
			}

			//update the lists
			switchlists(F, F.getDegree(), F.getDegree() + 2);
			switchlists(v, 0, 1);
			switchlists(u, u.getDegree(), u.getDegree() + 1);
			nops++;

			return true;
		}

		//returns true if the reduction is actually performed, false if it is
		// not performed because of invalid labeling.
		private boolean Rparallel(Face f)
		{
			if (debug)
			{
				System.out.println();
				System.out.println("Starting Rparallel, face " + f.getNumber());
			}
			Arc[] arcs = f.getArcs();
			Arc uv1, vu1, uv2, vu2, uvN, vuN;
			Face F1, F2;
			Vertex u, v;
			int l;

			if (f.getDegree() != 2 || arcs.length != 2)
			{
				System.out.println("    Problem in Rparallel - face " + f.getNumber() + " has degree " + f.getDegree() + " and has "
						+ arcs.length + " edges.");
				System.exit(0);
			}
			uv1 = arcs[0];
			vu1 = uv1.getRev();
			vu2 = arcs[1];
			uv2 = vu2.getRev();
			u = uv1.getStart();
			v = uv1.getEnd();

			if (debug)
			{
				System.out.println("    Parallel Reduction: u=" + u.getNumber() + ", v=" + v.getNumber());
			}
			if (uv1.getLabel() == uv2.getLabel())
			{
				l = uv1.getLabel();
			}
			else if (uv1.getLabel() == uv2.getLabel() - 1)
			{
				l = uv1.getLabel();
			}
			else if (uv1.getLabel() - 1 == uv2.getLabel())
			{
				l = uv2.getLabel();
			}
			else
			{
				parallel.removeFace(f);
				return false; //incorrect labels (according to Feo & Provan paper)
			}

			//create new arc;
			if (debug)
				System.out.println("    Creating new arc uv with label " + l);
			uvN = new Arc(u, v, l);
			vuN = new Arc(v, u, l);
			uvN.setRev(vuN);
			vuN.setRev(uvN);

			//get faces and deal with their firstarcs.
			F1 = vu1.getFace();
			F2 = uv2.getFace();
			//adjust the first arcs of the faces so they do not include arcs
			// that will be removed
			//			System.out.println(" Adjusting first arcs");
			//			F1.adjustFirstArc(uv2,vu1);
			//			F2.adjustFirstArc(uv2,vu1);
			//Don't have to worry about F1 and F2 being the same face -
			// firstArc will now be moved so it is not either uv1 or uv2.
			//unless we are down to 2 vertices with these two edges between
			// them...still have to check (later)

			//Add new arc to vertices u and w
			if (debug)
				System.out.println("    Adding new arcs to u and v (uvN,vuN)");
			u.insertAfter(uvN, uv2);
			v.insertAfter(vuN, vu1);

			//remove parallel edges from the graph
			if (debug)
				System.out.println("    Removing edges uv1 and uv2 from the graph");
			uv1.removeEdge();
			uv2.removeEdge();

			//Add the new arcs to F1 and F2
			if (debug)
				System.out.println("    adding new arcs to F1 and F2");
			F1.addArc(vuN);
			F2.addArc(uvN);

			if (F1 == F2)
				F1.setFirstArc(vuN);

			//Change edges of gd object
			//just need to remove edge uv
			myg.remove_edge(u.getNumber(), v.getNumber());

			//now color the new edges
			mygd.gcolor = new Coloring(myg);
			hilight(workingarcs);

			//update auxilary information of the new edges according to:
			// c = a + b
			if (updateAux)
			{
				BigInteger[] a = uv2.getAux();
				BigInteger[] b = uv1.getAux();
				BigInteger Func1 = a[0].multiply(b[1]).add(a[1].multiply(b[0]));
				BigInteger Func2 = a[1].multiply(b[1]);
				BigInteger[] c = new BigInteger[2];
				c[0] = Func1;
				c[1] = Func2;
				if (printAuxUpdate)
					System.out.println("parallel: c = " + c[0].toString() + " / " + c[1].toString());
				if (printAuxUpdateLength)
					System.out.println("parallel: c has " + c[0].toString().length() + " / " + c[1].toString().length() + " digits.");
				uvN.setEdgeAux(c);

				//update the multiplier according to:
				// M = M (no change)
			}

			//update the lists
			if (debug)
			{
				System.out.println("    Updating the lists");
			}
			switchlists(u, u.getDegree(), u.getDegree() + 1);
			switchlists(v, v.getDegree(), v.getDegree() + 1);
			switchlists(f, 0, 2);

			nops++;

			return true;
		}

		//returns true if the reduction is actually performed, false if it is
		// not performed because of invalid labeling.
		private boolean Rseries(Vertex v)
		{
			if (debug)
			{
				System.out.println();
				System.out.println("Starting Rseries, v=" + v.getNumber());
			}
			Arc[] arcs = v.getArcs();
			Arc vu, uv, vw, wv, uw, wu;
			Face F1, F2;
			Vertex u, w;
			int l;
			if (v.getDegree() != 2 || arcs.length != 2)
			{
				System.out.println("Problem in Rleaf - v has degree " + v.getDegree());
				System.exit(0);
			}
			vu = arcs[0];
			uv = vu.getRev();
			vw = arcs[1];
			wv = vw.getRev();
			u = uv.getStart();
			w = vw.getEnd();
			if (debug)
				System.out.println("    Series Reduction: u=" + u.getNumber() + ", v=" + v.getNumber() + ", w=" + w.getNumber());
			if (uv.getLabel() == vw.getLabel())
				l = uv.getLabel();
			else if (uv.getLabel() == vw.getLabel() - 1)
				l = uv.getLabel();
			else if (uv.getLabel() - 1 == vw.getLabel())
				l = vw.getLabel();
			else
			{
				series.removeVertex(v);
				return false; //incorrect labels (according to Feo & Provan paper)
			}

			//create new arc;
			if (debug)
				System.out.println("    Creating new arc uw with label " + l);
			uw = new Arc(u, w, l);
			wu = new Arc(w, u, l);
			uw.setRev(wu);
			wu.setRev(uw);

			//get faces and deal with their firstarcs.
			F1 = uv.getFace();
			F2 = vu.getFace();
			//adjust the first arcs of the faces so they do not include arcs
			// that will be removed
			//			System.out.println(" Adjusting first arcs");
			//			F1.adjustFirstArc(uv,vw);
			//			F2.adjustFirstArc(wv,vu);
			//guaranteed that there is at least 1 other edge on F1 and F2
			//(since otherwise u must be the same as w --> uv and vw are in
			// series)

			//Add new arc to vertices u and w
			if (debug)
				System.out.println("    Adding new arcs to u and w (uw,wu)");
			u.insertAfter(uw, uv);
			w.insertAfter(wu, wv);

			//remove series edges from the graph
			if (debug)
				System.out.println("    Removing edges uv and vw from the graph");
			uv.removeEdge();
			vw.removeEdge();

			//Add the new arcs to F1 and F2
			if (debug)
				System.out.println("    adding new arcs to F1 and F2");
			F1.addArc(uw);
			F2.addArc(wu);

			//Change edges of gd object
			//firstfirst uncolour the old edges
			unhilight(workingarcs);

			//first find positions for new arcs
			int posforuw = myg.find_pos(v.getNumber(), u.getNumber());
			int posforwu = myg.find_pos(v.getNumber(), w.getNumber());

			//myg.print_graph("graph looks like this:");	

			//now remove edges uv and vw
			myg.remove_edge(u.getNumber(), v.getNumber());
			myg.remove_edge(v.getNumber(), w.getNumber());

			//remove vertex v
			myg.degree[v.getNumber()] = -1;

			//myg.print_graph("graph looks like this:");	

			//finally add edge uw
			myg.insert_edge(posforuw, u.getNumber(), posforwu, w.getNumber());

			//now color the new edges
			mygd.gcolor = new Coloring(myg);
			workingarcs = new Arc[1];
			workingarcs[0] = uw;
			hilight(workingarcs);

			//update auxilary information of the new edges according to:
			//        ab
			// c = -------
			//      a + b
			if (updateAux)
			{
				BigInteger[] a = uv.getAux();
				BigInteger[] b = vw.getAux();
				BigInteger Func1 = a[0].multiply(b[0]);
				BigInteger Func2 = a[0].multiply(b[1]).add(a[1].multiply(b[0]));
				BigInteger Func3 = a[1].multiply(b[1]);
				BigInteger[] c = new BigInteger[2];
				c[0] = Func1;
				c[1] = Func2;
				if (scaleEdges)
					scale(c);
				if (printAuxUpdate)
					System.out.println("series: c = " + c[0].toString() + " / " + c[1].toString());
				if (printAuxUpdateLength)
					System.out.println("series: c has " + c[0].toString().length() + " / " + c[1].toString().length() + " digits.");
				uw.setEdgeAux(c);

				//update the multiplier according to:
				// M = M(a + b)
				M[0] = M[0].multiply(Func2);
				M[1] = M[1].multiply(Func3);
				if (scaleM)
					scale(M);
				if (printAuxUpdate)
					System.out.println("series: M = " + M[0].toString() + " / " + M[1].toString());
				if (printAuxUpdateLength)
					System.out.println("series: M has " + M[0].toString().length() + " / " + M[1].toString().length() + " digits.");
			}

			//update the lists
			if (debug)
				System.out.println("    Updating the lists");
			switchlists(v, 0, 2);
			switchlists(F1, F1.getDegree(), F1.getDegree() + 1);
			switchlists(F2, F2.getDegree(), F2.getDegree() + 1);
			nops++;
			return true;
		}

		//returns true if the reduction is actually performed, false if it is
		// not performed because of invalid labeling.
		private boolean Rdeltawye(Face d)
		{
			if (debug)
			{
				System.out.println();
				System.out.println("Starting Rdeltawye: face " + d.getNumber());
			}
			Face F1, F2, F3;
			Vertex x, y, z, q;
			Arc xy, yx, yz, zy, zx, xz, qx, xq, qy, yq, qz, zq;
			Arc[] d_arcs = d.getArcs();
			int l;

			//get the three arcs in the delta, the reversals of the arcs, and
			// the three
			//vertices, as well as the three faces adjacent to arcs yx,zy, and
			// xz
			if (d_arcs[0].getLabel() == d_arcs[1].getLabel() && d_arcs[2].getLabel() == d_arcs[0].getLabel() - 1)
			{
				xy = d_arcs[0];
				yz = d_arcs[1];
				zx = d_arcs[2];
			}
			else if (d_arcs[2].getLabel() == d_arcs[0].getLabel() && d_arcs[1].getLabel() == d_arcs[2].getLabel() - 1)
			{
				xy = d_arcs[2];
				yz = d_arcs[0];
				zx = d_arcs[1];
			}
			else if (d_arcs[1].getLabel() == d_arcs[2].getLabel() && d_arcs[0].getLabel() == d_arcs[1].getLabel() - 1)
			{
				xy = d_arcs[1];
				yz = d_arcs[2];
				zx = d_arcs[0];
			}
			else
			{
				if (debug)
					System.out.println("    Aborting due to incorrect labels");
				delta.removeFace(d);
				return false;
			}
			l = zx.getLabel(); //know now that xy and yz have label l+1 and zx
								// has label l

			//get the reverse arcs, the vertices and the faces
			yx = xy.getRev();
			x = xy.getStart();
			F1 = yx.getFace();
			zy = yz.getRev();
			y = yz.getStart();
			F2 = zy.getFace();
			xz = zx.getRev();
			z = zx.getStart();
			F3 = xz.getFace();

			//create the new center vertex of the Y, degree = 3
			q = new Vertex(nvertices, 3);
			if (nvertices + 1 < vertices.length)
			{
				vertices[nvertices] = q;
				nvertices++;
			}
			else
			{
				System.out.println("Need more vertices - currently have " + vertices.length);
				System.exit(0);
			}

			if (debug)
				System.out.println("    Delta-Wye: x=" + x.getNumber() + ", y=" + y.getNumber() + ", z=" + z.getNumber() + ", q="
						+ q.getNumber());
			//create new arcs of the Y
			if (debug)
				System.out.println("    Creating new arcs");
			qx = new Arc(q, x, l);
			xq = new Arc(x, q, l);
			qx.setRev(xq);
			xq.setRev(qx);
			qy = new Arc(q, y, l + 1);
			yq = new Arc(y, q, l + 1);
			qy.setRev(yq);
			yq.setRev(qy);
			qz = new Arc(q, z, l);
			zq = new Arc(z, q, l);
			qz.setRev(zq);
			zq.setRev(qz);

			//update auxilary information of the new edges according to:
			// {a} {1/alpha}
			// {b} = (alpha*beta + alpha*gamma + beta*gamma) * {1/beta }
			// {c} {1/gamma}
			if (updateAux)
			{
				BigInteger[] alpha = yz.getAux();
				BigInteger[] beta = xy.getAux();
				BigInteger[] gamma = zx.getAux();
				BigInteger Func1 = alpha[0].multiply(beta[0].multiply(gamma[1])).add(
						alpha[0].multiply(beta[1].multiply(gamma[0])).add(alpha[1].multiply(beta[0].multiply(gamma[0]))));
				BigInteger Func2 = alpha[1].multiply(beta[1].multiply(gamma[1]));
				BigInteger Func3 = alpha[0].multiply(beta[0].multiply(gamma[0]));
				BigInteger[] a = new BigInteger[2];
				BigInteger[] b = new BigInteger[2];
				BigInteger[] c = new BigInteger[2];
				a[0] = Func1.multiply(alpha[1]);
				a[1] = Func2.multiply(alpha[0]);
				if (scaleEdges)
					scale(a);
				if (printAuxUpdate)
					System.out.println("deltawye: a = " + a[0].toString() + " / " + a[1].toString());
				if (printAuxUpdateLength)
					System.out.println("deltawye: a has " + a[0].toString().length() + " / " + a[1].toString().length() + " digits.");
				qx.setEdgeAux(a);
				b[0] = Func1.multiply(beta[1]);
				b[1] = Func2.multiply(beta[0]);
				if (scaleEdges)
					scale(b);
				if (printAuxUpdate)
					System.out.println("deltawye: b = " + b[0].toString() + " / " + b[1].toString());
				if (printAuxUpdateLength)
					System.out.println("deltawye: b has " + b[0].toString().length() + " / " + b[1].toString().length() + " digits.");
				qz.setEdgeAux(b);
				c[0] = Func1.multiply(gamma[1]);
				c[1] = Func2.multiply(gamma[0]);
				if (scaleEdges)
					scale(c);
				if (printAuxUpdate)
					System.out.println("deltawye: c = " + c[0].toString() + " / " + c[1].toString());
				if (printAuxUpdateLength)
					System.out.println("deltawye: c has " + c[0].toString().length() + " / " + c[1].toString().length() + " digits.");
				qy.setEdgeAux(c);

				//update the multiplier according to:
				//                                     M
				// M =
				// -------------------------------------------------------------------
				//     (alpha*beta + alpha*gamma + beta*gamma)(1/alpha + 1/beta +
				// 1/gamma)
				M[0] = M[0].multiply(Func2.multiply(Func3));
				M[1] = M[1].multiply(Func1.multiply(Func1));
				if (scaleM)
					scale(M);
				if (printAuxUpdate)
					System.out.println("deltawye: M = " + M[0].toString() + " / " + M[1].toString());
				if (printAuxUpdateLength)
					System.out.println("deltawye: M has " + M[0].toString().length() + " / " + M[1].toString().length() + " digits.");
			}

			//Change edges of gd object
			//firstfirst uncolour the old edges
			unhilight(workingarcs);

			//first find positions for new arcs
			int posforxq = myg.find_pos(z.getNumber(), x.getNumber()) % (myg.degree[x.getNumber()] - 1);
			int posforqx = 0;
			int posforyq = myg.find_pos(x.getNumber(), y.getNumber()) % (myg.degree[y.getNumber()] - 1);
			int posforqy = 1;
			int posforzq = myg.find_pos(y.getNumber(), z.getNumber()) % (myg.degree[z.getNumber()] - 1);
			int posforqz = 2;

			//myg.print_graph("graph looks like this:");	

			//now remove d's edges
			myg.remove_edge(x.getNumber(), y.getNumber());
			myg.remove_edge(y.getNumber(), z.getNumber());
			myg.remove_edge(z.getNumber(), x.getNumber());

			//add q to mygd
			mygd = mygd.make_bigger(1);
			myg = mygd.g;
			myg.degree[q.getNumber()] = 0;

			//mygd.embed.set_angle_level(q.getNumber(),qangle,qlevel);
			int[] newxy = getCenter(d);
			mygd.embed.set_angle_level_xy(q.getNumber(), newxy[0], newxy[1]);
			//myg.print_graph("graph looks like this:");	

			//finally add the 3 new edges to q
			myg.insert_edge(posforxq, x.getNumber(), posforqx, q.getNumber());
			myg.insert_edge(posforyq, y.getNumber(), posforqy, q.getNumber());
			myg.insert_edge(posforzq, z.getNumber(), posforqz, q.getNumber());

			//Add arcs to new vertex q
			if (debug)
				System.out.println("    Adding arcs to q");
			q.addArc(qx);
			q.addArc(qz);
			q.addArc(qy); //in THIS order!!!

			//Add arcs to vertices x,y,z - more complicated as we have to
			// maintain the embedding.
			if (debug)
				System.out.println("    Adding arcs to x,y,z");
			x.insertBefore(xq, xy); //put arc (x,q) before arc (x,z) in x's adjacency list
			y.insertBefore(yq, yz); //put arc (y,q) before arc (y,x) in y's adjacency list
			z.insertBefore(zq, zx); //put arc (z,q) before arc (z,y) in z's adjacency list

			//remove d's edges (arcs) from the graph
			if (debug)
				System.out.println("    Removing delta's edges");
			xy.removeEdge();
			yz.removeEdge();
			zx.removeEdge();

			//Add the new Y arcs to F1, F2, and F3
			if (debug)
				System.out.println("    Adding the Y arcs to the faces");
			F1.addArc(yq);
			F1.addArc(qx);
			F2.addArc(zq);
			F2.addArc(qy);
			F3.addArc(xq);
			F3.addArc(qz);

			//now color the new edges
			mygd.gcolor = new Coloring(myg);
			workingarcs = hilightarcs(q);
//			mygd.gcolor.color_edge(x.getNumber(),q.getNumber(),Color.blue);
//			mygd.gcolor.color_edge(y.getNumber(),q.getNumber(),Color.blue);
//			mygd.gcolor.color_edge(z.getNumber(),q.getNumber(),Color.blue);

			if (debug)
				System.out.println("   Fixing the lists");
			switchlists(d, 0, 3);
			switchlists(x, x.getDegree(), x.getDegree() + 1);
			switchlists(y, y.getDegree(), y.getDegree() + 1);
			switchlists(z, z.getDegree(), z.getDegree() + 1);
			switchlists(q, q.getDegree(), 0);
			switchlists(F1, F1.getDegree(), F1.getDegree() - 1);
			switchlists(F2, F2.getDegree(), F2.getDegree() - 1);
			switchlists(F3, F3.getDegree(), F3.getDegree() - 1);

			nops++;

			return true;
		}

		//returns true if the reduction is actually performed, false if it is
		// not performed because of invalid labeling.
		private boolean Rwyedelta(Vertex q)
		{
			if (debug)
			{
				System.out.println();
				System.out.println("Starting Rwyedelta: vertex " + q.getNumber());
			}

			Face F1, F2, F3, d;
			Vertex x, y, z;
			Arc xy, yx, yz, zy, zx, xz, qx, xq, qy, yq, qz, zq;
			Arc[] q_arcs = new Arc[3];
			q_arcs[0] = q.getFirstArc();
			q_arcs[1] = q_arcs[0].getNext();
			q_arcs[2] = q_arcs[1].getNext();
			int l;

			//get the three arcs in the delta, the reversals of the arcs, and
			// the three
			//vertices, as well as the three faces adjacent to arcs yx,zy, and
			// xz
			if (q_arcs[0].getLabel() == q_arcs[1].getLabel() && q_arcs[2].getLabel() == q_arcs[0].getLabel() - 1)
			{
				qy = q_arcs[0];
				qx = q_arcs[1];
				qz = q_arcs[2];
			}
			else if (q_arcs[2].getLabel() == q_arcs[0].getLabel() && q_arcs[1].getLabel() == q_arcs[2].getLabel() - 1)
			{
				qy = q_arcs[2];
				qx = q_arcs[0];
				qz = q_arcs[1];
			}
			else if (q_arcs[1].getLabel() == q_arcs[2].getLabel() && q_arcs[0].getLabel() == q_arcs[1].getLabel() - 1)
			{
				qy = q_arcs[1];
				qx = q_arcs[2];
				qz = q_arcs[0];
			}
			else
			{
				if (debug)
				{
					System.out.println("    Aborting due to incorrect labels");
				}
				wye.removeVertex(q);
				return false;
			}

			l = qz.getLabel(); //know now that qx and qy have label l+1 and qz
								// has label l
			//get the reverse arcs, the vertices and the faces
			xq = qx.getRev();
			x = xq.getStart();
			F1 = qx.getFace();
			yq = qy.getRev();
			y = yq.getStart();
			F2 = qy.getFace();
			zq = qz.getRev();
			z = zq.getStart();
			F3 = qz.getFace();

			if (debug)
			{
				System.out.println("    Wye-Delta: x=" + x.getNumber() + ", y=" + y.getNumber() + ", z=" + z.getNumber() + ", q="
						+ q.getNumber());
			}

			//create new arcs of the delta
			xy = new Arc(x, y, l + 1);
			yx = new Arc(y, x, l + 1);
			xy.setRev(yx);
			yx.setRev(xy);
			yz = new Arc(y, z, l);
			zy = new Arc(z, y, l);
			yz.setRev(zy);
			zy.setRev(yz);
			zx = new Arc(z, x, l);
			xz = new Arc(x, z, l);
			zx.setRev(xz);
			xz.setRev(zx);

			if (updateAux)
			{
				//update auxilary information of the new edges according to:
				// {alpha} abc {1/a}
				// {beta } = ----------- * {1/b}
				// {gamma} a + b + c {1/c}
				BigInteger[] a = qx.getAux();
				BigInteger[] b = qz.getAux();
				BigInteger[] c = qy.getAux();
				BigInteger Func1 = a[0].multiply(b[0].multiply(c[0]));
				BigInteger Func2 = a[0].multiply(b[1].multiply(c[1])).add(a[1].multiply(b[0].multiply(c[1])))
						.add(a[1].multiply(b[1].multiply(c[0])));
				BigInteger Func3 = a[1].multiply(b[1].multiply(c[1]));
				BigInteger[] alpha = new BigInteger[2];
				BigInteger[] beta = new BigInteger[2];
				BigInteger[] gamma = new BigInteger[2];
				alpha[0] = Func1.multiply(a[1]);
				alpha[1] = Func2.multiply(a[0]);
				if (scaleEdges)
					scale(alpha);
				if (printAuxUpdate)
					System.out.println("wyedelta: alpha = " + alpha[0].toString() + " / " + alpha[1].toString());
				if (printAuxUpdateLength)
					System.out.println("wyedelta: alpha has " + alpha[0].toString().length() + " / " + alpha[1].toString().length()
							+ " digits.");
				yz.setEdgeAux(alpha);
				beta[0] = Func1.multiply(b[1]);
				beta[1] = Func2.multiply(b[0]);
				if (scaleEdges)
					scale(beta);
				if (printAuxUpdate)
					System.out.println("wyedelta: beta = " + beta[0].toString() + " / " + beta[1].toString());
				if (printAuxUpdateLength)
					System.out.println("wyedelta: beta has " + beta[0].toString().length() + " / " + beta[1].toString().length()
							+ " digits.");
				xy.setEdgeAux(beta);
				gamma[0] = Func1.multiply(c[1]);
				gamma[1] = Func2.multiply(c[0]);
				if (scaleEdges)
					scale(gamma);
				if (printAuxUpdate)
					System.out.println("wyedelta: gamma = " + gamma[0].toString() + " / " + gamma[1].toString());
				if (printAuxUpdateLength)
					System.out.println("wyedelta: gamma has " + gamma[0].toString().length() + " / " + gamma[1].toString().length()
							+ " digits.");
				zx.setEdgeAux(gamma);

				//update the multiplier according to:
				// M = M(a + b + c)
				M[0] = M[0].multiply(Func2);
				M[1] = M[1].multiply(Func3);
				if (scaleM)
					scale(M);
				if (printAuxUpdate)
					System.out.println("wyedelta: M = " + M[0].toString() + " / " + M[1].toString());
				if (printAuxUpdateLength)
					System.out.println("wyedelta: M has " + M[0].toString().length() + " / " + M[1].toString().length() + " digits.");
			}

			//adjust the first arcs of the faces so they do not include arcs
			// that will be removed
			//			System.out.println(" Adjusting first arcs");
			//			F1.adjustFirstArc(yq,qx);
			//			F2.adjustFirstArc(zq,qy);
			//			F3.adjustFirstArc(xq,qz);

			//Change edges of gd object
			//firstfirst uncolour the old edges
			unhilight(workingarcs);

			//first find positions for new arcs
			//the order matters here as once we've added an arc to a vertex, we
			// need to
			//remember whether to add the next one before or after it - draw a
			// picture!
			int posforxz = myg.find_pos(q.getNumber(), x.getNumber());
			int posforzx = myg.find_pos(q.getNumber(), z.getNumber());
			int posforxy = posforxz + 1;
			int posforyx = myg.find_pos(q.getNumber(), y.getNumber());
			int posforzy = posforzx;
			int posforyz = posforyx + 1;
			//now remove q's edges
			myg.remove_edge(q.getNumber(), x.getNumber());
			myg.remove_edge(q.getNumber(), y.getNumber());
			myg.remove_edge(q.getNumber(), z.getNumber());

			//remove q
			myg.degree[q.getNumber()] = AdjList.NO_NEIGHBOUR;

			//finally add the 3 new edges
			myg.insert_edge(posforxz, x.getNumber(), posforzx, z.getNumber());
			myg.insert_edge(posforxy, x.getNumber(), posforyx, y.getNumber());
			myg.insert_edge(posforyz, y.getNumber(), posforzy, z.getNumber());

			//myg.print_graph("graph looks like this:");

			//Add non-delta arcs to vertices x,y,z
			if (debug)
			{
				System.out.println("    Adding non-delta arcs (xy,yz,zx)");
			}
			x.insertAfter(xy, xq);
			y.insertAfter(yz, yq);
			z.insertAfter(zx, zq);

			//remove q's edges (arcs) from the graph
			if (debug)
			{
				System.out.println("    Removing q's edges from the graph");
			}
			qx.removeEdge();
			qy.removeEdge();
			qz.removeEdge();

			//Add delta arcs to vertices x,y,z
			if (debug)
			{
				System.out.println("    Adding delta arcs (xz,yx,zy)");
			}
			x.insertBefore(xz, xy);
			y.insertBefore(yx, yz);
			z.insertBefore(zy, zx);

			//Add the new delta arcs to F1, F2, and F3
			if (debug)
			{
				System.out.println("    adding the new delta arcs to F1 (yx), F2(zy), and F3(xz)");
			}
			F1.addArc(yx);
			F2.addArc(zy);
			F3.addArc(xz);

			//Add the new face
			if (debug)
			{
				System.out.println("    Adding the new face");
			}
			d = new Face(nfaces);
			if (nfaces + 1 < faces.length)
			{
				faces[nfaces] = d;
				nfaces++;
			}
			else
			{
				System.out.println("Need more faces - currently have " + faces.length);
				System.exit(0);
			}
			//add the arcs to the new face
			if (debug)
				System.out.println("    Adding the arcs to the new face");
			d.addArc(xy);
			d.addArc(yz);
			d.addArc(zx);

			//now color the new edges
			mygd.gcolor = new Coloring(myg);
			workingarcs = hilightarcs(d);

			//update the lists
			if (debug)
				System.out.println("    Updating the lists");
			switchlists(q, 0, 3);
			switchlists(x, x.getDegree(), x.getDegree() - 1);
			switchlists(y, y.getDegree(), y.getDegree() - 1);
			switchlists(z, z.getDegree(), z.getDegree() - 1);
			switchlists(F1, F1.getDegree(), F1.getDegree() + 1);
			switchlists(F2, F2.getDegree(), F2.getDegree() + 1);
			switchlists(F3, F3.getDegree(), F3.getDegree() + 1);

			nops++;

			return true;
		}

		private int[] getCenter(Face f)
		{
			Vertex[] verts = f.getVertices();
			int xpoints[] = new int[verts.length];
			int ypoints[] = new int[verts.length];
			for (int i = 0; i < verts.length; i++)
			{
				xpoints[i] = mygd.embed.findx(verts[i].getNumber());
				ypoints[i] = mygd.embed.findy(verts[i].getNumber());
			}
			return getCenter(xpoints, ypoints, verts.length);
		}

		private int[] getCenter(int xpoints[], int ypoints[], int npoints)
		{
			int[] center = new int[2];
			Polygon P = new Polygon(xpoints, ypoints, npoints);
			Rectangle R = P.getBounds();
			center[0] = (int) R.getCenterX();
			center[1] = (int) R.getCenterY();
			return center;
		}

		private Vertex[] hilightvertices(Vertex v)
		{
			Vertex[] neighbours = v.getNeighbours();
			Vertex[] verts = new Vertex[neighbours.length + 1];
			verts[0] = v;
			for (int i = 0; i < neighbours.length; i++)
				verts[i + 1] = neighbours[i];
			hilight(verts);
			return verts;
		}

		private Arc[] hilightarcs(Vertex v)
		{
			Arc[] arcs = v.getArcs();
			hilight(arcs);
			return arcs;
		}

		private Vertex[] hilightvertices(Face f)
		{
			Vertex[] verts = f.getVertices();
			hilight(verts);
			return verts;
		}

		private Arc[] hilightarcs(Face f)
		{
			Arc[] arcs = f.getArcs();
			hilight(arcs);
			return arcs;
		}

		private void hilight(Vertex[] verts)
		{
			for (int i = 0; i < verts.length; i++)
				mygd.gcolor.color_vertex(verts[i].getNumber(), Color.blue);
			FUIGUI.fullereneArea.new_picture(mygd);
		}

		private void hilight(Arc[] arcs)
		{
			for (int i = 0; i < arcs.length; i++)
				mygd.gcolor.color_edge(arcs[i].getStart().getNumber(), arcs[i].getEnd().getNumber(), Color.blue);
			FUIGUI.fullereneArea.new_picture(mygd);
		}

		private void unhilight(Vertex[] verts)
		{
			for (int i = 0; i < verts.length; i++)
				mygd.gcolor.color_vertex(verts[i].getNumber(), Color.yellow);
			FUIGUI.fullereneArea.new_picture(mygd);
		}

		private void unhilight(Arc[] arcs)
		{
			for (int i = 0; i < arcs.length; i++)
				mygd.gcolor.color_edge(arcs[i].getStart().getNumber(), arcs[i].getEnd().getNumber(), Color.red);
			FUIGUI.fullereneArea.new_picture(mygd);
		}

		private void switchlists(Vertex v, int newdegree, int olddegree)
		{
			switch (olddegree)
			{
			case 1:
				leaf.removeVertex(v);
				break;
			case 2:
				series.removeVertex(v);
				break;
			case 3:
				wye.removeVertex(v);
				break;
			}
			switch (newdegree)
			{
			case 1:
				leaf.addVertex(v);
				break;
			case 2:
				series.addVertex(v);
				break;
			case 3:
				wye.addVertex(v);
				break;
			}
		}

		private void switchlists(Face f, int newdegree, int olddegree)
		{
			switch (olddegree)
			{
			case 1:
				loop.removeFace(f);
				break;
			case 2:
				parallel.removeFace(f);
				break;
			case 3:
				delta.removeFace(f);
				break;
			}
			switch (newdegree)
			{
			case 1:
				loop.addFace(f);
				break;
			case 2:
				parallel.addFace(f);
				break;
			case 3:
				delta.addFace(f);
				break;
			}
		}

		private void printlists()
		{
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("Loops: " + loop.getSize());
			loop.printlist();
			System.out.println("Leafs: " + leaf.getSize());
			leaf.printlist();
			System.out.println("Series: " + series.getSize());
			series.printlist();
			System.out.println("Parallels: " + parallel.getSize());
			parallel.printlist();
			System.out.println("Deltas: " + delta.getSize());
			delta.printlist();
			System.out.println("Wyes: " + wye.getSize());
			wye.printlist();
			System.out.println("------------------------------------------------------------------------------");
		}

		private void scale(BigInteger[] a)
		{
			BigInteger g = a[0].gcd(a[1]);
			a[0] = a[0].divide(g);
			a[1] = a[1].divide(g);
		}
	}

	private class Vertex
	{
		int number;

		int degree;

		int label;

		ArcList adj;

		Vertex next;

		Vertex prev;

		boolean inlist;

		public Vertex(int vertex_number, int vertex_degree, int vertex_label)
		{
			label = vertex_label;
			number = vertex_number;
			//degree = vertex_degree;
			degree = 0;
			adj = new ArcList();
			next = null;
			prev = null;
			inlist = false;
		}

		public Vertex(int vertex_number, int vertex_degree)
		{
			this(vertex_number, vertex_degree, -1);
		}

		public void addArc(Arc uv)
		{
			adj.addArc(uv);
			degree++;
		}

		public void removeArc(Arc a)
		{
			adj.removeArc(a);
			degree--;
		}

		//insert arc uv before arc aft in the arclist to which aft belongs
		public void insertBefore(Arc uv, Arc aft)
		{
			uv.setPrev(aft.getPrev());
			uv.setNext(aft);
			aft.getPrev().setNext(uv);
			aft.setPrev(uv);
			degree++;
		}

		//insert arc uv after arc bef in the arclist to which bef belongs
		public void insertAfter(Arc uv, Arc bef)
		{
			uv.setNext(bef.getNext());
			uv.setPrev(bef);
			bef.getNext().setPrev(uv);
			bef.setNext(uv);
			degree++;
		}

		public void print()
		{
			System.out.println(number + ": degree=" + degree + ", label=" + label);
			adj.print();
		}

		public Arc[] getArcs()
		{
			Arc[] arcs = new Arc[degree];
			if (degree > 0)
			{
				arcs[0] = getFirstArc();
				for (int i = 1; i < degree; i++)
				{
					arcs[i] = arcs[i - 1].getNext();
				}
			}
			return arcs;
		}

		public Vertex[] getNeighbours()
		{
			Arc[] arcs = getArcs();
			Vertex[] vertices = new Vertex[degree];
			if (degree > 0)
			{
				for (int i = 0; i < degree; i++)
				{
					vertices[i] = arcs[i].getEnd();
				}
			}
			return vertices;
		}

		public int getNumber()
		{
			return number;
		}

		public Arc getFirstArc()
		{
			return adj.getFirst();
		}

		public int getDegree()
		{
			return degree;
		}

		public Vertex getNext()
		{
			return next;
		}

		public Vertex getPrev()
		{
			return prev;
		}

		public void setNext(Vertex v)
		{
			next = v;
		}

		public void setPrev(Vertex v)
		{
			prev = v;
		}

		public void setInList()
		{
			inlist = true;
		}

		public void setNotInList()
		{
			inlist = false;
		}

		public boolean isInList()
		{
			return inlist;
		}
	}

	private class Arc
	{
		Vertex start;

		Vertex end;

		boolean faceWalked;

		BigInteger[] aux = new BigInteger[2];

		int label;

		Arc revArc;

		Face myFace;

		Arc next;

		Arc prev;

		public Arc(Vertex u, Vertex v, int edge_label)
		{
			start = u;
			end = v;
			faceWalked = false;
			//aux = new BigDecimal(BigInteger.ONE);
			aux[0] = new BigInteger("1");
			aux[1] = new BigInteger("1");
			label = edge_label;
			revArc = null; //found after all arcs have been created
			myFace = null; //found during face walk
			next = null;
			prev = null;
			;
		}

		public void setRev(Arc rev)
		{
			revArc = rev;
		}

		public BigInteger[] getAux()
		{
			return aux;
		}

		public void setEdgeAux(BigInteger[] a)
		{
			setAux(a);
			getRev().setAux(a);
		}

		private void setAux(BigInteger[] a)
		{
			aux[0] = a[0];
			aux[1] = a[1];
		}

		public void print()
		{
			if (myFace == null)
				System.out.println("MYFACE IS NULL!");
			System.out.println("     (" + start.getNumber() + "," + end.getNumber() + "): faceWalked=" + faceWalked + "  aux="
					+ aux[0].toString() + "/" + aux[1].toString() + "  label=" + label + "  face=" + myFace.getNumber());
		}

		public void printwoface()
		{
			System.out.println("     (" + start.getNumber() + "," + end.getNumber() + "): faceWalked=" + faceWalked + "  aux="
					+ aux[0].toString() + "/" + aux[1].toString() + "  label=" + label);
		}

		public void printJustArc()
		{
			System.out.print("     (" + start.getNumber() + "," + end.getNumber() + ")");
		}

		public void setFace(Face f)
		{
			myFace = f;
			faceWalked = true;
		}

		public boolean isFaceWalked()
		{
			return faceWalked;
		}

		public Vertex getStart()
		{
			return start;
		}

		public Vertex getEnd()
		{
			return end;
		}

		public Arc getNext()
		{
			return next;
		}

		public void setNext(Arc a)
		{
			next = a;
		}

		public Arc getPrev()
		{
			return prev;
		}

		public void setPrev(Arc a)
		{
			prev = a;
		}

		public int getLabel()
		{
			return label;
		}

		public Face getFace()
		{
			return myFace;
		}

		public Arc getRev()
		{
			return revArc;
		}

		//remove this arc and its reverse permanently --> remove the edge
		public void removeEdge()
		{
			removeArc(this);
			removeArc(getRev());
		}

		private void removeArc(Arc uv)
		{
			uv.getFace().removeArc(uv);
			uv.getStart().removeArc(uv);
		}
	}

	private class Face
	{
		int degree;

		Face next;

		Face prev;

		Arc firstArc;

		int number;

		boolean inlist;

		public Face(int num)
		{
			degree = 0;
			next = null;
			prev = null;
			firstArc = null;
			number = num;
			inlist = false;
		}

		public void addArc(Arc uv)
		{
			//if(degree==0)
			setFirstArc(uv);
			uv.setFace(this);
			degree++;
		}

		//changes the firstarc of this face as necessary so uv and vw are NOT
		// the first arc.
		public void adjustFirstArc(Arc uv, Arc vw)
		{ //arcs are adjacent on the
			// face
			if (firstArc == uv)
				firstArc = vw;
			if (firstArc == vw)
				firstArc = vw.getRev().getNext();
		}

		public void removeArc(Arc uv)
		{
			if (firstArc == uv)
				firstArc = uv.getRev().getNext();
			if (uv.getFace() == this)
			{
				uv.setFace(null);
				degree--;
			}
		}

		public void setFirstArc(Arc uv)
		{
			firstArc = uv;
		}

		public Arc getFirstArc()
		{
			return firstArc;
		}

		public int getDegree()
		{
			return degree;
		}

		public int getNumber()
		{
			return number;
		}

		public void print()
		{
			System.out.println("Face " + (number) + " has " + degree + " edges:");
			Arc[] arcs = getArcs();
			for (int i = 0; i < arcs.length; i++)
			{
				//arcs[i].printJustArc();
				arcs[i].print();
			}
			System.out.println();
		}

		public Arc[] getArcs()
		{
			int count = 0;
			Arc uv;
			Arc[] arcs = new Arc[degree];
			if (degree > 2)
			{
				uv = firstArc;
				arcs[count++] = uv;
				while (true)
				{
					if (count == degree)
					{
						System.out.println("Problem with facewalk");
						System.exit(0);
					}
					uv = uv.getRev().getNext();
					arcs[count++] = uv;
					if (uv.getEnd() == firstArc.getStart())
						break;
				}
			}
			else if (degree == 2)
			{
				arcs[0] = firstArc;
				arcs[1] = firstArc.getRev().getNext();
			}
			else if (degree == 1)
			{
				arcs[0] = firstArc;
			}
			return arcs;
		}

		public Vertex[] getVertices()
		{
			int count = 0;
			Arc uv;
			Vertex[] vertices = new Vertex[degree];
			if (degree > 2)
			{
				uv = firstArc;
				vertices[count++] = uv.getStart();
				while (true)
				{
					if (count == degree)
					{
						System.out.println("Problem with facewalk");
						System.exit(0);
					}
					uv = uv.getRev().getNext();
					vertices[count++] = uv.getStart();
					if (uv.getEnd() == firstArc.getStart())
						break;
				}
			}
			else if (degree == 2)
			{
				vertices[0] = firstArc.getStart();
				vertices[1] = firstArc.getEnd();
			}
			else if (degree == 1)
			{
				vertices[0] = firstArc.getStart();
			}
			return vertices;
		}

		public Face getNext()
		{
			return next;
		}

		public Face getPrev()
		{
			return prev;
		}

		public void setNext(Face n)
		{
			next = n;
		}

		public void setPrev(Face p)
		{
			prev = p;
		}

		public void setInList()
		{
			inlist = true;
		}

		public void setNotInList()
		{
			inlist = false;
		}

		public boolean isInList()
		{
			return inlist;
		}
	}

	//LISTS
	private class ArcList
	{
		Arc head;

		int size;

		public ArcList()
		{
			size = 0;
			head = null;
		}

		public void removeArc(Arc uv)
		{
			if (uv.getNext() == uv && uv.getPrev() == uv)
			{
				head = null;
			}
			else
			{
				head = uv.getNext();
				uv.getPrev().setNext(uv.getNext());
				uv.getNext().setPrev(uv.getPrev());
			}
			uv.setPrev(null);
			uv.setNext(null);
		}

		//add arc uv to "end" of circular list (in front of head)
		public void addArc(Arc uv)
		{
			size++;
			if (head == null)
			{
				head = uv;
				head.next = head;
				head.prev = head;
			}
			else
			{
				head.prev.next = uv;
				uv.prev = head.prev;
				head.prev = uv;
				uv.next = head;
			}
		}

		public void print()
		{
			Arc current = head;
			if (current != null)
			{
				current.printwoface();
				current = current.next;
				while (current != head)
				{
					current.printwoface();
					current = current.next;
				}
			}
		}

		public Arc getFirst()
		{
			return head;
		}
	}

	private class FaceList
	{
		Face head;

		Face tail;

		int size;

		public FaceList()
		{
			head = null;
			tail = null;
			size = 0;
		}

		public int getSize()
		{
			return size;
		}

		public void addFace(Face f)
		{
			if (head == null && tail == null)
			{
				head = f;
				tail = f;
				f.setPrev(null);
				f.setNext(null);
			}
			else
			{
				tail.setNext(f);
				f.setPrev(tail);
				f.setNext(null);
				tail = f;
			}
			f.setInList();
			size++;
		}

		public void removeFace(Face f)
		{
			if (f.isInList())
			{
				size--;
				if (f.getPrev() != null)
				{
					f.getPrev().setNext(f.getNext());
				}
				if (f.getNext() != null)
				{
					f.getNext().setPrev(f.getPrev());
				}
				if (head == f)
					head = f.getNext();
				if (tail == f)
					tail = f.getPrev();
				f.setNext(null);
				f.setPrev(null);
				f.setNotInList();
			}
		}

		public Face getFirst()
		{
			return head;
		}

		//sloppy as f could theoretically not belong to this list, be careful!
		public Face getNext(Face f)
		{
			return f.getNext();
		}

		public void printlist()
		{
			Face f = head;
			while (f != null)
			{
				f.print();
				f = f.getNext();
			}
		}

	}

	private class VertexList
	{
		Vertex head;

		Vertex tail;

		int size;

		public VertexList()
		{
			head = null;
			tail = null;
			size = 0;
		}

		public void addVertex(Vertex v)
		{
			if (head == null && tail == null)
			{
				head = v;
				tail = v;
				v.setPrev(null);
				v.setNext(null);
			}
			else
			{
				tail.setNext(v);
				v.setPrev(tail);
				v.setNext(null);
				tail = v;
			}
			v.setInList();
			size++;
		}

		public Vertex getFirst()
		{
			return head;
		}

		public void removeVertex(Vertex v)
		{
			if (v.isInList())
			{
				size--;
				if (v.getPrev() != null)
				{
					v.getPrev().setNext(v.getNext());
				}
				if (v.getNext() != null)
				{
					v.getNext().setPrev(v.getPrev());
				}
				if (head == v)
					head = v.getNext();
				if (tail == v)
					tail = v.getPrev();
				v.setNext(null);
				v.setPrev(null);
				v.setNotInList();
			}
		}

		public int getSize()
		{
			return size;
		}

		public void printlist()
		{
			Vertex v = head;
			while (v != null)
			{
				v.print();
				v = v.getNext();
			}
		}

	}
}
//GARBAGE!
/*
 * private void getFaces(int[][] G, int n) { int orig_u,degree_orig_u,u,degree_u,v,p,firstVertex;
 * int num_edges = 0; Face f; Arc uv; for(int i=0;i <n;i++) { orig_u = i; degree_orig_u = G[i][0];
 * for(int j=1;j <=degree_orig_u;j++) { v = G[i][j]; uv = getArc(orig_u,v); if(!uv.isFaceWalked()) {
 * //have found a new face f = new Face(nfaces++); addFace(f); f.setFirstArc(uv); uv.setFace(f);
 * //this also sets the faceWalked boolean of uv to true num_edges++; //traverse face that contains
 * (u,v) (face traversed in counterclockwise direction w.r.t. embedding) p = orig_u; u = v; v = -1;
 * while(true) { degree_u = G[u][0]; for(int k=1;k <=degree_u;k++) { //Find the vertex v after p in
 * u's adjacency list. if(G[u][k] == p) { if(k <degree_u) v = G[u][k+1]; else v = G[u][1]; } }//end
 * for uv = uv.getRev().getNext();//getArc(u,v); if(uv.isFaceWalked()) {
 * System.out.println("Error: Edge in face has already been traversed!"); System.exit(0); }
 * uv.setFace(f); //this also sets the faceWalked boolean of uv to true v = uv.getEnd().getNumber();
 * num_edges++; if(v==orig_u) break; //p = u; u = v; }//end while f.setDegree(num_edges); num_edges
 * = 0; } } } }
 */

/*
 * p = firstArc.getStart(); u = firstArc.getEnd(); v = null; while(true) { if(count==degree) {
 * System.out.println("Problem with facewalk"); System.exit(0); } //find arc (u,p) in u's adjacent
 * arc list degree_u = u.getDegree(); uv = u.getFirstAdj(); checkdeg = 1; while(uv.getEnd() != p) {
 * uv = uv.getNext(); checkdeg++; if(checkdeg>degree_u) {
 * System.out.println("Problem with facewalk"); System.exit(0); } } //arc (u,v) follows (u,p) in u's
 * adjacent arc list uv = uv.getNext(); v = uv.getEnd(); arcs[count++] = uv; //if we have returned
 * to the start then break if(uv.getEnd() == firstArc.getStart()) break; //otherwise continue along
 * the face p = u; u = v; }
 */
