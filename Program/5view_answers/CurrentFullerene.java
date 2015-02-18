//Panel containing the graph representations of the fullerenes

public class CurrentFullerene
{
//	public static int n_desired;
//	public String fullereneFileName;
//	public String number; //desired value of selected parameter type
//	public int paramType; //index of selected parameter type
//	private String parTFile, headFileName, comments;
//	private Parameter[] param;

	public static Fullerene f;
	public static FullerenePicture picture;

	public static Fullerene get_fullerene(String fullereneFileName, String fullerenePicFile, int n_desired)
	{
		f = null;

		if (fullereneFileName != null)
		{
			if (n_desired <= 0)
			{
				FullerenePanel.set_message("No Previous Fullerene.");
			}
			else
			{
				f = Pentagon.get_fullerene(fullereneFileName, fullerenePicFile, n_desired);
				if (f != null)
				{
					picture = new FullerenePicture(f);
					picture.draw_fullerene();
				}
				else
				{
					FullerenePanel.set_message("Fullerene " + n_desired + " does not exist.");
				}
			}
		}
		else
		{
			FullerenePanel.set_message("No Input File Selected.");
		}
		return (f);
	}
}
