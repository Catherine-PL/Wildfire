package wildfire.simulation;

//import representation.View.Screen;

import wildfire.simulation.Data.Direction;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;


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
   public PerspectiveCamera camera;
   private OrthographicCamera cam;
   private BitmapFont font;
   private int screensizeY =620;
   private int broadLeafTypeProbablitity =50;
   private int vegetationProbablitity =50;
   public ModelBatch modelBatch;
   public Model modelFree;
   public Model modelOak;
   public Model modelPiny;
   public Model modelBurning;
   public Model modelBurnt;
   public ModelInstance instance;
   public ModelInstance instance2;
   public Environment environment;
   public CameraInputController camController;
   //enum do wyboru opcji zeby napisy byly wpisywane i wyswietlane odpowiednio
   public enum Choice {
	    GENERATE(10), T_AREA(0), T_ROUGHNESS(1), T_MAXIMUM_HEIGHT(2), W_VELOCITY(3),W_DIRECTION(4),W_HUMIDITY (5),NONE(9), GENERATE_EXAMPLE(10) ;
	    private final int value;
	    
	    private Choice(int value) {
	        this.value = value;
	    }
	    public int getValue() {
	        return value;
	    }
	}
   private Choice option;
   Terrain t = new Terrain(60,50,(int)(Data.percent_oak),50,60); //tutaj powinno byc od razu dane z ku¿ni
   	//rozmiar boku, prawdopodobienstwo ze cos rosnie, prawd. ze liœciaste, mask wysokosc, zroznicowanie terenu
   //TODO
   
   @Override
   public void create() {
	   //do 3D
	   ModelBuilder modelBuilder = new ModelBuilder();
       modelFree = modelBuilder.createBox(3f, 1f, 3f, 
               new Material(ColorAttribute.createDiffuse(Color.BROWN)),
               Usage.Position | Usage.Normal);
       modelOak = modelBuilder.createCone(3f, 3f, 3f,3, 
               new Material(ColorAttribute.createDiffuse(Color.GREEN)),
               Usage.Position | Usage.Normal);
       modelPiny = modelBuilder.createCone(3f, 3f, 3f,3, 
               new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
               Usage.Position | Usage.Normal);
       modelBurning = modelBuilder.createCone(3f, 3f, 3f,3, 
               new Material(ColorAttribute.createDiffuse(Color.RED)),
               Usage.Position | Usage.Normal);
       modelBurnt = modelBuilder.createCone(3f, 3f, 3f,3, 
               new Material(ColorAttribute.createDiffuse(Color.BLACK)),
               Usage.Position | Usage.Normal);
      //instance = new ModelInstance(model);
	   
	   
	   
	   
	   
	   texts[0]=new StringBuilder("100");
	   texts[1]=new StringBuilder("10");
	   texts[2]=new StringBuilder("5");
	   texts[3]=new StringBuilder("4");
	   texts[4]=new StringBuilder("NE");
	   texts[5]=new StringBuilder("72");
	   font = new BitmapFont();
	   
	   option=Choice.NONE;
	   Gdx.input.setInputProcessor(this);
	   background=new Texture("background.png"); 
	   guitext=new Texture("text.png"); 
	   treeRed = new Texture("treered.jpg"); 
	   noTree= new Texture("notree.jpg"); 
	   treeBlack = new Texture("treeblack.jpg"); 
	   treeGreen = new Texture("treegreen.jpg"); 
	   chosenDensity = new Texture("densityOpen.png"); 	
	   chosenType = new Texture("typeMixed.png"); 
      // create the camera and the SpriteBatch
	   cam = new OrthographicCamera();
	   cam.setToOrtho(false, 1300,620);
	   camera = new PerspectiveCamera(57, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
       camera.position.set(15f, 175f, 390f);
       camera.lookAt(50,-150,70);
       camera.near = 1f;
       camera.far = 600f;
       camera.update();
       camController = new CameraInputController(camera);
      batch = new SpriteBatch();   
      
      modelBatch = new ModelBatch();

      environment = new Environment();
      environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
      environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
      
      batch.setProjectionMatrix(cam.combined);
      
   }


   @Override
   public void render() {
	   try {
		    Thread.sleep(75);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
      Gdx.gl.glClearColor(0.84f, 0.90f, 0.44f, 1); 
      Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      Gdx.input.setInputProcessor(this);
      batchMenu();

      if (option.getValue() ==Choice.GENERATE.getValue())
      {

    	  batchSimulation3D();
    	  //batchSimulation();
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
   
   public void batchSimulation3D()
   {
	   Gdx.input.setInputProcessor(camController);
 	   camController.update();
 	   camera.update();
       modelBatch.begin(camera);
       //modelBatch.render(instance, environment);
       //drzewa of course
    //   ModelBuilder modelBuilder = new ModelBuilder();
      // model = modelBuilder.createCone(1f, 1f, 1f,3, 
        //       new Material(ColorAttribute.createDiffuse(Color.GREEN)),
          //     Usage.Position | Usage.Normal);
      //instance = new ModelInstance(model);
     
       
       //
       int margin=290;
	      for (int y = 0; y<t.size; y++)
			{
				for (int x = 0; x<t.size; x++)
				{
					//wybuduj podstawe ziemii
					
					for (int z = -2; z<t.getCell(y, x).getElevation()-1; z++)
					{
						instance2 = new ModelInstance(modelFree,3*y-80,  z,3*x);
						modelBatch.render(instance2, environment);
					}
					
					
					
					 //instance = new ModelInstance(model,y,  0,x);
					 //instance = new ModelInstance(model);
					
					if(t.getCell(y, x).getState() == Cell.State.FREE)
					{
						//instance = new ModelInstance(modelFree,3*y,  t.getCell(y, x).getElevation(),3*x);
						//modelBatch.render(instance, environment);

					}
					if((t.getCell(y, x).getState() == Cell.State.FUEL))
					{
					//	batch.draw(treeGreen, 10*y+margin, 10*x+10);
						if((t.getCell(y, x).getType() == Cell.Wood.OAK))
						{
							 instance = new ModelInstance(modelOak,3*y-80, t.getCell(y, x).getElevation() ,3*x);
							modelBatch.render(instance, environment);
		
						}
						if((t.getCell(y, x).getType() == Cell.Wood.PINY))
						{
							 instance = new ModelInstance(modelPiny,3*y-80,  t.getCell(y, x).getElevation(),3*x);
							modelBatch.render(instance, environment);
		
					
						}
					}
					if((t.getCell(y, x).getState() == Cell.State.BURNING))
					{
						 instance = new ModelInstance(modelBurning,3*y-80,  t.getCell(y, x).getElevation(),3*x);
						modelBatch.render(instance, environment);
	
					
					}
					if((t.getCell(y, x).getState() == Cell.State.BURNT))
					{
						 instance = new ModelInstance(modelBurnt,3*y-80,  t.getCell(y, x).getElevation(),3*x);
						modelBatch.render(instance, environment);
					
				
					}
				}
			}
	   modelBatch.end();  
	   if (t.isAllBurnt() == false) t.spreadFire();
       
       
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

			}
		   //jak wcisnie gdzies indziej not o automatycznie wpiswywanie gdzies indziej
		   //Generate
			if ((X>16) && (X<280)  &&(Y>screensizeY -544) && (Y<screensizeY -500))
			{
				option=Choice.GENERATE;
				//ustawienie terenu wed³ug podanych w³aœciwoœci
				t = new Terrain(Integer.parseInt(texts[0].toString()),vegetationProbablitity,broadLeafTypeProbablitity,Integer.parseInt(texts[2].toString()),Integer.parseInt(texts[1].toString()) );
				//ustawienie danych Data wed³ug podanych w³aœciwoœci
				Data.setWind(Double.parseDouble(texts[3].toString()));
				Data.setDirection(Direction.valueOf(texts[4].toString()));
				Data.setHumidity(Integer.parseInt(texts[5].toString()));
				
//TODO przepisanie zmiennych: z wpisanych do Data
			}
			//wybor opcji vegetation
			//TODO gdzie ten parametr w³o¿yæ
			if ((Y<screensizeY -244) && (Y>screensizeY -260) )
			{
				if ((X>33) && (X<85))
				{	
					 chosenDensity = new Texture("densitySparse.png"); 	
					 vegetationProbablitity=50;
					 
				}
				if ((X>136) && (X<177))
				{	
					 chosenDensity = new Texture("densityOpen.png"); 
					 vegetationProbablitity=20;
				}
				if ((X>212) && (X<255))
				{	
					 chosenDensity = new Texture("densityDense.png");
					 vegetationProbablitity=90;
				}
			}
			if ((Y< screensizeY -291) && (Y>screensizeY -308) )
			{
				if ((X>33) && (X<114))
				{	
					  chosenType = new Texture("typeNeedleleaf.png"); 
					  broadLeafTypeProbablitity=15;
				}
				if ((X>127) && (X<198))
				{	
					  chosenType = new Texture("typeBroadleaf.png"); 
					  broadLeafTypeProbablitity=85;
				}
				if ((X>210) && (X<255))
				{	
					  chosenType = new Texture("typeMixed.png"); 
					  broadLeafTypeProbablitity=50;
				}
			}
		   // wpisywanie
			if ((X>185) && (X<263))
			{				
				if ((Y<screensizeY -60) && (Y>screensizeY -80) )
				{
					option=Choice.T_AREA;
				}
				if ((Y<screensizeY -86) && (Y>screensizeY -108) )
				{
					option=Choice.T_ROUGHNESS;	
				}
				if ((Y<screensizeY -113) && (Y>screensizeY -135) )
				{
					option=Choice.T_MAXIMUM_HEIGHT;	
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
 		//modelBatch.dispose();
       // model.dispose();
       // batch.dispose();
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



