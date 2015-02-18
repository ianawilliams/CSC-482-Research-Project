//*********************************************************
//Creates the display of FUIApplet
//*********************************************************

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.Timer;

public class FUIGUI extends JPanel
{
	Color ltRed = new Color(155, 255, 51);
	Color ltYellow = new Color(255, 255, 80);

	private int current = 0; //graph number of the current display
	private JTextArea summaryArea, parTextArea;
	private String number; //desired value of selected parameter type
	private int paramType; //index of selected parameter type
	private String fullereneFileName;
	private String fullerenePicFileName;

	private String headFileName, comments;
	private Parameter[] param;

	private JLabel title, statusLabel;
	private JButton selectFileButton;
	private JComboBox paramTypeCombo;
	private JTextField numberField;

	public static JButton startButton;
	public static JButton pauseButton;
	public static JComboBox algCombo;
	private JButton goButton;
	private JComboBox redrawCombo;
	private JButton plusOneButton;
	private JButton minusOneButton;
	public static FullerenePanel fullereneArea;
	/* Keeps track of status of animated algorithms. */
	static ActionListener listener;
	private static Animated animated_algorithm = new Animated();

	public static Timer timer;

	public FUIGUI(int width, int height)
	{
		fullereneFileName = null;
		number = "0";
		paramType = 0;

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(width, height));
		setBorder(BorderFactory.createEtchedBorder());

		// Create tool bars to reside at the top and bottom
		add(getTitleToolBar(), BorderLayout.NORTH);
		add(getBottomToolBar(), BorderLayout.SOUTH);

		// We will create a scrollable text area that can hold the summary information for the displayed graph
		summaryArea = new JTextArea(15, 20);
		summaryArea.setBackground(ltRed);
		summaryArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(summaryArea);
		//scrollPane.setPreferredSize(new Dimension(SUMMARY_WIDTH, SUMMARY_HEIGHT));

		// We will create a scrollable text area that can hold the specified parameter's text file
		parTextArea = new JTextArea(0, 20);
		parTextArea.setBackground(ltYellow);
		parTextArea.setEditable(false);
		JScrollPane scrollPane2 = new JScrollPane(parTextArea);
		//scrollPane2.setPreferredSize(new Dimension(SUMMARY_WIDTH, SUMMARY_HEIGHT));

