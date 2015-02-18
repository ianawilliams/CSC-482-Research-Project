
/*  Creator: Wendy Myrvold
 Last Update: Oct. 2005

 A fullerene consists of a primal 3-regular graph
 which has face sizes of 5 and six only, its dual
 graph, and also a list of its faces.
 These are given by:
 p- rotation system of primal graph.
 d- rotation system of dual graph.
 faces- list of faces (stored in an Adjlist although
 it is not in this case an adjacency list).
 It also has a graph_number which corresponds to its
 position in the current input file. 

 */
public class Fullerene
{
	AdjList p;
	AdjList d;
	AdjList faces;
	int graph_number;
	PictureInfo mPictureInfo;

	public Fullerene()
	{
	}
        public Fullerene(int num, Spiral sp)
        {
                int i;

                graph_number= num;

                p= new AdjList(sp.n);
                for (i=0; i < sp.n; i++)
                {
                        p.add_vertex(i, 3);
                }

                d= new AdjList(sp.nf);
                for (i=0; i < sp.nf; i++)
                {
                        d.add_vertex(i, sp.spiral_sequence[i]);
                }

                faces= new AdjList(sp.nf);
                for (i=0; i < sp.nf; i++)
                {
                        faces.add_vertex(i, sp.spiral_sequence[i]);
                }
                get_fullerene(sp);
                /*
                System.out.println("The primal:");
                p.printUpper();
                System.out.println("The dual:");
                d.printUpper();
                */
                /*
                if (num !=1)
                */
                FindGroup group= new FindGroup(p);
                /*
                group.printGroup();
                */
        }

	public Fullerene(int num, Spiral sp, PictureInfo pictureInfo)
	{
		int i;

		graph_number = num;
		mPictureInfo = pictureInfo;

		p = new AdjList(sp.n);
		for (i = 0; i < sp.n; i++)
		{
			p.add_vertex(i, 3);
		}

		d = new AdjList(sp.nf);
		for (i = 0; i < sp.nf; i++)
		{
			d.add_vertex(i, sp.spiral_sequence[i]);
		}

		faces = new AdjList(sp.nf);
		for (i = 0; i < sp.nf; i++)
		{
			faces.add_vertex(i, sp.spiral_sequence[i]);
		}
		get_fullerene(sp);
//		System.out.println("The primal:");
//		p.printUpper();
//		System.out.println("The dual:");
//		d.printUpper();
		/*
		 * if (num !=1)
		 */
		FindGroup group= new FindGroup(p);
		/*
		 * group.printGroup();
		 */
	}

	public Fullerene copyof()
	{
		Fullerene f;

		f = new Fullerene();

		f.graph_number = graph_number;
		f.p = p.copyof();
		f.d = d.copyof();
		f.faces = faces.copyof();
		return (f);
	}

	private void get_fullerene(Spiral sp)
	{
		AdjList arc_info;
		int i;
		int nv;

		Queue q;
		q = new Queue(sp.n);

		arc_info = p.copyof();

		init_face(arc_info, q, sp.spiral_sequence[0]);
		nv = sp.spiral_sequence[0];
		for (i = 1; i < sp.nf - 2; i++)
		{
			nv += add_face(nv, false, arc_info, q, i, sp.spiral_sequence[i]);
		}
		add_face(nv, true, arc_info, q, sp.nf - 2, sp.spiral_sequence[i]);
	}

	private void init_face(AdjList arc_info, Queue q, int init_face_size)
	{
		int prev;
		int nv;

		for (nv = 0; nv < init_face_size; nv++)
		{
			q.add_rear(nv);
			faces.Adj[0][nv] = nv;
		}
		prev = init_face_size - 1;
		for (nv = 0; nv < init_face_size; nv++)
		{
			cw_arc(arc_info, 0, nv, 2, prev, 0, nv);
			prev = (prev + 1) % init_face_size;
		}
	}

