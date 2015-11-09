package wildfire.simulation;

public class Ellipse {
	
	class Coordinate{
		int x;
		int y;
		
		Coordinate(int _x, int _y)
		{
			x = _x;
			y = _y;
		}
	}
	
	
	Coordinate xy [] = new Coordinate[8];
	
	Ellipse()
	{
		xy[0] = new Coordinate(-1,-1);		// x - rosn¹ w prawo; y - w dó³
		xy[1] = new Coordinate(0,-1);
		xy[2] = new Coordinate(1,-1);
		xy[3] = new Coordinate(-1,0);
		xy[4] = new Coordinate(1,0);
		xy[5] = new Coordinate(-1,1);
		xy[6] = new Coordinate(0,1);
		xy[7] = new Coordinate(-1,1);		
	}

}
