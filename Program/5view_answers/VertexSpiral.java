/*
 * Author: Sean Daugherty
 *
 * This file provides an animated algorithm to 
 * display all 6 of the vertex spirals that start at a given vertex.  
 * They are shown in a loop.  This allows someone to
 * see which spirals work and which do not.
 *
 * The animation shows the spiral being drawn, 
 * then it pauses to show the spiral
 * for a while, while making the vertices that were not reached blink.
 */

import java.awt.Color;

import javax.swing.JOptionPane;

public class VertexSpiral {
	GraphDrawing gd; //original graph drawing
	int start; //vertex to start the spiral at
	int second; //neighbor number of second vertex of the spiral (0 to 2)
	int direction; //-1 if cw, 1 if ccw
	int curr, next; //current and next vertices in the spiral
	int pred []; //holds the predecessor of each vertex in the spiral
	final Color COLOR = Color.blue;
	final Color DULLCOLOR = Color.lightGray;
	final Color ENDCOLOR = Color.red;
	final Color FLASHCOLOR = Color.yellow;
	final int CW = -1;
	final int CCW = 1;
	final int UNCOLORED = -2;
	final int COLOREDFLOATER = -1;
	boolean drawpausemode; //whether we are drawing or pausing to view
	final boolean DRAW = true;
	final boolean PAUSE = false;
	final int PAUSESTEPS = 10; //number of steps to pause on
	int pausecount;
	boolean startnewspiral; //start a new spiral the next time or not

	public VertexSpiral(GraphDrawing x) {
	//initializes the spiralling and asks the user 
	//to enter the starting vertex
		gd = x;

		String numberEntered;
		//ask the user what vertex to start at
		numberEntered = JOptionPane.showInputDialog(
				FUIGUI.fullereneArea,
			"What vertex number should the spiral start at?",
			"Select Vertex",JOptionPane.QUESTION_MESSAGE);
		while (!isValidVertex(numberEntered)) {
			numberEntered = JOptionPane.showInputDialog(
					FUIGUI.fullereneArea,
"The value you entered is not a valid vertex number.\nWhat vertex number should the spiral start at?",
				"Select Vertex",JOptionPane.QUESTION_MESSAGE);
		}
		//start was set to the correct value inside 
		//the call to isValidVertex

		//start out with the first neighbor of the starting vertex
		second = 0;
		//start out in the cw direction
		direction = CW;

		//start out in draw mode
		drawpausemode = DRAW;

		//start with no pause count
		pausecount = 0;

		//initially start a new spiral
		startnewspiral = true;
	}

	public void next_step() {
		//finds the next vertex spiral of the graph 
		//in order and draws it

		if (drawpausemode == DRAW) {
			next_step_draw();
		} else {
			next_step_pause();
		}
	}

	private void next_step_draw() {
		//see if this iteration should start a spiral
		if (startnewspiral) {
			//start out with a fresh copy of the 
			//graph drawing, with everything in
			//a dull color
			for (int i=0; i<gd.g.n; i++) {
				gd.gcolor.vertex_color[i] = DULLCOLOR;
				for (int j=0; j<gd.g.degree[i]; j++)
					gd.gcolor.arc_color[i][j] = DULLCOLOR;
			}

			//hold an array to remember if a vertex 
			//was colored yet or not
			pred = new int[gd.g.n];
			for (int i=0; i<gd.g.n; i++) pred[i] = UNCOLORED;

			//color the start vertex, second vertex, 
			//and the edge between them
			colorVertex(start, ENDCOLOR);
			pred[start] = start;

			curr = start;
			next = gd.g.Adj[start][second];

			startnewspiral = false;

			//update the drawing
			FUIGUI.fullereneArea.new_picture(gd);
		}

		//now add the next edge to the spiral
		colorEdge(curr, next, COLOR);
		colorVertex(next, COLOR);
		pred[next]=curr;

		//now advance the spiral
		curr = next;

		//we now want to find the next vertex
		//but, if we are on a floater, we need to move 
		//to the real vertex
		if (gd.g.degree[curr]==1) { //floater vertex
			//remember who the previous vertex in the spiral is
			int prev = pred[curr];
			//then we need to move from the floater 
			//to the copy of the
			//vertex located in the main part of the fullerene
			curr = realVertexNumber(curr);
			//now we have to set the predecessor of the vertex to be
			//the floater, but make sure it is the correct floater
			int nbr;
			for (int i=0; i<3; i++) {
				nbr = gd.g.Adj[curr][i];
				if (realVertexNumber(nbr)==prev) { 
					//if this is the desired floater
					pred[curr] = nbr;
					break;
				}
			}
		}

		//look at vertex in position direction+position of pred
		next = gd.g.Adj[curr][(gd.g.find_pos(pred[curr], 
					curr)+direction+3)%3];
		//if colored already, look at next value by adding direction
		if (pred[next]!=UNCOLORED)
			next = gd.g.Adj[curr][(gd.g.find_pos(pred[curr], 
						curr)+2*direction+3)%3];
		//if that is colored, then all 3 neighbors already 
		//colored, so done with spiral
		if (pred[next]!=UNCOLORED) {
			//this is the end, so color it in the end color
			colorVertex(curr, ENDCOLOR);

			//calculate the next spiral to draw
			direction *= -1; //switch the direction
			//if we are now going to do cw, 
			//we need to move to the next vertex
			if (direction == CW) second = (second + 1) % 3;

			//now pause to show this spiral for a while
			drawpausemode = PAUSE;

			//start a new spiral next time
			startnewspiral = true;
		}

		//now actually draw the spiral
		FUIGUI.fullereneArea.new_picture(gd);
	}

