#define NMAX 120
#include <stdio.h>
#include <stdlib.h>
#define PRINT 0
int read_picture(int *n, double x[NMAX], double y[NMAX], int G[NMAX][3]);
void print_picture(int n, double x[NMAX], double y[NMAX], int G[NMAX][3]);
int get_score(int isomer_number, int face_number, int n, double x[NMAX], double y[NMAX], int G[NMAX][3]);
main()
{
   int n;
   int G[NMAX][3];
   double x[NMAX];
   double y[NMAX];

   int isomer_number;
   int face_number;
   int score;

   isomer_number=0;

   while (read_picture(&n, x, y, G))
   {
       isomer_number++;
       face_number=0;

#if PRINT
       printf("========================= The pictures for isomer number ", isomer_number);
       printf("Isomer %3d centered at face %2d\n", isomer_number, face_number);
       print_picture(n, x, y, G);
#endif

       score= get_score(isomer_number, face_number, n, x, y, G);
#if PRINT
       printf("The score is:\n");
#endif
       printf("%0d\n", score);
       
       for (face_number=1; face_number < n/2+2; face_number++)
       {
            if (!read_picture(&n, x, y, G))
            {
                printf("Error- ran out of pictures earlier than expected: missing face number %2d.\n", face_number);
                exit(0);
            }
#if PRINT
            printf("Isomer %3d centered at face %2d\n", isomer_number, face_number);
            print_picture(n, x, y, G);
#endif
            score= get_score(isomer_number, face_number, n, x, y, G);
#if PRINT
            printf("The score is:\n");
#endif
            printf("%0d\n", score);
       }
   }
}
// Write the scoring code here.
int get_score(int isomer_number, int face_number, int n, double x[NMAX], double y[NMAX], int G[NMAX][3])
{
    int score;

    score=  face_number;
 
    return(score);
}
void print_picture(int n, double x[NMAX], double y[NMAX], int G[NMAX][3])
{
   int i, j;
   int d=3;

   for (i=0; i < n; i++)
   {
       printf("%3d (%4.0lf, %4.0lf): ", i, x[i], y[i]);
       for (j=0; j < d; j++)
       {
           printf(" %3d", G[i][j]);
       }
       printf("\n");
   }
}
int read_picture(int *n, double x[NMAX], double y[NMAX], int G[NMAX][3])
{
   int i, j;
   int d;

   if (scanf("%d", n)!= 1) return(0);
   if (*n < 20 || *n > NMAX)
   {
       printf("Error- bad value %2d for n\n", *n);
       exit(0);
   } 

   for (i=0; i < *n; i++)
   {
       if (scanf("%lf", &x[i])!=1) return(0);
       if (scanf("%lf", &y[i])!=1) return(0);
       if (scanf("%d", &d)!=1) return(0);
       if (d !=3)
       {
          printf("Error- bad value %2d for degree\n", d);
          exit(0);
       }
       for (j=0; j < d; j++)
       {
           if (scanf("%d", &G[i][j])!=1) return(0);
       }
   }

   return(1);
}
