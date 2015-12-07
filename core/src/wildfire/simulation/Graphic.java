package wildfire.simulation;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Graphic implements ApplicationListener {
   private Texture treeGreen;	
   private Texture treeRed;	
   private Texture noTree;	
   private Texture treeBlack;	
   private SpriteBatch batch;
   private OrthographicCamera camera;

   Terrain t = new Terrain(60,50,50);
   
   @Override
   public void create() {
	   treeRed = new Texture("treered.jpg"); 
	   noTree= new Texture("notree.jpg"); 
	   treeBlack = new Texture("treeblack.jpg"); 
	   treeGreen = new Texture("treegreen.jpg"); 
      // create the camera and the SpriteBatch
      camera = new OrthographicCamera();
      camera.setToOrtho(false, 900,620);
      batch = new SpriteBatch();      

   }


   @Override
   public void render() {
	   try {
		    Thread.sleep(75);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}

	  
      // clear the screen with a dark blue color. The
      // arguments to glClearColor are the red, green
      // blue and alpha component in the range [0,1]
      // of the color to be used to clear the screen.
      Gdx.gl.glClearColor(0.84f, 0.90f, 0.44f, 1); 
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 
      // tell the camera to update its matrices.
      camera.update();

      // tell the SpriteBatch to render in the
      // coordinate system specified by the camera.
      batch.setProjectionMatrix(camera.combined);

      // begin a new batch and draw the bucket and
      // all trees
      batch.begin();
      for (int y = 0; y<t.size; y++)
		{
			for (int x = 0; x<t.size; x++)
			{
				if(t.getCell(y, x).getState() == Cell.State.FREE)
				{
					batch.draw(noTree, 10*y+10, 10*x+10);
				}
				if((t.getCell(y, x).getState() == Cell.State.FUEL))
				{
					batch.draw(treeGreen, 10*y+10, 10*x+10);
				}
				if((t.getCell(y, x).getState() == Cell.State.BURNING))
				{
					batch.draw(treeRed, 10*y+10, 10*x+10);
				}
				if((t.getCell(y, x).getState() == Cell.State.BURNT))
				{
					batch.draw(treeBlack, 10*y+10, 10*x+10);
				}
			}
		}
		//System.out.println(sum + " " + board.size*board.size);
    batch.end();  
    if (t.isAllBurnt() == false) t.spreadFire();
 }


   @Override
   public void dispose() {
      // dispose of all the native resources
   
      batch.dispose();
   }

   @Override
   public void resize(int width, int height) {
   }

   @Override
   public void pause() {
   }

   @Override
   public void resume() {
   }
}
