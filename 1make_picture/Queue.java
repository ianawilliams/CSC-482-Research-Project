/* This class contains methods for operating on a
   Queue where the assumptions is that that queue
   has a fixed max number n of the total number of
   additions that will be made to the queue that
   is known in advance. These types of queues are
   well-suited for algorithms such as a BFS of a graph. 

   Dec. 17: 2005:
   Added myPosition so that the drawing routine can
   determine for a vertex the order in which the
   neighbours were placed in the queue so
   that the neighbour first placed in the queue
   can be found. The default value for the ones
   not in the queue is set to n to facilitate
   this operation. The values should not be updated
   as the vertices are removed from the queue in order
   to maintain the history.

   Creator: Wendy Myrvold
   Last updated: Dec. 17, 2005
 */
public class Queue
{
	int qfront, qrear;
	int[] queue;
	int[] myPosition;

	public Queue copyof()
	{
		Queue q;
		int i;

		int n = queue.length;

		q = new Queue(n);

		q.qfront = qfront;
		q.qrear = qrear;

		for (i = 0; i < n; i++)
		{
			q.queue[i] = queue[i];
			q.myPosition[i] = myPosition[i];
		}
		return (q);
	}

	public Queue(int n)
	{
		int i;

		qfront = 0;
		qrear = 0;
		queue = new int[n];
		myPosition = new int[n];
		for (i = 0; i < n; i++)
			myPosition[i] = n;
	}

	/* Swap entries in positions i and j. */

	public void swap(int i, int j)
	{
		int u, v;

		u = queue[i];
		v = queue[j];
		queue[j] = u;
		queue[i] = v;
		myPosition[u] = j;
		myPosition[v] = i;
	}

	void add_rear(int v)
	{
		queue[qrear] = v;
		myPosition[v] = qrear;
		qrear++;
	}

	void add_front(int v)
	{
		qfront--;
		queue[qfront] = v;
		myPosition[v] = qfront;
	}

	public int del_rear()
	{
		int v;

		qrear--;
		v = queue[qrear];
		return (v);
	}

	public int del_front()
	{
		int v;

		v = queue[qfront];
		qfront++;
		return (v);
	}

	public int size()
	{
		return (qrear - qfront);
	}
}
