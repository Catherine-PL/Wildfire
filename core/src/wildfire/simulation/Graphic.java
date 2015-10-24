package 
package wildfire.simulation;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Graphic implements ApplicationListener {
   private Texture treeGreen;	
   private Texture treeRed;	
   private Texture noTree;	
   private Texture treeBlack;	
   private SpriteBatch batch;
   private OrthographicCamera camera;

   Board board = new Board(60,50);
   
   @Override
   public void create() {
	   treeRed = new Texture(Gdx.files.internal("treered.png")); 
	   noTree= new Texture(Gdx.files.internal("notree.png")); 
	   treeBlack = new Texture(Gdx.files.internal("treeblack.png")); 
	   treeGreen = new Texture(Gdx.files.internal("treegreen.png")); 
      // create the camera and the SpriteBatch
      camera = new OrthographicCamera();
      camera.setToOrtho(false, 500,500);
      batch = new SpriteBatch();
      board.ignite();
   }


   @Override
   public void render() {
	   try {
		    Thread.sleep(100);                 //1000 milliseconds is one second.
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
      for (int k = 0; k<board.size; k++)
		{
			for (int l = 0; l<board.size; l++)
			{
				if(board.boardState[k][l].isTree==false)
				{
					//batch.draw(noTree, 10*k-10, 10*l-10);
				}
				if((board.boardState[k][l].isTree==true) &&(board.boardState[k][l].isBurning == false)&&(board.boardState[k][l].burnt == false))
				{
					batch.draw(treeGreen, 10*k-10, 10*l-10);
				}
				if((board.boardState[k][l].isTree==true) &&(board.boardState[k][l].isBurning == true)&&(board.boardState[k][l].burnt == false))
				{
					batch.draw(treeRed, 10*k-10, 10*l-10);
				}
				if((board.boardState[k][l].burnt == true)&&(board.boardState[k][l].isTree==true))
				{
					batch.draw(treeBlack, 10*k-10, 10*l-10);
				}
			}
		}
		//System.out.println(sum + " " + board.size*board.size);
      batch.end();  
      if (board.isAllBurnt()==false) board.spreadFire();
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
