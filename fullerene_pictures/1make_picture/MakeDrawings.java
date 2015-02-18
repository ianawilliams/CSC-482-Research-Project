import java.util.*;
public class MakeDrawings
{
   public static boolean debug= false;

   public static void main(String []  Args)
   {
      Pentagon pent;
      Spiral sp;
      Fullerene f;
      FaceCenter face_picture;
      Embedding embed;

      int i;

      Scanner sc = new Scanner(System.in);

      pent = new Pentagon();
      pent.num=0;

      while (sc.hasNextInt()) 
      {
          // Read in the face spiral.
          pent.n = sc.nextInt();
          pent.num++;

          for (i=0; i < 12; i++)
          {
              pent.pentagon_position[i] = sc.nextInt();
          } 

          if (debug)
          {
              System.out.println("Fullerene number " + pent.num);

              System.out.print("Pentagon positions: ");
              printVector(pent.pentagon_position);
          }

          sp= new Spiral(pent);
         
          if (debug)
          {
             System.out.print("Face spiral sequence: ");
             printVector(sp.spiral_sequence);
   
             System.out.println("Number of Vertices: " + sp.n);
             System.out.println("Number of Faces: " + sp.nf);
          }
 
          f= new Fullerene(pent.num, sp);

          if (debug)
          {
              f.p.print_graph("The primal graph");
              f.d.print_graph("The dual graph");
              f.faces.print_graph("The faces are:");
          }
          
          // Draw each of the face centered pictures.

          for (i=0; i < sp.nf; i++) 
          {
             face_picture= new FaceCenter(f.p);

             if (debug)
             {
                 System.out.println("The degree of face " + i + ":" + f.d.degree[i]);
                 System.out.print("The face is: ");
                 printVector(f.faces.Adj[i]);
             }

             face_picture.redraw(f.d.degree[i], f.faces.Adj[i], f);

             if (debug)
             {
                 System.out.print("The levels:");
                 printVector(face_picture.level);
                 System.out.print("The angles:");
                 printVector(face_picture.angle);
             }
             embed= new Embedding(face_picture);
             if (debug)
             {
                 embed.print("Embedding for Fullerene " + pent.num  + " Face " + i + ":");
             }
             printPicture(embed, f);
          }
      }
   }
   public static void printVector(int [] v)
   {
       int i;
       
       for (i=0; i < v.length ; i++)
           System.out.print(" " + v[i]);
       System.out.println();
   }
   public static void printPicture(Embedding embed, Fullerene f)
   {
       int i, j;

       System.out.println(" " + f.p.n);
       for (i=0; i < f.p.n; i++)
       {
           System.out.print("" + embed.x[i] + " " + embed.y[i] + "  3 ");
           for (j=0; j < 3; j++)
           {
               System.out.print(" " + f.p.Adj[i][j]);
           }
           System.out.println();
       }
       System.out.println();
   }
} 
