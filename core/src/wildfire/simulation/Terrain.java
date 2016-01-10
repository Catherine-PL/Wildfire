package wildfire.simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import wildfire.simulation.Cell.State;
import wildfire.simulation.Cell.Wood;
import wildfire.simulation.Data.Direction;

/**
 * Represents map section as cells. Main class for a simulation.
 * @author Sebastian
 *
 */
public class Terrain {

	private Cell [][] 				terrainState;
	private static Set<Cell> 		treesOnFire = new HashSet<Cell>();
	
	public static Set<Cell> 		treesOnFireAdd = new HashSet<Cell>();
	public static Set<Cell> 		treesOnFireRemove = new HashSet<Cell>();
	public static Random 			randomGenerator = new Random();		
	public static int 				outBorderFire=0;
	public static int 				spotingCount=0;
	public int 						size;
	
	
	Terrain(int _size, int probability, int vegtype, int relief, int roughness) //probability i roguhness zakres 0-100
	{		
		double l=0;
		size = _size;
		terrainState = new Cell[size][size];
		int life=20;
		int heightS = 500;
		int heightT = 10;
		
		for (int y = 0; y<size; y++)
			for (int x = 0; x<size; x++)
			{
				Wood wood;
				State s;
				if(probability > Terrain.randomGenerator.nextInt(100))
				{
					s = State.FUEL;
					if(vegtype > Terrain.randomGenerator.nextInt(100))
						{
						wood = Wood.OAK;
						life=Data.lifetime_oak;
						}
					else	
					{
						wood = Wood.PINY;
						life=Data.lifetime_piny;
					}
						
					
				}
				else		
				{
					s = State.FREE;
					wood = Wood.NONE;
				}																
				
				terrainState[y][x]=new Cell(new Cell.Coordinate(y, x), s, wood, life, heightS, heightT, 1);
				
				if(terrainState[y][x].getState() == Cell.State.FUEL)
					l++;
			}
		
		System.out.println("Number of all cells: " + size*size);
		System.out.println("Number of all trees: " + l);
		System.out.println();
		terrainState[size/2][size/2]=new Cell(new Cell.Coordinate(size/2, size/2), State.FUEL, Wood.OAK, life, heightS, heightT, 1);
		defineNeighbors(Data.winddir);		
		generateElevation(relief);
		ignite();
		
	}

	private void defineNeighbors(Data.Direction wind_direction)			
	{
		
		for (int y = 0; y<size; y++)
		{
			for (int x = 0; x<size; x++)
			{
				if(terrainState[y][x].getState() == Cell.State.FUEL)		// dla ka¿dego drzewa, a nie dla kamieni
				{
					HashMap<Integer, TreeSet<Integer>> neighbors = terrainState[y][x].elipse(wind_direction.angle);					
					for(Integer xn : neighbors.keySet())						
					{
						for(Integer yn : neighbors.get(xn))
						{
							if(yn >= 0 && yn < size && xn >= 0 && xn < size)
								terrainState[y][x].addNeighbor(terrainState[yn][xn]);							
						}
					}					
				}
			}												
		}														
	}
	//rozpoczêcie po¿aru - 9 œrodkowych drzew podpalanych
	public void ignite()													
	{
		
		Cell choosenTree;
		
		for(int i=-1;i<2;i++)
		{
			for(int j=-1;j<2;j++)
			{
				choosenTree= terrainState[size/2-i][size/2-j];
				choosenTree.setBurnThreshold(0);
				choosenTree.setState(Cell.State.BURNING);
				treesOnFire.add(choosenTree);
			}
		}
		
		
		
		
	}	
	
	
	//podstawowy generator, nie ma uwzglednienia wysokosci na jakiej sa sasiedzi
	public void generateElevation(int relief)
		{
			int probability=0;
			for (int y = 0; y<size; y++)
				for (int x = 0; x<size; x++)
				{
					probability = randomGenerator.nextInt(100);
					if (probability<10) terrainState[y][x].setElevation(relief);
					else if (probability<20) terrainState[y][x].setElevation(relief/5);
					else if (probability<50) terrainState[y][x].setElevation(relief/3);
					else if (probability<75) terrainState[y][x].setElevation(relief/2 + relief/5);
					else if (probability<90) terrainState[y][x].setElevation(relief/2 - relief/5);				
				}	
		}
		
