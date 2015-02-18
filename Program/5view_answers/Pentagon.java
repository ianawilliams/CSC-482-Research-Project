/* This class supports a representation of a fullerene which
   consists of the 12 positions of the pentagons in a face
   spiral of the fullerene where the positions are numbered
   starting with 0. As input, these should be entered as

   n p0 p1 ... p11
   where n is the number of vertices and p0..p11 are the pentagon positions.
   Each graph has a number which is its position in the input file
   and that value is num. The graphs are numbered starting with 1
   to correspond with chemists usual conventions in numbering
   the isomers.

   Creator: Wendy Myrvold
   Last Update: Oct. 2005

 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Pentagon
{
	int n;
	int num;
	int[] pentagon_position;

	public Pentagon()
	{
		pentagon_position = new int[12];
	}

	/*
	 * This will only give something reasonable if the dual vertices correspond to the faces and are
	 * in order of the face spiral (true initially).
	 */
	public Pentagon(Fullerene f)
	{
		int i, pos;
		pos = 0;

		n = f.p.n;
		pentagon_position = new int[12];

		for (i = 0; i < f.d.n; i++)
		{
			if (f.d.degree[i] == 5)
			{
				pentagon_position[pos] = i;
				pos++;
			}
		}
	}

	// Read in the pentagon representation of a fullerene from the 
	// current position of the input file.

	public static Pentagon read_pentagon(int my_num, BufferedReader infile)
	{
		Pentagon pent;
		int i, n;
		StringTokenizer tokenizer;
		String line;

		try
		{
			do
			{
				line = infile.readLine();
				if (line == null)
					return (null);
				tokenizer = new StringTokenizer(line);
			} while (!tokenizer.hasMoreElements());

			/*
			 * The first entry on any line should be the number of vertices.
			 */

			n = Integer.parseInt(tokenizer.nextToken());
			pent = new Pentagon();
			pent.n = n;
			pent.num = my_num;

			for (i = 0; i < 12; i++)
			{
				while (!tokenizer.hasMoreElements())
				{
					line = infile.readLine();
					if (line == null)
						return (null);
					tokenizer = new StringTokenizer(line);
				}
				pent.pentagon_position[i] = Integer.parseInt(tokenizer.nextToken());
			}
			return (pent);
		}
		catch (IOException exception)
		{
			return (null);
		}
		catch (NumberFormatException exception)
		{
			return (null);
		}
	}

	public String toString()
	{
		int i;
		String s;
		s = "" + n;
		for (i = 0; i < 12; i++)
			s += " " + pentagon_position[i];
		return (s);
	}

	public Fullerene make_fullerene(int num, PictureInfo pictureInfo)
	{
		Fullerene f;

		Spiral sp = new Spiral(this);
		f = sp.make_fullerene(num, pictureInfo);
		return (f);
	}

	// Reads the n_desired'th line of the fullerene file fname 
	// and then make the fullerene from it.

	public static Fullerene get_fullerene(String fname, String picFileName, int n_desired)
	{
		int num;
		Fullerene f;
		Pentagon pent;

		num = 0;
		try
		{
			FileReader fr = new FileReader(fname);
			BufferedReader infile = new BufferedReader(fr);
			do
			{
				pent = Pentagon.read_pentagon(num + 1, infile);
				if (pent != null)
				{
					num++;
					if (num == n_desired)
					{
						f = pent.make_fullerene(num, PictureInfo.getInfo(picFileName, n_desired));
						infile.close();
						return (f);
					}
				}
			} while (pent != null);
			infile.close();
			return (null);
		}
		catch (FileNotFoundException exception)
		{
			System.out.println("File not found.");
			return (null);
		}
		catch (IOException exception)
		{
			System.out.println("Bad input.");
			return (null);
		}
	}

	// Returns the graph number of the next/previous 
	// fullerene with given desired parameter
	// by reading the selected parameter's value file.

	/*
	 * Updated by Marsha Minchenko in Summer 2005 with a switch to plug and play parameters.
	 */

	/*
	 * plus- true if next one or false if the previous one is desired. current- the graph number of
	 * the current fullerene. parameter_num- index of selected parameter. desired_parameter_value-
	 * value for graphs of interest which_par- the selected parameter.
	 */
	public static int next_fullerene(boolean plus, int current, int parameter_num, String desired_parameter_value, Parameter which_par)
	{
		String vfname = which_par.vfileName;

		int num;

		/* just go back or forwards one. */

		if (parameter_num == Parameter.FULLERENE_NUMBER)
		{
			if (plus)
				return (current + 1);
			else
				return (current - 1);
		}

		try
		{
			FileReader fr = new FileReader(vfname);
			BufferedReader infile = new BufferedReader(fr);
			if (plus)
			{
				num = which_par.get_next_line_num(infile, current, desired_parameter_value);
			}
			else
			{
				num = which_par.get_prev_line_num(infile, current, desired_parameter_value);
			}
			infile.close();
			return num;
		}
		catch (FileNotFoundException exception)
		{
			System.out.println("File not found.");
			return (0);
		}
		catch (IOException exception)
		{
			System.out.println("Bad input.");
			return (0);
		}
	}
}
