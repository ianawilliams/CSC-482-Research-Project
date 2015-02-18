public class Pause
{
	public static void sleep(int n_millisecond)
	{
		try
		{
			Thread.sleep(n_millisecond);
		}
		catch (InterruptedException e)
		{
		}
	}
}