	private void next_step_pause() {
		pausecount++; //pause for this iteration
		//if the max number of pauses is reached
		if (pausecount == PAUSESTEPS) {
			pausecount = 0; //reset the counter
			drawpausemode = DRAW; //switch back to draw mode
		}

		//now make all of the uncolored vertices flash (blink)
		for (int i=0; i< gd.g.n; i++) {
			//blink the uncolored vertices
			if (pred[i] == UNCOLORED) {
				if (pausecount % 2 == 0)
					gd.gcolor.vertex_color[i] = FLASHCOLOR;
				else
					gd.gcolor.vertex_color[i] = DULLCOLOR;
			}
		}
	}

	private boolean isValidVertex(String num) {
		//returns false if not a valid vertex number, otherwise returns true
		//and saves the valid number in the value "start"
		int vertex;
		//make sure the number is not null
		if (num==null) return false;
		//make sure the string is actually an integer
		try {
			vertex = Integer.parseInt(num);
		} catch (NumberFormatException e) {
			return false;
		}
		//make sure the integer is within range
		if (vertex < 0 || vertex >= CurrentFullerene.f.p.n) return false;
		start = vertex;
		return true;
	}

	private void colorEdge(int u, int v, Color c) {
		//colors the edge the given color, including all floater edges, if any

		//colors the edges between u and v
		gd.gcolor.arc_color[u][gd.g.find_pos(v,u)] = c;
		gd.gcolor.arc_color[v][gd.g.find_pos(u,v)] = c;

		//maybe this edge is also a floater edge somewhere
		if (gd.g.degree[u]==1 || gd.g.degree[v]==1) {
			//get the real vertex numbers
			int realu, realv;
			realu = realVertexNumber(u);
			realv = realVertexNumber(v);

			//loop through the floaters
			int nbr; //neighbor of a floater
			for (int i=CurrentFullerene.f.p.n; i<gd.g.n; i++) {
				nbr = gd.g.Adj[i][0];
				//floater edge is the same edge
				if ((realVertexNumber(i)==realu && realVertexNumber(nbr)==realv) ||
					(realVertexNumber(i)==realv && realVertexNumber(nbr)==realu)){
					gd.gcolor.arc_color[i][0] = c;
					gd.gcolor.arc_color[nbr][gd.g.find_pos(i,nbr)] = c;
				}
			}
		}
	}

	private void colorVertex(int v, Color c) {
		//colors the given vertex, including all floaters of the same vertex
		int vertex = realVertexNumber(v); //the real vertex number to color

		//color the actual vertex (not a floater)
		gd.gcolor.vertex_color[vertex] = c;

		//now loop through the floaters and color those with the same number
		for (int i=CurrentFullerene.f.p.n; i<gd.g.n; i++)
			if (realVertexNumber(i)==vertex) {
				gd.gcolor.vertex_color[i] = c;
				pred[i] = COLOREDFLOATER;
			}
	}

	private int realVertexNumber(int v) {
		//given a floater vertex number, it looks up to see what the vertex number
		//is for the actual given floater

		if (gd.g.degree[v]!=1) return v; //make sure we are using a floater

		//the actual vertex numbers are stored in
		//CurrentFullerene.f.p

		//The basic idea is to look at what this floater is adjacent to
		//then find the corresponding position in that vertex's list
		//then look up the corresponding position in the primal list
		int nbr; //vertex that is the neighbor of floatervertex
		int posinnbr; //position of floatervertex in its neighbor
		nbr = gd.g.Adj[v][0];
		posinnbr = gd.g.find_pos(v, nbr);
		return CurrentFullerene.f.p.Adj[nbr][posinnbr];
	}
}

