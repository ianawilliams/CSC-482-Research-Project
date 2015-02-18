#define NMAX 120
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define PRINT 0
main(int argc, char *argv[])
{

   char*  names[7];
   FILE*  fp[7];

   int n;
   int pent[12];
   int isomer_number;
   int face_number;
   int face_size;
   int score;
   int i;

   if (argc!=2)
   {
       printf("Usage: %s <3 character string to help name files>\n", argv[0]);
       exit(0);
   }

   names[0] = strdup("pic_hxxx"); // Header file name.
   names[1] = strdup("pic_fxxx"); // Fullerene file name.
   names[2] = strdup("pic_nxxx"); // Number of vertices file name.
   names[3] = strdup("pic_ixxx"); // Isomer number file name.
   names[4] = strdup("pic_cxxx"); // Center for the picture file name.
   names[5] = strdup("pic_sxxx"); // Score file name.
   names[6] = strdup("pic_pxxx"); // Picture drawing info file name.

   for (i=0; i < 7; i++)
   {
       names[i][5]= argv[1][0];
       names[i][6]= argv[1][1];
       names[i][7]= argv[1][2];
   }
   for (i=0; i < 7; i++)
   {
       fp[i]= fopen(names[i], "w");
   }
   printf("The files created for FUIGUI are:\n");
   printf("The header file         : %s\n", names[0]);
   printf("The fullerenes          : %s\n", names[1]);
   printf("The number of vertices  : %s\n", names[2]);
   printf("The isomer numbers      : %s\n", names[3]);
   printf("The centering face      : %s\n", names[4]);
   printf("The picture score       : %s\n", names[5]);
   printf("The picture drawing info: %s\n", names[6]);

// First make the new header file.

/*
    Sample header file format:

    Drawings of fullerenes with scores: 030
    pic_f030 5
    "Number of vertices" int pic_n030
    pic_n030
    "Isomer number" int pic_i030
    pic_i030
    "Centering face number " int pic_c030
    pic_c030
    "Score for the picture" int pic_s030
    pic_s030
    "Picture drawing information" pic pic_p030
    pic_p030


*/

   fprintf(fp[0], "Drawings of fullerenes with scores: %1c%1c%1c\n", 
          argv[1][0], argv[1][1], argv[1][2]);

   fprintf(fp[0], "%s 5\n", names[1]);

   fprintf(fp[0], "\"Number of vertices\" int %s\n%s\n", names[2], names[2]);
   fprintf(fp[0], "\"Isomer number\" int %s\n%s\n", names[3], names[3]);
   fprintf(fp[0], "\"Centering face number \" int %s\n%s\n", names[4], names[4]);
   fprintf(fp[0], "\"Score for the picture\" int %s\n%s\n", names[5], names[5]);
   fprintf(fp[0], "\"Picture drawing information\" pic %s\n%s\n", names[6], names[6]);

   while (scanf("%d", &score)==1)
   {

//      Read in the one per line information.

        scanf("%d", &isomer_number);
        scanf("%d", &face_number);
        read_pent(&n, pent);

//      Write it to files suitable for fuigui browsing.
/*
        names[1] = strdup("pic_fxxx"); // Fullerene file name.
        names[2] = strdup("pic_nxxx"); // Number of vertices file name.
        names[3] = strdup("pic_ixxx"); // Isomer number file name.
        names[4] = strdup("pic_cxxx"); // Center for the picture file name.
        names[5] = strdup("pic_sxxx"); // Score file name.
        names[6] = strdup("pic_pxxx"); // Picture drawing info file name.
*/

  // Print the fullerene in the face spiral notation.

        fprintf(fp[1], "%d", n);
        for (i=0; i < 12; i++)
            fprintf(fp[1], " %0d", pent[i]);
        fprintf(fp[1], "\n");
       
        fprintf(fp[2], "%d\n", n);
        fprintf(fp[3], "%d\n", isomer_number);
        fprintf(fp[4], "%d\n", face_number);
        fprintf(fp[5], "%d\n", score);

        face_size=6;
        for (i=0; i < 12; i++)
        {
            if (face_number == pent[i])
            {
                face_size=5;
                break;
            }
        }
        fprintf(fp[6], "%d %d\n", face_size, face_number);
   }
   for (i=0; i < 7; i++)
   {
       fclose(fp[i]);
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
