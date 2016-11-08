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
	
	//do wyswietlania GUI
	private String assetsPath = "D:\\Biblioteka\\Studia\\VII semestr\\studio projektowe 2\\Wildfire\\core\\assets\\";

	private int simulationSpeed = 0;
	private int speedCounter = 0;
   private int screensizeY =620;
   private Texture treeGreen;	
   private Texture chosenDensity;	
   private Texture chosenType;
   private Texture treeRed;	
   private Texture noTree;
   private Texture background;
   private Texture guitext;
   private Texture treeBlack;	
   

   private int broadLeafTypeProbablitity =50;
   private int vegetationProbablitity =50;
   
   
   //do wy�wietlania modelu 3D
   public ModelBatch modelBatch;
   public Model modelFree;
	public Model modelNorth;
   public Model modelOak;
   public Model modelPiny;
   public Model modelBurning;
   public Model modelBurnt;
   public ModelInstance instance;
   public ModelInstance instance2;
   public Environment environment;
   public CameraInputController camController;
   private SpriteBatch batch;
   public PerspectiveCamera camera;
   private OrthographicCamera cam;
   private BitmapFont font;
   private StringBuilder[] texts =new StringBuilder[6];	//bufor do  wpisywania atrybut�w terenu
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

   //dane z Ku�ni - example
   Terrain t = new Terrain(100,150,50,(int)(Data.percent_oak),4,60); //tutaj powinno byc od razu dane z ku�ni
   
   @Override
   public void create() {
	   //do 3D

	   ModelBuilder modelBuilder = new ModelBuilder();
	   modelNorth = modelBuilder.createBox(5f, 3f, 5f,
			   new Material(ColorAttribute.createDiffuse(Color.BLACK)),
			   Usage.Position | Usage.Normal);
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
       //dwie kamery: cam do GUI, camera do modelu lasu
       cam = new OrthographicCamera();
	   cam.setToOrtho(false, 1300,620);
	   camera = new PerspectiveCamera(57, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
       camera.position.set(45f, 325f, 490f);
       camera.lookAt(20,-350,20);
       camera.near = 1f;
       camera.far = 800f;
       camera.update();
       camController = new CameraInputController(camera);
      batch = new SpriteBatch();   
      
      modelBatch = new ModelBatch();

      //srodowisko do cieni
      environment = new Environment();
      environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
      environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    
      batch.setProjectionMatrix(cam.combined);

      
	   //ladowanie tekstur i napisow poczatkowych
	   texts[0]=new StringBuilder("100");
	   texts[1]=new StringBuilder("10");
	   texts[2]=new StringBuilder("5");
	   texts[3]=new StringBuilder("4");
	   texts[4]=new StringBuilder("N");
	   texts[5]=new StringBuilder("72");
	   font = new BitmapFont();
	   
	   background=new Texture(assetsPath + "background.png");
	   guitext=new Texture(assetsPath + "text.png");
	   treeRed = new Texture(assetsPath + "treered.jpg");
	   noTree= new Texture(assetsPath + "notree.jpg");
	   treeBlack = new Texture(assetsPath + "treeblack.jpg");
	   treeGreen = new Texture(assetsPath + "treegreen.jpg");
	   chosenDensity = new Texture(assetsPath + "densityOpen.png");
	   chosenType = new Texture(assetsPath + "typeMixed.png");
      
	   option=Choice.NONE;
	   Gdx.input.setInputProcessor(this); //ustawienie odbierania klikniec myszy


      
   }


   @Override
   public void render() {
	   //kolor t�a mi�dzy renderami
      Gdx.gl.glClearColor(0.84f, 0.90f, 0.44f, 1); 
      Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      Gdx.input.setInputProcessor(this);
      batchMenu();
      if (option.getValue() ==Choice.GENERATE.getValue())
      {
		  batchSimulation3D();
      }
   }

   //metoda odpowiedzialna za wywietlanie menu
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
   
   //metoda odpowiedzialna za wywietlanie modelu lasu
   public void batchSimulation3D()
   {
	   Gdx.input.setInputProcessor(camController);
 	   camController.update();
 	   camera.update();
       modelBatch.begin(camera);

	   instance2 = new ModelInstance(modelNorth, 3*t.sizeY -70, 0, 3*t.sizeX/2);
	   modelBatch.render(instance2, environment);

	   for (int y = 0; y<t.sizeY; y++)
			{
				for (int x = 0; x<t.sizeX; x++)
				{
					//budowanie podstawe ziemii
					for (int z = -2; z<t.getCell(y, x).getElevation()-1; z++)
					{
						instance2 = new ModelInstance(modelFree,3*y-80,  z,3*x);
						modelBatch.render(instance2, environment);
					}
					//sadzenie drzew
					if((t.getCell(y, x).getState() == Cell.State.FUEL))
					{
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
	   //przejcie do kolejnego kroku poaru
	   if ((speedCounter >= 5*simulationSpeed) && t.isAllBurnt() == false) {
		   speedCounter = 0;
		   t.spreadFire();
	   } else {
		   speedCounter++;
	   }
   }
 
   //metoda do wywietlania symulacji w 2D. Aktualnie nieu - przeszliy na model trjwymiarowy
   public void batchSimulation()
   {
	   batch.begin();
	   int margin=290;
	      for (int y = 0; y<t.sizeY; y++)
			{
				for (int x = 0; x<t.sizeX; x++)
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
	   if (t.isAllBurnt() == false) {
		   t.spreadFire();
	   }
   }
   
	//obsuga wpisywania znakw w pola w GUI
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

   //obsuga klikni mysz w GUI
   @Override
   public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	   if ((button == Buttons.LEFT) && (option.getValue()!=Choice.GENERATE.getValue())){
		   int X= screenX;
			int Y=screensizeY - screenY;
			//wybrana opcja to Generate example - uruchamiamy przyk�ad z Ku�ni:
			if ((X>16) && (X<280)  &&(Y>screensizeY -600) && (Y<screensizeY -556))
			{
				option=Choice.GENERATE_EXAMPLE;
			}
		   //wybrana opcja to Generate:
			if ((X>16) && (X<280)  &&(Y>screensizeY -544) && (Y<screensizeY -500))
			{
				//ustawienie danych Data wed�ug podanych w�a�ciwo�c
				Data.setWind(Double.parseDouble(texts[3].toString()));
				Data.setDirection(Direction.valueOf(texts[4].toString()));
				Data.setHumidity(Integer.parseInt(texts[5].toString()));
				//ustawienie terenu wed�ug podanych w�a�ciwo�ci
				t = new Terrain(Integer.parseInt(texts[0].toString()),100,vegetationProbablitity,broadLeafTypeProbablitity,Integer.parseInt(texts[2].toString()),Integer.parseInt(texts[1].toString()) );
				option=Choice.GENERATE;
			}
			//wybor opcji vegetation
			if ((Y<screensizeY -244) && (Y>screensizeY -260) )
			{
				if ((X>33) && (X<85))
				{	
					 chosenDensity = new Texture(assetsPath + "densitySparse.png");
					 vegetationProbablitity=50;
					 
				}
				if ((X>136) && (X<177))
				{	
					 chosenDensity = new Texture(assetsPath + "densityOpen.png");
					 vegetationProbablitity=20;
				}
				if ((X>212) && (X<255))
				{	
					 chosenDensity = new Texture(assetsPath + "densityDense.png");
					 vegetationProbablitity=90;
				}
			}
			if ((Y< screensizeY -291) && (Y>screensizeY -308) )
			{
				if ((X>33) && (X<114))
				{	
					  chosenType = new Texture(assetsPath + "typeNeedleleaf.png");
					  broadLeafTypeProbablitity=15;
				}
				if ((X>127) && (X<198))
				{	
					  chosenType = new Texture(assetsPath + "typeBroadleaf.png");
					  broadLeafTypeProbablitity=85;
				}
				if ((X>210) && (X<255))
				{	
					  chosenType = new Texture(assetsPath + "typeMixed.png");
					  broadLeafTypeProbablitity=50;
				}
			}
		   // wpisywanie w pozosta�e pola - prze��czenie kontekstu
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



