import java.util.Random;
public class Main {}

class Board
{
    int size;	
	Node [] [] boardState;
	Board(int _probability, int _size)
	{
		if (_probability<1) System.exit(2);
		size = _size;
		boardState = new Node [size][size];
		for (int i = 0; i<size; i++)
		{
			for (int j = 0; j<size; j++)
			{
				boardState[i][j]=new Node(_probability);
			}
		}
	}
	
	void ignite()
	{
		Random randomGenerator = new Random();
		Node choosenTree;
		do
		{
			choosenTree= boardState[randomGenerator.nextInt(size)][randomGenerator.nextInt(size)];
		}
		while (choosenTree.isTree == false);
		choosenTree.isBurning=true;
	}
	
	void spreadFire()
	{
	/*	for (int i = 0; i<size; i++)
		{
			for (int j = 0; j<size; j++)
			{
				if ((boardState[i][j].isTree==true) && (boardState[i][j].isBurning==true))
				{
					burnNeighboursAndBurnOut(i,j);	
				}
				if (boardState[i][j].isTree==false)
				{
					boardState[i][j].isBurning=false;
					boardState[i][j].burnt=false;
				}
			}
		}*/
	}
	
	void burnNeighboursAndBurnOut(int _i,int _j)
	{
		
		if ((_i-1>=0)&&(_j+1<size)&&(boardState[_i-1][_j+1].isTree==true)&& (boardState[_i-1][_j+1].burnt == false)) boardState[_i-1][_j+1].isBurning = true;
		if ((_j+1<size)&&(boardState[_i][_j+1].isTree==true) && (boardState[_i][_j+1].burnt == false)  )  boardState[_i][_j+1].isBurning = true;
		if ((_j+1<size)&&(_i+1<size)&&(boardState[_i+1][_j+1].isTree==true)&&(boardState[_i+1][_j+1].burnt == false))  boardState[_i+1][_j+1].isBurning = true;
		if ((_i+1<size)&&(boardState[_i+1][_j].isTree==true) &&(boardState[_i+1][_j].burnt == false))boardState[_i+1][_j].isBurning = true;
		if ((_j-1>=0)&&(_i+1<size)&&(boardState[_i+1][_j-1].isTree==true)&&(boardState[_i+1][_j-1].burnt == false)) boardState[_i+1][_j-1].isBurning = true;
		if ((_j-1>=0)&&(boardState[_i][_j-1].isTree==true)&&(boardState[_i][_j-1].burnt == false) )boardState[_i][_j-1].isBurning = true;
		if ((_i-1>=0)&&(_j-1>=0)&&(boardState[_i-1][_j-1].isTree==true)&&(boardState[_i-1][_j-1].burnt == false)) boardState[_i-1][_j-1].isBurning = true;
		if ((_i-1>=0)&&(boardState[_i-1][_j].isTree==true)&&(boardState[_i-1][_j].burnt == false) )boardState[_i-1][_j].isBurning = true;
		boardState[_i][_j].isBurning=false;
		boardState[_i][_j].burnt=true;
	}
	
  
	boolean isAllBurnt()
	{
		boolean forestBurnt = true;
		label:
		for (int i = 0; i<size; i++)
		{
			for (int j = 0; j<size; j++)
			{
				if ((boardState[i][j].isTree==true) && (boardState[i][j].isBurning==true))
				{
					forestBurnt = false;
					break label;
				}
			}
		}
		return forestBurnt;
	}
	
	
}

class Node
{
	static Random randomGenerator = new Random();
	boolean isTree = false;
	boolean isBurning = false;
	boolean burnt = false;
	
	Node(int _probability)
	{
		if (generateRandom()< _probability)
		{
			isTree = true;
		}
	}
	
	int generateRandom()
	{
		return randomGenerator.nextInt(100);

	}
}