	/**
	 * Spreading fire among trees. It invoke spreadFire() function from Cell class to ignite neighbors. 
	 */
	public void spreadFire()
	{
		
		for(Cell t : treesOnFire)
		{
			t.spreadFire();
		}	
		
		System.out.println("---treesOnFire: " + treesOnFire.size() + ", treesOnFireAdd: " + treesOnFireAdd.size()+ ", treesOnFireRemove: " + treesOnFireRemove.size());		
		for(Cell temp : treesOnFireAdd)
		{
			treesOnFire.add(temp);
			

		}		
		treesOnFireAdd.clear();
		for(Cell temp : treesOnFireRemove)
		{
			treesOnFire.remove(temp);
		}		
		treesOnFireRemove.clear();
		
		spreadSpotting2(treesOnFire);
		System.out.println("Poza zasiêgiem "+Terrain.outBorderFire+" Spotting fire"+Terrain.spotingCount);
	}	
	public boolean isAllBurnt()
	{
		return treesOnFire.isEmpty();
	}
	public Cell getCell(int y, int x)
	{
		return terrainState[y][x];
	}
	
	public String toString()
	{
		String txt = new String();
		for(int y = 0 ; y<size; y++)
		{
			for(int x = 0 ; x<size; x++)
			{
				txt = txt + terrainState[y][x].getState() + ", ";
			}
			txt = txt + "\n";
		}
		
		return txt;
	}
	
	
	public void spreadSpotting2(Set<Cell> paliSie){
		int X=0,Y=0,N=0,kX=0,kY=0,skalar=0,nowyX,nowyY;		
		double spottingDist=0;
		Random ran=new Random();
		
		switch (Data.winddir){
		
		//case N :{ X=-1;Y=0; break;}
		case N :{ X=0;Y=-1; break;}
		//case E :{ X=0;Y=1; break;}
		case E :{ X=1;Y=0; break;}
		//case W : { X=0;Y=-1; break;}
		case W : { X=-1;Y=0; break;}
		//case S :{ X=1;Y=0; break;}
		case S :{ X=0;Y=1; break;}
		//case NE :{ X=-1;Y=1; break;}
		case SW :{ X=-1;Y=1; break;}
		case SE :{ X=1;Y=1; break;}
		//case SW : { X=1;Y=-1; break;}
		case NE : { X=1;Y=-1; break;}
		case NW : { X=-1;Y=-1; break;}
		}
		
		//for(Cell komorka: Terrain.this.treesOnFire){
		for(int i=0;i<size;i++)
		  for(int j=0;j<size;j++){
			  Cell komorka=Terrain.this.getCell(j, i);
			  if(komorka.getState()==Cell.State.BURNING){
			//komórka dla której liczê
			//kX=komorka.getCoordinates().x;
				  kX=i;
				  kY=j;
			//kY=komorka.getCoordinates().y;
			nowyX=kX;
			nowyY=kY;
			//obliczenie iloœci pal¹cych siê drzew w okolicy 3x3
			N=getNeighnoursOnFire(kX,kY)+1;
			//wyliczenie maksymalnego dystansu spotiingu z Albiniego
			spottingDist=komorka.getSpottingDistance(N);
			
			//losowo wybiermay spotting distance z progiem górnym z Albiniego
			skalar=ran.nextInt((int) spottingDist);
			
			if(X!=0)nowyX=kX+X*skalar;
			if(Y!=0)nowyY=kY+Y*skalar;		
			if(nowyY>0 && nowyY<100 && nowyX>0 && nowyX<100){
			if(Terrain.this.terrainState[nowyY][nowyX].getState()==Cell.State.FUEL){
				//Terrain.this.terrainState[nowyY][nowyX].setBurnthresholdToZero();
				Terrain.this.terrainState[nowyY][nowyX].setState(Cell.State.BURNING);;
				Terrain.spotingCount++;
					}
				}else Terrain.outBorderFire++;
			}
			
			
			
			
		}
		
		
		
		
		
		
		
		
		
		
		
	}
	
