import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class PictureInfo
{
	public enum CenterType
	{
		VERTEX, EDGE, FACE
	}

	private CenterType mType;
	private int mValue1;
	private int mValue2;
	private int mFaceSize;

	public static PictureInfo getInfo(String pFilename, int pDesired)
	{
		if (pFilename == null)
			return null;
		
		PictureInfo result = new PictureInfo();
		Scanner in = null;

		try
		{
			in = new Scanner(new File(pFilename));
			String pattern = in.delimiter().toString();

			for (int i = 0; i < pDesired; i++)
			{
				result.readOneRecord(in);
			}
			return result;
		}
		catch (FileNotFoundException e)
		{
		}
		catch (InputMismatchException e)
		{
		}
		catch (NoSuchElementException e)
		{
		}
		catch (IllegalStateException e)
		{
		}
		finally
		{
			if (in != null)
				in.close();
		}

		// Display some sort of error dialog here?
		return null;
	}

	private void readOneRecord(Scanner in) throws InputMismatchException, NoSuchElementException, IllegalStateException
	{
		mFaceSize = in.nextInt();
		switch (mFaceSize)
		{
		case 1:
			mType = CenterType.VERTEX;
			mValue1 = in.nextInt();
			break;
		case 2:
			mType = CenterType.EDGE;
			mValue1 = in.nextInt();
			mValue2 = in.nextInt();
			break;
		case 5:
		case 6:
			mType = CenterType.FACE;
			mValue1 = in.nextInt();
			break;
		default:
			throw new InputMismatchException();
		}
	}

	public int getFaceSize()
	{
		return mFaceSize;
	}

	public CenterType getType()
	{
		return mType;
	}

	public int getVertex()
	{
		return mValue1;
	}

	public int getEdge1()
	{
		return mValue1;
	}

	public int getEdge2()
	{
		return mValue2;
	}

	public int getFaceNumber()
	{
		return mValue1;
	}

}
