/*   Creator: Marsha Minchenko
 Last Update: Oct. 2005

 This class takes care of the presentation and manipulation
 of the parameters associated with a fullerene as specified
 in the header file.

 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JTextArea;

public class Parameter
{
	final static String PNAME = "Number of graph";
	final static int FULLERENE_NUMBER = 0;
	static int max = 1;
	String name, type, vfileName, tfileName;

	public Parameter(String nm, String tp)
	{
		name = nm;
		type = tp;
		vfileName = null;
		tfileName = null;
	}

	public Parameter(String nm, String typ, String vfname, String tfname)
	{
		name = nm;
		type = typ;
		vfileName = vfname;
		tfileName = tfname;
	}

	//For making copies 
	public Parameter(Parameter p)
	{
		name = p.name;
		type = p.type;
		vfileName = p.vfileName;
		tfileName = p.tfileName;
	}

	public static Parameter[] initParamArray(int newMax)
	{
		Parameter[] parArray;
		max = 1 + newMax;

		parArray = new Parameter[max];
		parArray[FULLERENE_NUMBER] = new Parameter(PNAME, "int");
		return parArray;
	}

	public static Parameter[] initParamArray(List<Parameter> pParams)
	{
		max = 1 + pParams.size();
		Parameter[] parArray = new Parameter[max];
		parArray[FULLERENE_NUMBER] = new Parameter(PNAME, "int");
		for (int i = 0; i < pParams.size(); i++)
			parArray[i + 1] = pParams.get(i);
		return parArray;
	}

	public static String itos(int n)
	{
		String s;

		s = "" + n;
		while (s.length() < 3)
		{
			s = " " + s;
		}
		return (s);
	}

	//Checks that an entry is truly of its specified parameter type.
	//Returns false if the entry is the wrong type or there is no such type.
	public boolean checkParType(String entry)
	{
		try
		{
			if (type.equalsIgnoreCase("int"))
				Integer.parseInt(entry);
			else if (type.equalsIgnoreCase("float"))
				Float.valueOf(entry);
			else if (type.equalsIgnoreCase("double"))
				Double.valueOf(entry);
			else if (type.equalsIgnoreCase("byte"))
				Byte.valueOf(entry);
			else if (type.equalsIgnoreCase("short"))
				Short.valueOf(entry);
			else if (type.equalsIgnoreCase("long"))
				Long.valueOf(entry);
			else if (type.equalsIgnoreCase("String"))
				return (true);
			else if (type.equalsIgnoreCase("char"))
				return (entry.length() == 1);
			else if (type.equalsIgnoreCase("boolean"))
			{
				if (!Boolean.getBoolean(entry))
					return (type.equalsIgnoreCase("false"));
			}
			else
				return (false);
		}
		catch (NumberFormatException exception)
		{
			return (false);
		}
		return (true);
	}

	//  Finds the next graph number for which the 
	//  fullerene has parameter value desired_par_val. 

	public int get_next_line_num(BufferedReader infile, int current, String desired_par_val)
	{
		int num = 0;
		String line;
		StringTokenizer tokenizer;

		try
		{
			do
			{
				num++;
				line = infile.readLine();
				if (line == null)
					return num;
			} while (num <= current);
			// On completion, line contains the next fullerene's parameter value

			do
			{
				tokenizer = new StringTokenizer(line);
				if (desired_par_val.equalsIgnoreCase(tokenizer.nextToken()))
					return num;
				line = infile.readLine();
				num++;
			} while (line != null);

			return num;
		}
		catch (IOException exception)
		{
			return (0);
		}
	}

	//  Finds the most previous graph number for which the 
	//  fullerene has parameter value desired_par_val. 
	public int get_prev_line_num(BufferedReader infile, int current, String desired_par_val)
	{
		int num, max_num = 0;
		String line;
		StringTokenizer tokenizer;

		try
		{
			for (num = 1; num < current; num++)
			{
				line = infile.readLine();
				tokenizer = new StringTokenizer(line);
				if (desired_par_val.equalsIgnoreCase(tokenizer.nextToken()))
					max_num = num;
			}
			return max_num;
		}
		catch (IOException exception)
		{
			return (0);
		}
	}

	//Gives info on each graph based on parameter array values.
	public static void display_summary(Parameter[] summ, JTextArea ta, int graphNum)
	{
		int i;

		ta.append(summ[0].name + " : " + itos(graphNum));

		for (i = 1; i < max; i++)
		{
			ta.append("\n");
			ta.append(summ[i].name + " : " + summ[i].read_summary(graphNum));
		}
	}

	//  Returns the parameter on line vline which coresponds 
	//  to the fullerene with graph number = vline

	public String read_summary(int vline)
	{
		int i = 0;
		StringTokenizer tokenizer;
		String line, value;

		try
		{
			FileReader fr = new FileReader(vfileName);
			BufferedReader inF = new BufferedReader(fr);

			do
			{
				line = inF.readLine();
				if (line == null)
					return " ";
				i++;
			} while (i < vline);

			tokenizer = new StringTokenizer(line);
			value = tokenizer.nextToken();
			inF.close();
			return (value);
		}
		catch (FileNotFoundException exception)
		{
			return (null);
		}
		catch (IOException exception)
		{
			return (null);
		}
	}

	//Outputs a text file into the specified panel
	public static void display_tfile(Parameter[] summ, JTextArea ta, int parNum)
	{
		Parameter parchosen = summ[parNum];

		if (parNum == FULLERENE_NUMBER || parchosen.tfileName == null)
			return;

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(parchosen.tfileName));
			String line;
			while ((line = in.readLine()) != null)
			{
				ta.append(line);
				ta.append("\n");
			}
			in.close();
		}
		catch (FileNotFoundException ex)
		{
			return;
		}
		catch (IOException e)
		{
			return;
		}
	}
}
