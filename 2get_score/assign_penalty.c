#define NMAX 120
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#define PRINT 0
#define pi 3.14159265358979323846264338327950288419716939937510
int read_picture(int *n, double x[NMAX], double y[NMAX], int G[NMAX][3]);
void print_picture(int n, double x[NMAX], double y[NMAX], int G[NMAX][3]);
int get_lengths( int n, double x[NMAX], double y[NMAX], int G[NMAX][3], double first[NMAX][2], double second[NMAX][2], int num_edges);
int get_crossing( int n, double x[NMAX], double y[NMAX], int G[NMAX][3], double first[NMAX][2], double second[NMAX][2], int num_edges);
int compareEdges(double xa, double ya, double xb, double yb, double xc, double yc, double xd, double yd);
double length(double xa, double ya, double xb, double yb);
double slope(double xa, double ya, double xb, double yb);
double yIntercept(double xa, double ya, double slope);
double getAngle(double x, double y, double a, double b);
double angle_difference(double angle);
int get_symmetry( int n, double x[NMAX], double y[NMAX]);
int get_straight( int n, double x[NMAX], double y[NMAX], int G[NMAX][3], double first[NMAX][2], double second[NMAX][2], int num_edges);

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
	int score = 0;

	int num_edges = 0;	
	int vertex = 0;
	int adj = 0;
	int edges = n * 3;
	int num = 0;
	
	double first[NMAX][2];
	double second[NMAX][2];


	while (vertex < n)
	{
		adj = 0;
		while (adj < 3)
		{
			num = 0;
			while (num < num_edges)
			{
				if (first[num][0] == x[vertex] && first[num][1] == y[vertex])
				{
					if (second[num][0] == x[G[vertex][adj]] && second[num][1] == y[G[vertex][adj]])
					{
						//the edge is already in the list
						break;
					}
						
				}
				else if (first[num][0] == x[G[vertex][adj]] && first[num][1] == y[G[vertex][adj]])
				{
					if (second[num][0] == x[vertex] && second[num][1] == y[vertex])
					{
						//the edge is already in the list
						break;
					}
					
					
				}
					
				num++;
			}
			int temp = 0;
			if (num == num_edges)
			{
				//the edge is not already in the set

					first[num_edges][0] = x[vertex];
					first[num_edges][1] = y[vertex];
					second[num_edges][0] = x[G[vertex][adj]];
					second[num_edges][1] = y[G[vertex][adj]];		
					num_edges++;
			}

			adj++;
		}

		vertex++;	
	}

	int cross = get_crossing( n, x, y, G, first, second, num_edges);
	int edge_length = get_lengths( n, x, y, G, first, second, num_edges);	
	int convex = get_convex( n, x, y, G);	
	int symmetry = get_symmetry( n, x, y); 	
	int straight = get_straight( n, x, y, G, first, second, num_edges);
	double crissCross = 1.0; 
	if (cross > 0)
	{
		
		if (cross < 3)
		{
			crissCross = 0.5;
		}
		else if (cross < 6)
		{
			crissCross = 0.4;
		}
		else
		{
			crissCross = 0.3;
		}
	}
	else
	{
		crissCross = 1.0;
	}

	
	double length = 0;

	if (edge_length < 3)
	{
		length = 1.0;			
	}
	else if (edge_length < 4)
	{
		length = 0.9;
	}
	else if (edge_length < 5)
	{
		length = 0.85;
	}
	else if (edge_length < 6)
	{
		length = 0.80;
	}
	else
	{
		length = 0.75;
	}


	double angles;
	if (convex < 16)
	{
		angles = 1.0;
	}

	else if (convex < 19)
	{
		angles = 0.95;
	}
	else if (convex < 21)
	{
		angles = 0.90;
	}
	else if (convex < 23)
	{
		angles = 0.85;
	}
	else if (convex < 25)
	{
		angles = 0.8;
	}
	else
	{
		angles = 0.75;
	}
	
	double sym;
	if (symmetry < 5)
	{
		sym = 1.0;
	}
	else if (symmetry < 10)
	{
		sym = 0.96;
	}
	else if (symmetry < 15)
	{
		sym = 0.92;
	}
	else if (symmetry < 20)
	{
		sym = 0.88;
	}
	else if (symmetry < 25)
	{
		sym = 0.84;
	}
	else if (symmetry < 30)
	{
		sym = 0.80;
	}
	else
	{
		sym = 0.76;
	}
	
	double str;
	if (straight > 11)
	{
		str = 1.0;
	}
	
	else if (straight > 9)
	{
		str = 0.96;
	}	

	else if (straight > 7)
	{
		str = 0.92;
	}	
	else if (straight > 5)
	{
		str = 0.88;
	}	
	else if (straight > 3)
	{
		str = 0.84;
	}
	else
	{
		str = 0.8;
	}


	
	int result = ((55 * length) + (35 * angles) + (10 * str * (0.75 * sym))) * crissCross;


	//Commented out line is to reverse order of scores 
	//ie: best images at the beginning	
	//int answer = 100 - result;
	return result;
}