	private int add_face(int nv, boolean done, AdjList arc_info, Queue q, int new_face_num, int face_size)
	{
		int start;
		int end;
		int count, n_extra;
		int prev, current, next;
		int j;

		start = q.del_rear();
		end = q.del_front();

		/*
		 * Walk from the end vertex to the start vertex to find the number of vertices already
		 * inside the current face.
		 */

		count = 1;
		prev = end;
		current = p.Adj[end][0];
		faces.Adj[new_face_num][face_size - 1] = end;
		do
		{
			faces.Adj[new_face_num][count - 1] = current;
			if (count <= face_size)
				ccw_arc(arc_info, prev, current, new_face_num, count - 1);
			count++;
			j = p.find_pos(prev, current);
			j = (j + 2) % 3;
			next = p.Adj[current][j];
			prev = current;
			current = next;

		} while (prev != start);

		/* If no additional vertices needed, close the face. */

		if (count == face_size)
		{
			cw_arc(arc_info, new_face_num, count - 1, 1, start, 1, end);
			if (done)
				add_last_face(end, start, arc_info);
			return (0);
		}

		/* Add n_extra new vertices. */

		n_extra = face_size - count;

		cw_arc(arc_info, new_face_num, count - 1, 1, start, 0, nv);
		q.add_rear(nv);
		faces.Adj[new_face_num][count - 1] = nv;
		nv++;
		count++;

		while (count < face_size)
		{
			cw_arc(arc_info, new_face_num, count - 1, 2, nv - 1, 0, nv);
			q.add_rear(nv);
			faces.Adj[new_face_num][count - 1] = nv;
			nv++;
			count++;
		}

		/* Add the last edge. */

		cw_arc(arc_info, new_face_num, count - 1, 2, nv - 1, 1, end);
		if (done)
			add_last_face(end, start, arc_info);

		return (n_extra);
	}

	private void add_last_face(int last_u, int last_v, AdjList arc_info)
	{
		int last;

		int count, prev, current, next;
		int start, end;
		int j;

		last = d.n - 1;

		start = last_u;
		end = last_v;
		faces.Adj[last][0] = start;

		count = 1;
		prev = end;
		current = p.Adj[end][0];
		do
		{
			faces.Adj[last][count] = prev;
			ccw_arc(arc_info, prev, current, last, count - 1);
			count++;
			j = p.find_pos(prev, current);
			j = (j + 2) % 3;
			next = p.Adj[current][j];
			prev = current;
			current = next;

		} while (prev != start);
		ccw_arc(arc_info, start, end, last, count - 1);
	}

	private void cw_arc(AdjList arc_info, int fnum, int pos, int posu, int u, int posv, int v)
	{
		arc_info.Adj[u][posu] = pos;
		arc_info.Adj[v][posv] = fnum;
		p.add_edge(posu, u, posv, v);
	}

	private void ccw_arc(AdjList arc_info, int u, int v, int new_num, int new_pos)
	{
		int fnum, pos;
		int posu, posv;

		posu = p.find_pos(v, u);
		posv = p.find_pos(u, v);
		fnum = arc_info.Adj[u][posu];
		pos = arc_info.Adj[v][posv];
		d.add_edge(new_pos, new_num, pos, fnum);
	}

	public boolean hasPictureInfo()
	{
		return mPictureInfo != null;
	}

	public int getFaceSize()
	{
		return mPictureInfo.getFaceSize();
	}

	public int[] getFace()
	{
		int[] result = null;

		switch (mPictureInfo.getType())
		{
		case VERTEX:
			result = new int[1];
			result[0] = mPictureInfo.getVertex();
			break;
		case EDGE:
			result = new int[2];
			result[0] = mPictureInfo.getEdge1();
			result[1] = mPictureInfo.getEdge2();
			break;
		case FACE:
			int faceNumber = mPictureInfo.getFaceNumber();
			int size = faces.degree[faceNumber];
			result = new int[size];
			for (int i = 0; i < size; i++)
				result[i] = faces.Adj[faceNumber][i];
			break;
		}

		return result;
	}
}
