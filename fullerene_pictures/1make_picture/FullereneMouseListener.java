/**
 * This MouseListener handles clicks inside the fullereneArea
 * 
 * @author sgirn
 */
public class FullereneMouseListener extends java.awt.event.MouseAdapter
{
	public void mouseClicked(java.awt.event.MouseEvent e)
	{
		Object object = e.getSource();
		if (object instanceof FullerenePanel && DrawingOption.click_option)
		{
			SpecifiedCenter.centerPictureOnClick(DrawingOption.default_option.click_center_option, e.getX(), e.getY());
		}
	}
}