int get_symmetry( int n, double x[NMAX], double y[NMAX])
{
	
	double xMax = x[0];
	double xMin = x[0];
	double yMax = y[0];
	double yMin = y[0];
	int sum = 0;
	int temp = 0;
	int big = 0;
	int little = 0;
	
	int index = 0;
	while (index < n)
	{
		if (x[index] > xMax)
		{
			xMax = x[index];	
		}
		else if (x[index] < xMin)
		{
			xMin = x[index];	
		}
		index++;	
	}
	index = 0;
	while (index < n)
	{
		if (y[index] > yMax)
		{
			yMax = y[index];	
		}
		else if (y[index] < yMin)
		{
			yMin = y[index];	
		}
		index++;	
	}
	double xLength = xMax - xMin;
	double yLength = yMax - yMin;
	double xMid = (xLength / 2);
	double yMid = (yLength / 2);
	double Xaxis = xMid + xMin;
	double Yaxis = yMid + yMin;
	
	double divider = 25;
	int xNum[25][2] = {0};
	double quadrant = yLength / divider;
	int quarter = 0;
	int side = 0;
	int vertex = 0;
	double curr;
	
	while (vertex < n)
	{
	
		curr = y[vertex];
		
		quarter = 0;
		while (curr > (yMin + quadrant))
		{
			curr = curr - quadrant;
			quarter++;	
		}
		if (x[vertex] > Xaxis)
		{
			side = 1;	
		}
		else
		{
			side = 0;	
		}
		
		temp = xNum[quarter][side];
		xNum[quarter][side] = temp + 1;
		
		temp = 0;
		vertex++;

	}
	
	index = 0;
	while (index < divider)
	{
		
		if (xNum[index][0] > xNum[index][1])
		{
			big = xNum[index][0];
			little = xNum[index][1];
		}
		else
		{
			little = xNum[index][0];
			big = xNum[index][1];
		}
		
		sum = sum + (big - little);
		index++;
		
	}
	int result = sum;
	return result;
}

int get_convex( int n, double x[NMAX], double y[NMAX], int G[NMAX][3])
{
	
	int vertex = 0;
	int adj = 0;
	double a;
	double b;
	double c;
	double highest;
	double second;
	double third;
	double difference;
	double sum;
	double count = 0;
	int adjTwo;
	double checkOne;
	double checkTwo;
	
	while (vertex < n)
	{
		
	
		a = getAngle(x[vertex], y[vertex], x[G[vertex][0]], y[G[vertex][0]]);
		b = getAngle(x[vertex], y[vertex], x[G[vertex][1]], y[G[vertex][1]]);	
		c = getAngle(x[vertex], y[vertex], x[G[vertex][2]], y[G[vertex][2]]);
			
		if (a > b)
		{
			if (a > c)
			{
				highest = a;
				if (b > c)
				{
					second = b;
					third = c;
				}
				else
				{
				// c > b
					second = c;
					third = b;
				}
				
			}
			else
			{
			// c > a
				highest = c;
				second = a;
				third = b;
			}
		}
		else
		{
		// b > a	
			if (b > c)
			{
				highest = b;
				if (a > c)
				{
					second = a;
					third = c;
				}
				else
				{
				// c > a
					second = c;
					third = a;
				}
			}
			else
			{
			// c > b	
				highest = c;
				second = b;
				third = a;
			}
		}
	a = highest - second;
	b = second - third;
	c = third + (360 - highest);
	
	
	if (a < b)
	{
		checkOne = a;
		if (b < c)
		{
			checkTwo = b;
		}
		else
		{
		// b > c
			checkTwo = c;		
		}
	}
	else
	{
	// a > b
		checkOne = b;
		if (a < c)
		{
			checkTwo = a;
		}
		else
		{
		// a > c
			checkTwo = c;
		}
	}
	
	sum = sum + angle_difference(checkOne);
	sum = sum + angle_difference(checkTwo);
	count = count + 2;
	/*	
	
	if (checkOne > checkTwo)
	{
		sum = sum + (checkOne - checkTwo);
	}
	else
	{
		sum = sum + (checkTwo - checkOne);
	}
	count++;
	*/
	vertex++;
	}
	
	double temp = sum / count;
	int answer = sum / count;
	
	
	return answer;
	
}

