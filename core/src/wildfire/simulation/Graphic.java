package wildfire.simulation;

//import representation.View.Screen;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class Graphic implements ApplicationListener,  InputProcessor {
	
   private StringBuilder[] texts =new StringBuilder[6];	
   private Texture treeGreen;	
   private Texture chosenDensity;	
   private Texture chosenType;
   private Texture treeRed;	
   private Texture noTree;
   private Texture background;
   private Texture guitext;
   private Texture treeBlack;	
   private SpriteBatch batch;
   private OrthographicCamera camera;
   private BitmapFont font;
   private int screensizeY =620;
   //enum do wyboru opcji zeby napisy byly wpisywane i wyswietlane odpowiednio
   public enum Choice {
	    GENERATE(10), T_AREA(2), T_ROUGHNESS(0), T_MAXIMUM_HEIGHT(1), W_VELOCITY(3),W_DIRECTION(4),W_HUMIDITY (5),NONE(9), GENERATE_EXAMPLE(10) ;
	    private final int value;
	    private Choice(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
   private Choice option;
   Terrain t = new Terrain(60,50,50);
   
   @Override
   public void create() {
	   for(int i=0; i<6 ;i++)
	   {
		   texts[i]=new StringBuilder("");
	   }
	   font = new BitmapFont();
	   
	   option=Choice.NONE;
	   Gdx.input.setInputProcessor(this);
	   background=new Texture("background.png"); 
	   guitext=new Texture("text.png"); 
	   treeRed = new Texture("treered.jpg"); 
	   noTree= new Texture("notree.jpg"); 
	   treeBlack = new Texture("treeblack.jpg"); 
	   treeGreen = new Texture("treegreen.jpg"); 
	   chosenDensity = new Texture("none.png"); 	
	   chosenType = new Texture("none.png"); 
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
      batchMenu();
     // batchSimulation();
      if (option.getValue() ==Choice.GENERATE.getValue())
      {
    	  batchSimulation();
      }
 }

   public void batchMenu()
   {
	   batch.begin();
	   batch.draw(background, 0,0);
	   batch.draw(chosenDensity, 29,360);
	   batch.draw(chosenType, 29,308);
	   batch.draw(guitext, 0,0);
	   
	   font.draw(batch, texts[0].toString(), 188, screensizeY-65);	
	   font.draw(batch, texts[1].toString(), 188,  screensizeY-93);	
	   font.draw(batch, texts[2].toString(), 188,  screensizeY-120);	
	   font.draw(batch, texts[3].toString(), 188,  screensizeY-394);	
	   font.draw(batch, texts[4].toString(), 188,  screensizeY-422);	
	   font.draw(batch, texts[5].toString(), 188,  screensizeY-449);	
	   
	   
	   
	   batch.end();  
   }
   public void batchSimulation()
   {
	   batch.begin();
	  // batch.draw(background, 0,0);
	   int margin=290;
	      for (int y = 0; y<t.size; y++)
			{
				for (int x = 0; x<t.size; x++)
				{
					if(t.getCell(y, x).getState() == Cell.State.FREE)
					{
						batch.draw(noTree, 10*y+margin, 10*x+10);
					}
					if((t.getCell(y, x).getState() == Cell.State.FUEL))
					{
						batch.draw(treeGreen, 10*y+margin, 10*x+10);
					}
					if((t.getCell(y, x).getState() == Cell.State.BURNING))
					{
						batch.draw(treeRed, 10*y+margin, 10*x+10);
					}
					if((t.getCell(y, x).getState() == Cell.State.BURNT))
					{
						batch.draw(treeBlack, 10*y+margin, 10*x+10);
					}
				}
			}
	   batch.end();  
	   if (t.isAllBurnt() == false) t.spreadFire();
   }
   
	//overrides
   @Override
   public boolean keyDown(int keycode) {
	   if  ((option.getValue()!=Choice.GENERATE.getValue())&&(option.getValue()!=Choice.NONE.getValue()))
	   {
		   if ((keycode==Keys.BACKSPACE ) && (texts[option.getValue()].length()>0)) texts[option.getValue()].deleteCharAt(texts[option.getValue()].length()-1);
	       else if(keycode!=Keys.BACKSPACE) texts[option.getValue()].append(Input.Keys.toString(keycode));
	   }

   	return false;
   }

   @Override
   public boolean keyUp(int keycode) {
       return false;
   }

   @Override
   public boolean keyTyped(char character) {
       return false;
   }

   @Override
   public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	   if ((button == Buttons.LEFT) && (option.getValue()!=Choice.GENERATE.getValue())){
		   //option=Choice.GENERATE_EXAMPLE;
		   int X= screenX;
			int Y=screensizeY - screenY;
			   //Generate example
			if ((X>16) && (X<280)  &&(Y>screensizeY -600) && (Y<screensizeY -556))
			{
				option=Choice.GENERATE_EXAMPLE;
				//TODO jakoœ zmieniæ ¿eby wczytywane by³y dane KuŸni
			}
		   //jak wcisnie gdzies indziej not o automatycznie wpiswywanie gdzies indziej
		   //Generate
			if ((X>16) && (X<280)  &&(Y>screensizeY -544) && (Y<screensizeY -500))
			{
				option=Choice.GENERATE;
//TODO przepisanie zmiennych: z wpisanych do Data
			}
			//wybor opcji vegetation
			if ((Y<screensizeY -244) && (Y>screensizeY -260) )
			{
				if ((X>33) && (X<85))
				{	
					 chosenDensity = new Texture("densitySparse.png"); 	
					 
				}
				if ((X>136) && (X<177))
				{	
					 chosenDensity = new Texture("densityOpen.png"); 	
				}
				if ((X>212) && (X<255))
				{	
					 chosenDensity = new Texture("densityDense.png"); 	
				}
			}
			if ((Y< screensizeY -291) && (Y>screensizeY -308) )
			{
				if ((X>33) && (X<114))
				{	
					  chosenType = new Texture("typeNeedleleaf.png"); 
				}
				if ((X>127) && (X<198))
				{	
					  chosenType = new Texture("typeBroadleaf.png"); 
				}
				if ((X>210) && (X<255))
				{	
					  chosenType = new Texture("typeMixed.png"); 
				}
			}
		   // wpisywanie
			if ((X>185) && (X<263))
			{				
				if ((Y<screensizeY -60) && (Y>screensizeY -80) )
				{
					option=Choice.T_ROUGHNESS;
				}
				if ((Y<screensizeY -86) && (Y>screensizeY -108) )
				{
					option=Choice.T_MAXIMUM_HEIGHT;	
				}
				if ((Y<screensizeY -113) && (Y>screensizeY -135) )
				{
					option=Choice.T_AREA;	
				}
				if ((Y<screensizeY -387) && (Y>screensizeY -409) )
				{
					option=Choice.W_VELOCITY;	
				}
				if ((Y<screensizeY -414) && (Y>screensizeY -436) )
				{
					option=Choice.W_DIRECTION;	
				}
				if ((Y<screensizeY -441) && (Y>screensizeY -463) )
				{
					option=Choice.W_HUMIDITY;	
				}
			}
			
	   }
       return false;
   }

   @Override
   public boolean touchUp(int screenX, int screenY, int pointer, int button) {
       return false;
   }

   @Override
   public boolean touchDragged(int screenX, int screenY, int pointer) {
       return false;
   }

   @Override
   public boolean mouseMoved(int screenX, int screenY) {
       return false;
   }
   @Override
   public boolean scrolled(int amount) {
       return false;
   }
	
 //TODO na razie mnie to nie interesuje	
 	public void dispose() {
 	      // dispose of all the native resources
 		
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



