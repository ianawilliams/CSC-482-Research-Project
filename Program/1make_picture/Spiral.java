/* A face spiral sequence has one entry for each face of the
   fullerene and these are 5 or 6 depending on the face size
   in the given face spiral. The Pentagon representation is
   converted to this before creating a fullerene as it
   is a bit easier to work with when constructing the fullerene.

   Creator: Wendy Myrvold
   Last Update: Sept. 9, 2005
 */
public class Spiral
{

	int n; // number of vertices of the fullerene.
	int nf; // number of faces of the fullerene.
	int[] spiral_sequence; // Sequence of 5's and 6's of length nf.

	public Spiral(Pentagon pent)
	{
		int i;

		n = pent.n;
		nf = n / 2 + 2;
		spiral_sequence = new int[nf];

		for (i = 0; i < nf; i++)
		{
			spiral_sequence[i] = 6;
		}
		for (i = 0; i < 12; i++)
		{
			spiral_sequence[pent.pentagon_position[i]] = 5;
		}
	}

	public String toString()
	{
		int i;
		String s;
		s = n + " ";
		for (i = 0; i < nf; i++)
			s += spiral_sequence[i];
		return (s);
	}

	/*
	 * Make a fullerene by adding appropriately sized faces one at a time. num is the fullerene
	 * number, it's position in the input file. Maybe should be updated at some point to handle
	 * errors in a more appropriate manner. Currently it just returns an incomplete fullerene when
	 * an error is encountered.
	 */

	public Fullerene make_fullerene(int num, PictureInfo pictureInfo)
	{
		Fullerene f;
		int i;
		int pcount;

		/* First ensure there are 12 pentagons. */

		pcount = 0;
		for (i = 0; i < nf; i++)
			if (spiral_sequence[i] == 5)
				pcount++;
		if (pcount != 12)
			return (null);
		f = new Fullerene(num, this, pictureInfo);
		return (f);
	}
}