double angle_difference(double angle)
{
	double difference;
	if (angle < 90)
	{
		difference = 90 - angle;
	}
	else if (angle > 120)
	{
		difference = angle - 120;
	}
	else
	{
		difference = 0;
	}
	return difference;
}

double getAngle(double x, double y, double a, double b)
{
	double opp;
	double adj;
	int position;
	double deg;	
/*	
 THIS SECTION FINDS WHICH QUADRANT THE LINE IS IN	
	(x,y) is the center, (a,b) is in relation
*/ 
	if (y == b)
	{
		if (x < a)
		{
			return 0;	
		}
		else
		{
			return 180;
		}
	}
	if (x == a)
	{
		if (y < b)
		{
			return 90;
		}
		else
		{
			return 270;	
		}
	}


 if (a > x)
	{
		if (b > y)
		{
			position = 1;	
		}
		else
		{
			position = 4;	
		}
		
	}
	else
	{
		if (b > y)
		{
			position = 2;	
		}
		else
		{
			position = 3;	
		}
	
	}
	
	
// THIS SECTION DETERMIENS WHICH VARAIBLES MAKE UP ADJACENT AND OPPOSITE

	if (position == 1)
	{
		opp = b - y;
		adj = a - x;
		deg = 0;
	}
	
	if (position == 2)
	{
		opp = x - a;
		adj = b - y;
		deg = 90.0;
	}
	
	if (position == 3)
	{
		opp = y - b;
		adj = x - a;
		deg = 180.0;
	}
	
	if (position == 4)
	{
		opp = y - b;
		adj = a - x;
		deg = 270.0;
	}
	
// THIS SECTION FINDS THE INVERSE TANGENT

	double num = opp / adj;
	double tan = atanf(num);
	double angleOne = tan * (180 / pi);
	angleOne = angleOne + deg;

	return angleOne;
}

double length(double xa, double ya, double xb, double yb)
{
	double y = yb - ya;
	
	if (y < 0)
	{
		y = y * -1.0;
	}
	
	double x = xb - xa;
	
	if (x < 0)
	{
		x = x * -1.0;
	}
	
	double hyp = (x * x) + (y * y);
	
	double answer = sqrt(hyp);
	
	return answer;	
}

int get_lengths( int n, double x[NMAX], double y[NMAX], int G[NMAX][3], double first[NMAX][2], double second[NMAX][2], int num_edges)
{
	int line_length;
	double distances[num_edges];
	double sorted_distances[num_edges];
	int added[num_edges];
	int central = 0;
	double differences[num_edges - 1];

	int count = 0;
	while (count < num_edges)
	{
		distances[count] = length(first[count][0], first[count][1], second[count][0], second[count][1]);
		count++;
	}
	
	int temp = 0;
	int outer = 0;
	int inner = 0;	
	double highest;
	while (outer < num_edges)
	{	
		highest = 0;
		inner = 0;
		while (inner < num_edges)
		{ 
			if (distances[inner] > highest)
			{
				highest = distances[inner];
				temp = inner;
			}
			
			inner++;
		}
		sorted_distances[outer] = highest;
		distances[temp] = 0;
		outer++;
	}
	
	count = 0;
	double total = 0;
	while (count < num_edges -1)
	{
		differences[count] = sorted_distances[count] - sorted_distances[count + 1];
		total = total + differences[count];		
		count++;
	}
	double result = total / (num_edges - 1);
	int answer = result;
	return answer;



}

