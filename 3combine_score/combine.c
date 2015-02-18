#define NMAX 120
#include <stdio.h>
#include <stdlib.h>
#define PRINT 0
main(int argc, char *argv[])
{
   FILE *sc_file;
   
   int n;
   int pent[12];
   int isomer_number;
   int face_number;
   int score;
   int i;

   if (argc!= 2)
   {
       printf("Usage: %s <name of file with scores>\n", argv[0]);
       exit(0);
   }

   sc_file= fopen(argv[1], "r");

   isomer_number=0;

   while (read_pent(&n, pent))
   {
       isomer_number++;
       for (face_number=0; face_number < n/2+2; face_number++)
       {
           if (fscanf(sc_file, "%d", &score)!=1)
           {
               printf("Error- missing score for isomer number %3d and face number %3d\n", 
                      isomer_number, face_number);
               exit(0);
           }
           // Print the info we need later one line per picture so we can sort by score.
           printf("%4d ", score);
           printf("%4d %2d  %3d ", isomer_number, face_number, n);
           for (i=0; i < 12; i++)
               printf(" %2d", pent[i]);
           printf("\n");
       }
   }
}
// Read in number of vertices and the 12 pentagon numbers for a face spiral.
int read_pent(int *n, int pent[12])
{
    int i;
    if (scanf("%d", n)!=1) return(0);
    for (i=0; i < 12; i++)
    {
        if (scanf("%d", &pent[i])!=1)
        {
             printf("Error- missing pentagon.\n");
             exit(0);
        }
    }
    return(1);
} 