		// Create a split pane that holds a text area above and the other below
		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, scrollPane2);
		jsp.setOneTouchExpandable(true);
		add(jsp, BorderLayout.CENTER);

		// Create the fullerene panel.
		// For now, we will just create a blank JPanel.
		fullereneArea = new FullerenePanel();

		/* SG: Begin change */
		//added a mouse listener to fullereneArea so that we can have point 
		//and click functionality for re-centering the graph.
		fullereneArea.addMouseListener(new FullereneMouseListener());
		/* SG: End change */

		// Create a split pane that holds a split summary area on one side and the
		//  panel to draw the fullerenes on the other.
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jsp, fullereneArea);
		sp.setOneTouchExpandable(true);
		add(sp, BorderLayout.CENTER);

		// Create a split pane that holds a main menu bar above and the panels below
		JSplitPane Totalsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getMainToolBar(), sp);
		Totalsp.setOneTouchExpandable(true);
		add(Totalsp, BorderLayout.CENTER);
	}

	private JToolBar getTitleToolBar()
	{
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);

		title = new JLabel(" ");
		tb.add(title);

		return (tb);
	}

	private JToolBar getMainToolBar()
	{
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		listener = new FUIGUIListener();

		selectFileButton = new JButton("Select File");
		selectFileButton.addActionListener(listener);

		//Initially empty
		paramTypeCombo = new JComboBox();
		paramTypeCombo.addItem(Parameter.PNAME);
		paramTypeCombo.addActionListener(listener);

		numberField = new JTextField(number, 5);
		numberField.addActionListener(listener);

		startButton = new JButton("Start");
		startButton.addActionListener(listener);
		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(listener);

		algCombo = new JComboBox();
		Animated.make_menu(algCombo);
		algCombo.addActionListener(listener);

		redrawCombo = new JComboBox();
		DrawingOption.make_menu(redrawCombo);
		redrawCombo.addActionListener(listener);

		goButton = new JButton("First one");
		goButton.addActionListener(listener);
		plusOneButton = new JButton("+1");
		plusOneButton.addActionListener(listener);
		minusOneButton = new JButton("-1");
		minusOneButton.addActionListener(listener);

		tb.add(selectFileButton);
		tb.addSeparator();
		tb.add(paramTypeCombo);
		tb.add(numberField);
		tb.addSeparator();
		tb.add(redrawCombo);
		tb.add(startButton);
		tb.add(pauseButton);
		tb.addSeparator();
		tb.add(algCombo);
		tb.addSeparator();
		tb.add(goButton);
		tb.add(plusOneButton);
		tb.add(minusOneButton);

		return (tb);
	}

	private JToolBar getBottomToolBar()
	{
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);

		statusLabel = new JLabel(" ");
		tb.add(statusLabel);

		return (tb);
	}

	private boolean checkNumberField(boolean displayError)
	{
		String tempEntry = numberField.getText();

		if (param[paramType].checkParType(tempEntry))
		{
			//need to change number to a String
			number = tempEntry;
			return (true);
		}
		else
		{
			JOptionPane.showMessageDialog(this, "Error in type of entry.", "Bad Type", JOptionPane.ERROR_MESSAGE);
			numberField.setText(number);
			return (false);
		}
	}

	private class FUIGUIListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();

			/*
			 * Take the code for handling the processing of interrupts for animated algorithms out
			 * of FUIGUI. Dec.2005.
			 */

			animated_algorithm.handle_interrupt(source);

			if (source == selectFileButton)
			{
				while (true)
				{
					String fileEntered;

					fileEntered = JOptionPane.showInputDialog(fullereneArea, "What is the name of the desired head file?",
							"Select Fullerene", JOptionPane.QUESTION_MESSAGE);
					if (fileEntered == null)
						return;
					if (!checkFile(fileEntered))
						return;
					headFileName = fileEntered;

					//Initialize components and panels
					initialize();

					readHeadFile(headFileName);
					CurrentFullerene.get_fullerene(fullereneFileName, fullerenePicFileName, current);
					if (fullereneFileName == null)
						break;

					title.setText(comments);
					Parameter.display_summary(param, summaryArea, current);
					numberField.setText(Integer.toString(current));

					break;
				}
			}
			else if (source == numberField)
			{
				checkNumberField(true);
			}
			else if (source == paramTypeCombo)
			{
				paramType = paramTypeCombo.getSelectedIndex();

				//Display the Parameter summary file based on the selected one
				parTextArea.setText("");
				Parameter.display_tfile(param, parTextArea, paramType);
			}

			else if (source == goButton)
			{
				boolean badInput = false;

				badInput = !checkNumberField(!badInput) || badInput;
				if (badInput)
					return;

				/*
				 * If the selected parameter is the default: Graph #, then we can simply update to
				 * that graph. Otherwise we have to find the graph # of the one that's first
				 * occurence satisfies the requested parameter value.
				 */
				try
				{
					if (paramType == Parameter.FULLERENE_NUMBER)
						current = Integer.parseInt(number);
					else
						current = Pentagon.next_fullerene(true, 0, paramType, number, param[paramType]);
				}
				catch (NumberFormatException exception)
				{
					JOptionPane.showMessageDialog(fullereneArea, "Error in type of entry.", "Bad Type", JOptionPane.ERROR_MESSAGE);
					numberField.setText(number);
				}
				CurrentFullerene.get_fullerene(fullereneFileName, fullerenePicFileName, current);
				numberField.setText(number);
				summaryArea.setText("");
				Parameter.display_summary(param, summaryArea, current);
				statusLabel.setText("Make request for type=" + paramType + " number=" + number);
			}
			//Get the next graph (in graph# order) with the chosen parameter value
			else if (source == plusOneButton)
			{
				boolean badInput = false;

				badInput = !checkNumberField(!badInput) || badInput;
				if (badInput)
					return;

				current = Pentagon.next_fullerene(true, current, paramType, number, param[paramType]);
				CurrentFullerene.get_fullerene(fullereneFileName, fullerenePicFileName, current);
				if (paramType == Parameter.FULLERENE_NUMBER)
					number = Integer.toString(current);
				numberField.setText(number);
				numberField.setText(number);
				summaryArea.setText("");
				Parameter.display_summary(param, summaryArea, current);
				statusLabel.setText("Make request for type=" + paramType + " number=" + number);

			}

			//Get the previous graph (in order of graph#)  with the chosen parameter value
			else if (source == minusOneButton)
			{
				boolean badInput = false;

				badInput = !checkNumberField(!badInput) || badInput;
				if (badInput)
					return;

				current = Pentagon.next_fullerene(false, current, paramType, number, param[paramType]);
				CurrentFullerene.get_fullerene(fullereneFileName, fullerenePicFileName, current);
				if (paramType == Parameter.FULLERENE_NUMBER)
					number = Integer.toString(current);
				numberField.setText(number);
				summaryArea.setText("");
				Parameter.display_summary(param, summaryArea, current);
				statusLabel.setText("Make request for type=" + paramType + " number=" + number);
			}
			/* begin WJM */
			else if (source == redrawCombo)
			{
				DrawingOption.update_default(redrawCombo.getSelectedIndex());
				CurrentFullerene.picture.draw_fullerene();

				/*
				 * CurrentFullerene.get_fullerene(fullereneFileName, current);
				 */
			}
			/* end WJM */
			repaint();
		}

		//catch bad or non-existent user specified files (return false)
		public boolean checkFile(String FileName)
		{
			File file = new File(FileName);
			if (!file.exists())
			{
				JOptionPane.showMessageDialog(fullereneArea, "Cannot find the file: " + FileName, "Cannot Find File",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			return true;
		}

		//check that parameter type is legitimate, case insensitive (return false)
		public boolean checkParType(String type)
		{
			if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("float") || type.equalsIgnoreCase("double")
					|| type.equalsIgnoreCase("byte") || type.equalsIgnoreCase("short") || type.equalsIgnoreCase("long")
					|| type.equalsIgnoreCase("String") || type.equalsIgnoreCase("char") || type.equalsIgnoreCase("boolean")
					|| type.equalsIgnoreCase("pic"))
				return true;
			else
			{
				JOptionPane.showMessageDialog(fullereneArea, "No such parameter type: " + type, "Type Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		//clear added things to jcombobox, clear textpanels
		public void initialize()
		{
			//clear jcombobox
			while (paramTypeCombo.getItemCount() > 1)
			{
				paramTypeCombo.removeItemAt(1);
			}

			title.setText("");

			summaryArea.setText("");
			summaryArea.setCaretPosition(0);

			parTextArea.setText("");
			parTextArea.setCaretPosition(0);

			current = 1; //initial graph number
		}

		public void badParMessage()
		{
			JOptionPane.showMessageDialog(fullereneArea, "Error in parameter information; " + "some parameters may not be in view.",
					"Error in Parameter Information", JOptionPane.ERROR_MESSAGE);
		}

		public void readHeadFile(String file)
		{
			String parName, parType, parVFile, temp;
			int i, numParams = 0;

			try
			{
				fullereneFileName = null;
				fullerenePicFileName = null;

				FileReader fr = new FileReader(file);
				BufferedReader infile = new BufferedReader(fr);

				String line = infile.readLine();
				StringTokenizer tokenizer = new StringTokenizer(line);

				//Read title of this set of fullerene graphs
				while (!tokenizer.hasMoreElements())
				{
					line = infile.readLine();
					if (line == null)
					{
						return;
					}
					tokenizer = new StringTokenizer(line);
				}
				temp = tokenizer.nextToken("\"").trim();
				while (temp.length() == 0)
				{
					while (!tokenizer.hasMoreElements())
					{
						line = infile.readLine();
						if (line == null)
						{
							return;
						}
						tokenizer = new StringTokenizer(line);
					}
					temp = tokenizer.nextToken("\"").trim();
				}
				comments = temp;
				if (tokenizer.hasMoreElements())
					tokenizer.nextToken(" \t\n\r\f");

				//Read fullerene file name
				while (!tokenizer.hasMoreElements())
				{
					line = infile.readLine();
					if (line == null)
					{
						return;
					}
					tokenizer = new StringTokenizer(line);
				}
				temp = tokenizer.nextToken();
				if (!checkFile(temp))
				{
					return;
				}
				fullereneFileName = temp;

				//Read number of parameters
				try
				{
					while (!tokenizer.hasMoreElements())
					{
						line = infile.readLine();
						if (line == null)
							throw new NumberFormatException();
						tokenizer = new StringTokenizer(line);
					}
					numParams = Integer.parseInt(tokenizer.nextToken());
				}
				catch (NumberFormatException ex)
				{
					JOptionPane.showMessageDialog(fullereneArea, "Invalid number of paramaters; running default mode.",
							"Error in number of parameters!", JOptionPane.ERROR_MESSAGE);
					numParams = 0;
				}

				ArrayList<Parameter> parsedParams = new ArrayList<Parameter>(numParams);

				//Read parameter info that's in the headfile
				try
				{
					for (i = 0; i < numParams; i++)
					{
						boolean keepParam = true;

						//get the parameter name
						while (!tokenizer.hasMoreElements())
						{
							line = infile.readLine();
							if (line == null)
								throw new ParameterException();
							tokenizer = new StringTokenizer(line);
						}
						temp = tokenizer.nextToken("\"").trim();
						while (temp.length() == 0)
						{
							while (!tokenizer.hasMoreElements())
							{
								line = infile.readLine();
								if (line == null)
									throw new ParameterException();
								tokenizer = new StringTokenizer(line);
							}
							temp = tokenizer.nextToken("\"").trim();
						}
						parName = temp;
						if (tokenizer.hasMoreElements())
							tokenizer.nextToken(" \t\n\r\f");

						//Get the Parameter type
						while (!tokenizer.hasMoreElements())
						{
							line = infile.readLine();
							if (line == null)
								throw new ParameterException();
							tokenizer = new StringTokenizer(line);
						}
						parType = tokenizer.nextToken();
						if (!checkParType(parType))
							throw new ParameterException();

						//Get the parameter values file
						while (!tokenizer.hasMoreElements())
						{
							line = infile.readLine();
							if (line == null)
								throw new ParameterException();
							tokenizer = new StringTokenizer(line);
						}
						parVFile = tokenizer.nextToken();
						if (!checkFile(parVFile))
							throw new ParameterException();

						if (parType.equalsIgnoreCase("pic"))
						{
							fullerenePicFileName = parVFile;
							keepParam = false;
						}

						//if no text file is desired then replace fileName with keyword "null"
						//Get the parameter text file
						while (!tokenizer.hasMoreElements())
						{
							line = infile.readLine();
							if (line == null)
							{
								if (keepParam)
									parsedParams.add(new Parameter(parName, parType, parVFile, null));
								throw new ParameterException();
							}
							tokenizer = new StringTokenizer(line);
						}
						String parTFile = tokenizer.nextToken();
						if (!parTFile.equalsIgnoreCase("null"))
						{
							if (!checkFile(parTFile))
								parTFile = null;
						}
						else
							parTFile = null;

						if (keepParam)
							parsedParams.add(new Parameter(parName, parType, parVFile, parTFile));
					}
				}
				catch (ParameterException e)
				{
					badParMessage();
				}

				/*
				 * Process each parameter that we saw until we either had a problem or consumed them
				 * all.
				 */

				for (Parameter parm : parsedParams)
					paramTypeCombo.addItem(parm.name);

				param = Parameter.initParamArray(parsedParams);

				infile.close();
			}
			catch (FileNotFoundException exception)
			{
				JOptionPane.showMessageDialog(fullereneArea, "Cannot find the file: " + headFileName, "Cannot Find File",
						JOptionPane.ERROR_MESSAGE);
				fullereneFileName = null;
				return;
			}
			catch (IOException exception)
			{
				JOptionPane.showMessageDialog(fullereneArea, "Error in headfile, check that format is correct", "Error",
						JOptionPane.ERROR_MESSAGE);
				fullereneFileName = null;
				return;
			}
		}
	}

	private Frame findParentFrame()
	{
		Component c = getParent();
		while (true)
		{
			if (c instanceof Frame)
			{
				return ((Frame) c);
			}
			else
			{
				c = c.getParent();
			}
		}
	}

	private class ParameterException extends Exception
	{
	}
}