int get_straight( int n, double x[NMAX], double y[NMAX], int G[NMAX][3], double first[NMAX][2], double second[NMAX][2], int num_edges)
{
	int line_length;
	double distances[num_edges];
	double sorted_distances[num_edges];
	int added[num_edges];
	int central = 0;
	double differences[num_edges - 1];

	int count = 0;
	int index = 0;
	while (index < num_edges)
{
	if (first[index][0] == second[index][0])
	{
		count++;
	}
	else if (first[index][1] == second[index][1])
	{
		count++;
	}

	index++;
}

	return count;



}



int get_crossing( int n, double x[NMAX], double y[NMAX], int G[NMAX][3], double first[NMAX][2], double second[NMAX][2], int num_edges)
{	
	int vertex = 0;
	int adj = 0;
	int num = 0;	
	int crossings = 0;
	int index = 0;
	int position = 0;
	int check = 0;
	while (check < num_edges)
	{
		position = 0;
		while (position < check)
		{
			
			if (compareEdges(first[check][0], first[check][1], second[check][0], second[check][1], first[position][0], first[position][1], second[position][0], second[position][1]) != 0)
			{
				
				crossings++;
			}
			
			position++;
		}
		check++;
	}

	return(crossings);
}

int compareEdges(double xa, double ya, double xb, double yb, double xc, double yc, double xd, double yd)
{

	//check to see if the edges are the same

	/*
		check to see if any edges are adjacent
	*/
	if (xa == xc && ya == yc)
	{
		return 0;
	}
	if (xa == xd && ya == yd)
	{
		return 0;
	}
	if (xb == xc && yb == yc)
	{
		return 0;
	}
	if (xb == xd && yb == yd)
	{
		return 0;
	}

	double slopeA = slope(xa, ya, xb, yb);
	double slopeB = slope(xc, yc, xd, yd);

	double intA = yIntercept(xa, ya, slopeA);
	double intB = yIntercept(xc, yc, slopeB);
	
	if (slopeA == slopeB)
	{
		if (intA != intB)
		{
			/*if the lines are parralel but dont 
			have the same intercept they never touch*/
			return 0;	
		}
		else
		{
			//return 1;	
		}
	}
	
	double slant = slopeA - slopeB;
	double b = intB - intA;
	
	double x = b / slant;
	double y = (slopeA * x) + intA;
	
	// print the intersection point
	
	/*
	Start of copied code from old document
	*/
	
	double array[4];
	
	if (xa > xb)
	{
		array[0] = xb;
		array[1] = xa;
	}
	else
	{
		array[1] = xb;
		array[0] = xa;	
	}
	
	if (xc > xd)
	{
		array[2] = xd;
		array[3] = xc;
	}
	else
	{
		array[3] = xd;
		array[2] = xc;	
	}
	
	if (x > array[0])
	{
		if (x < array [1])
		{
			if (x > array[2])
			{
				if (x < array[3])
				{
					return 1;
								
				}
			}
		}
	}
	
	/*
	End of copied code from old document
	*/

	return 0;	
}


double slope(double xa, double ya, double xb, double yb)
{
	double array[4];
	
	if (xa > xb)
	{
		array[0] = xb;
		array[1] = xa;
		array[2] = yb;
		array[3] = ya;
		
	}
	else
	{
		array[1] = xb;
		array[0] = xa;
		array[3] = yb;
		array[2] = ya;
		
	}
	
	double rise = array[3] - array[2];
	double run = array[1] - array[0];
	
	double slope = rise / run;
	
	if (slope < 0)
	{
		if (array[3] > array[2])
		{
			slope = slope * -1.0;	
		}
	}
	else
	{
		if (array[3] < array[2])
		{
			slope = slope * -1.0;	
		}	
	}
	
	return slope;
}

double yIntercept(double x, double y, double slope)
{
	double b = y - (slope * x);
	return b;	
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
