public class XY
{
	double x;
	double y;

	public XY()
	{
		x = 0;
		y = 0;
	}

	public XY(double xv, double yv)
	{
		x = xv;
		y = yv;
	}

	public void between_fix(XY u, XY v)
	{
		boolean ok;

		ok = false;

		if (u.x <= v.x)
		{
			if (x >= u.x && x <= v.x)
				ok = true;
		}
		else
		{
			if (x <= u.x && x >= v.x)
				ok = true;
		}
		if (ok)
		{
			if (u.y <= v.y)
			{
				if (y >= u.y && y <= v.y)
					ok = true;
			}
			else
			{
				if (y <= u.y && y >= v.y)
					ok = true;
			}
		}
		if (!ok)
		{
			if (u.x <= v.x)
			{
				if (x <= u.x)
				{
					x = u.x;
					y = u.y;
				}
				else
				{
					x = v.x;
					y = v.y;
				}
			}
			else
			{
				if (x <= v.x)
				{
					x = v.x;
					y = v.y;
				}
				else
				{
					x = u.x;
					y = u.y;
				}
			}
		}
	}

	public void print(String s)
	{
		System.out.println(s + " x= " + x + " y= " + y);
	}
}