	public void spreadSpotting(){
		Set<Cell> onFire=Terrain.treesOnFire; //Terrain.treesOnFire;
		Set<Cell> newOnFire=new HashSet<Cell>();
		double spDist;
		int Y=0,X=0;
	
		switch (Data.winddir){
				//wg mnie to jest OK
		case N :{ X=-1;Y=0; break;}
		case E :{ X=0;Y=1; break;}
		case W : { X=0;Y=-1; break;}
		case S :{ X=1;Y=0; break;}

		case NE :{ X=-1;Y=1; break;}
		case SE :{ X=1;Y=1; break;}
		case SW : { X=1;Y=-1; break;}
		case NW : { X=-1;Y=-1; break;}
		}
		
		
		for(Cell cell: onFire){
			int skalar=1,N=0;
			int newX=0,newY=0;				//inicjalizacja koordynatów 
			int cX=cell.getCoordinates().x; //pobranie X pal¹cego siê drzewa
			int cY=cell.getCoordinates().y;
			N=getNeighnoursOnFire(cX,cY);	//obliczenie iloœci pal¹cych siê s¹siadów
			spDist=cell.getSpottingDistance(1+N);//wyiaczane za wzoru Albiniego
			/////Normalizacja
			if(spDist>=size)
				spDist=spDist-(spDist/size-1)*size-2;
			if(spDist<=size && spDist>=0){
				skalar=randomGenerator.nextInt((int)spDist)+1;//+1 ¿eby nie dostawaæ 0 z random
				//wspó³rzêdna komórki dla której wykonuje obliczenia + wspó³. kierunku wiartu* odleg³oœæ na któr¹ wyrzuci³o ga³¹Ÿ
				if(Y!=0)newY=cY+Y*skalar;else newY=cY;
				if(X!=0)newX=cX+X*skalar;else newX=cX;
				//System.out.println("newX"+newX+",newY"+newY+"\t Xc"+cX+",Yc"+cY+"\tSKALAR"+skalar+"\t wiatr X"+X+" Y"+Y);
	
				if(newX>0 && newY>0 && newY<size && newX<size){
					newOnFire.add(Terrain.this.getCell(newY,newX));
					//System.out.println("X "+cX+"Y "+cY+"  \t newX"+newX+",newY "+newY +"ok"+"\t"+"X"+Terrain.this.getCell(newY,newX).getCoordinates().x+"Y"+Terrain.this.getCell( newY,newX).getCoordinates().y+" skalar"+skalar+" wiatr X "+X+" Y "+Y);
	
				}
				else{
					Terrain.outBorderFire++;
				}
			}
			
		}
		for(Cell cello:newOnFire){
			if(cello.getState()==Cell.State.FUEL && cello.getState()!=Cell.State.BURNT && cello.getState()!=Cell.State.BURNING){
				//cello.setBurnthresholdToZero();
				//treesOnFireAdd.add(cell);
				cello.setBurnThreshold(0);
				cello.setState(Cell.State.BURNING);				
				//cello.setState(Cell.State.BURNING);
				//Terrain.treesOnFire.add(cello);
				System.out.println("Zapalam X,Y "+cello.getCoordinates().x+" ; "+cello.getCoordinates().y);
				//Terrain.treesOnFireAdd.add(cello);
				Terrain.spotingCount++;
			}
			
		}
		System.out.println("Poza zasiêgiem "+Terrain.outBorderFire+" Spotting fire"+Terrain.spotingCount);
	}
	
	private int getNeighnoursOnFire(int x,int y) {
		int n=0;
		if(x+1<Terrain.this.size && y+1<Terrain.this.size && x>1 && y>1){
			if(this.terrainState[x+1][y+1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x][y+1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x+1][y].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x-1][y-1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x-1][y].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x][y-1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x+1][y-1].getState()==Cell.State.BURNING)n=n+1;
			if(this.terrainState[x-1][y+1].getState()==Cell.State.BURNING)n=n+1;
		}
		return n;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int size = 6;
		//Terrain t = new Terrain(size,100,50);		
		
		//System.out.println(t.terrainState[2][2].neighbors);
		
		
		// testowanie
		Data.setDirection(Direction.S);
		Terrain t = new Terrain(5,100,(int)(Data.percent_oak),50,60);
		System.out.println(t.terrainState[2][2]);			
		System.out.println(t.terrainState[2][2].elipse(Direction.S.angle));
		while(!t.isAllBurnt())
		{
			System.out.println("-------------------------");		
			System.out.println(t.toString());
			
			t.spreadFire();			
		}
		
		System.out.println("-----------------------------");
		
		System.out.println("Cell: x=2 y=2");
		System.out.println("-----------");		
		System.out.println("E: " + t.terrainState[2][2].elipse(0));
		System.out.println("W: " + t.terrainState[2][2].elipse(180));
		System.out.println("N: " + t.terrainState[2][2].elipse(Data.Direction.N.angle));
		System.out.println("S: " + t.terrainState[2][2].elipse(Data.Direction.S.angle));
		
		
		
	}
		
	

	

}
