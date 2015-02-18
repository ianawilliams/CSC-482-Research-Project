import java.awt.Color;

public class ColorVertices
{
    int i, c;

    static Color [] new_color = {
		Color.blue,
		Color.black,
		Color.yellow};
    GraphDrawing gd;

    public ColorVertices(GraphDrawing x)
    {
	    i=0; c=0;
	    gd= x;
    }
    public void next_step()
    {
	 int u;
	 int pos;

         gd.gcolor.vertex_color[i]= new_color[c];
	 if (c < gd.g.degree[i])
	 {
             gd.gcolor.arc_color[i][c]= new_color[c];
	     u= gd.g.Adj[i][c];
	     pos= gd.g.find_pos(i, u);
	     if ( pos >= 0 && pos < gd.g.degree[c])
                 gd.gcolor.arc_color[u][pos]= new_color[c];
	 }

	 FUIGUI.fullereneArea.new_picture(gd);
	 i++;
	 if (i == gd.g.n || gd.g.degree[i] < 3)
	 {
		 i=0;
		 c++;
		 c%= 3;
	 }
    }
}

