package wildfire.simulation;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import wildfire.simulation.Data.Direction;

/**
 * GraphicController controls GUI and rendering simulation
 *
 * @author Katarzyna
 */
public class GraphicController implements ApplicationListener, InputProcessor {
    private String assetsPath = "D:\\Biblioteka\\Studia\\VII semestr\\studio projektowe 2\\Wildfire\\core\\assets\\";
    private Textures textures;
    private Simulation simulation;
    private Models models;
    private int screensizeY = 620;

    public ModelBatch modelBatch;
    public ModelInstance instance;
    public ModelInstance instance2;
    public Environment environment;
    public CameraInputController camController;
    private SpriteBatch batch;
    public PerspectiveCamera camera;
    private BitmapFont font;
    private StringBuilder[] texts = new StringBuilder[6];    //bufor do  wpisywania atrybut�w terenu

    /**
     * Available options in GUI
     */
    public enum Choice {
        GENERATE(10), T_AREA(0), T_ROUGHNESS(1), T_MAXIMUM_HEIGHT(2), W_VELOCITY(3), W_DIRECTION(4), W_HUMIDITY(5), NONE(9), GENERATE_EXAMPLE(10), FINISHED(11);
        private final int value;

        Choice(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private Choice option;

    //dane z Ku�ni - example
    Terrain t = new Terrain(100, 150, Data.vegetation_probability, (int) (Data.percent_oak), 4, 60); //tutaj powinno byc od razu dane z ku�ni

    /**
     * Initialization of all necessary fields before rendering
     */
    @Override
    public void create() {
        //do 3D
        textures = new Textures(assetsPath);
        simulation = new Simulation();
        models = new Models();

        //dwie kamery: cam do GUI, camera do modelu lasu
        OrthographicCamera cam = new OrthographicCamera();
        initCameras(cam);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        modelBatch = new ModelBatch();

        //srodowisko do cieni
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));


        //ladowanie tekstur i napisow poczatkowych
        texts[0] = new StringBuilder("100");
        texts[1] = new StringBuilder("10");
        texts[2] = new StringBuilder("5");
        texts[3] = new StringBuilder("4");
        texts[4] = new StringBuilder("N");
        texts[5] = new StringBuilder("72");
        font = new BitmapFont();

        option = Choice.NONE;
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Continuous rendering of elements according to User's choice
     */
    @Override
    public void render() {
        //kolor t�a mi�dzy renderami
        Gdx.gl.glClearColor(0.84f, 0.90f, 0.44f, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.input.setInputProcessor(this);
        batchMenu();
        if (option.getValue() == Choice.GENERATE.getValue()) {
            batchSimulation3D();
        }
        if (option.getValue() == Choice.FINISHED.getValue()) {
            batchSimulation3D();
            batchReport();
        }
    }

    /**
     * Renders report after simulation end
     */
    public void batchReport() {
        batch.begin();
        batch.draw(textures.report, 460, 60);
        font.draw(batch, "SIMULATION FINISHED", 480, 210);
        font.draw(batch, "Simulation time: " + simulation.getSimulationTime() + " seconds", 480, 180);
        font.draw(batch, "Trees total: " + t.fuelCount, 480, 150);
        font.draw(batch, "Trees alive: " + (t.fuelCount - t.getFuelBurntCount()) + ", which is " + GraphicUtils.displayDouble(t.getFuelAlivePercent()) + " percent of total", 480, 120);
        font.draw(batch, "Trees burnt: " + t.getFuelBurntCount() + ", which is " + GraphicUtils.displayDouble(t.getFuelBurntPercent()) + " percent of total", 480, 90);
        batch.end();
    }

    /**
     * Renders menu
     */
    public void batchMenu() {
        batch.begin();
        batch.draw(textures.background, 0, 0);
        batch.draw(textures.chosenDensity, 29, 360);
        batch.draw(textures.chosenType, 29, 308);
        batch.draw(textures.guitext, 0, 0);
        font.draw(batch, texts[0].toString(), 188, screensizeY - 65);
        font.draw(batch, texts[1].toString(), 188, screensizeY - 93);
        font.draw(batch, texts[2].toString(), 188, screensizeY - 120);
        font.draw(batch, texts[3].toString(), 188, screensizeY - 394);
        font.draw(batch, texts[4].toString(), 188, screensizeY - 422);
        font.draw(batch, texts[5].toString(), 188, screensizeY - 449);
        batch.end();
    }

    /**
     * Renders terrain with trees during and after simulation
     */
    public void batchSimulation3D() {
        simulation.startTimer();
        Gdx.input.setInputProcessor(camController);
        camController.update();
        camera.update();
        modelBatch.begin(camera);

        instance2 = new ModelInstance(models.modelNorth, 3 * t.sizeY - 70, 0, 3 * t.sizeX / 2);
        modelBatch.render(instance2, environment);

        for (int y = 0; y < t.sizeY; y++) {
            for (int x = 0; x < t.sizeX; x++) {
                renderGround(y, x);
                renderTree(y, x);
            }
        }
        modelBatch.end();
        //przejcie do kolejnego kroku poaru
        if (t.isAllBurnt()) {
            if (!option.equals(Choice.FINISHED)) {
                simulation.stopTimer();
            }
            option = Choice.FINISHED;
        } else if (simulation.readyNextStep() && !t.isAllBurnt()) {
            Data.updateWind();
            t.spreadFire();
        }
    }

    /**
     * Helper method for rendering ground
     */
    private void renderGround(int y, int x) {
        for (int z = -2; z < t.getCell(y, x).getElevation() - 1; z++) {
            instance2 = new ModelInstance(models.modelFree, 3 * y - 80, z, 3 * x);
            modelBatch.render(instance2, environment);
        }
    }

    /**
     * Helper method for rendering trees on the ground
     */
    private void renderTree(int y, int x) {
        if ((t.getCell(y, x).getState() == Cell.State.FUEL)) {
            if ((t.getCell(y, x).getType() == Cell.Wood.OAK)) {
                instance = new ModelInstance(models.modelOak, 3 * y - 80, t.getCell(y, x).getElevation(), 3 * x);
                modelBatch.render(instance, environment);
            }
            if ((t.getCell(y, x).getType() == Cell.Wood.PINY)) {
                instance = new ModelInstance(models.modelPiny, 3 * y - 80, t.getCell(y, x).getElevation(), 3 * x);
                modelBatch.render(instance, environment);
            }
        }
        if ((t.getCell(y, x).getState() == Cell.State.BURNING)) {
            instance = new ModelInstance(models.modelBurning, 3 * y - 80, t.getCell(y, x).getElevation(), 3 * x);
            modelBatch.render(instance, environment);
        }
        if ((t.getCell(y, x).getState() == Cell.State.BURNT)) {
            instance = new ModelInstance(models.modelBurnt, 3 * y - 80, t.getCell(y, x).getElevation(), 3 * x);
            modelBatch.render(instance, environment);
        }
    }

    /**
     * Initializes position of cameras - menu camera and simulation (perspective) camera
     */
    private void initCameras(OrthographicCamera cam) {
        cam.setToOrtho(false, 1300, 620);
        camera = new PerspectiveCamera(57, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(45f, 325f, 490f);
        camera.lookAt(20, -350, 20);
        camera.near = 1f;
        camera.far = 800f;
        camera.update();
        camController = new CameraInputController(camera);
    }


    /**
     * Catches and renders text written in fields by User
     */
    @Override
    public boolean keyDown(int keycode) {
        if ((option.getValue() != Choice.GENERATE.getValue()) && (option.getValue() != Choice.NONE.getValue())) {
            if ((keycode == Keys.BACKSPACE) && (texts[option.getValue()].length() > 0))
                texts[option.getValue()].deleteCharAt(texts[option.getValue()].length() - 1);
            else if (keycode != Keys.BACKSPACE) texts[option.getValue()].append(Input.Keys.toString(keycode));
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

    /**
     * Discovers mouse touches on menu and selects appropriate fields
     * @return False if no problems occured after touchdown
     */
    @Override
    public boolean touchDown(int X, int screenY, int pointer, int button) {
        if ((button == Buttons.LEFT) && (option.getValue() != Choice.GENERATE.getValue())) {
            int Y = screensizeY - screenY;
            //option GENERATE_EXAMPLE
            if (GraphicUtils.inBorders(X, 16, 280) && GraphicUtils.inBorders(Y, screensizeY - 600, screensizeY - 556)) {
                option = Choice.GENERATE_EXAMPLE;
            }
            //option GENERATE
            if (GraphicUtils.inBorders(X, 16, 280) && GraphicUtils.inBorders(Y, screensizeY - 544, screensizeY - 500)) {
                Data.setWindVelocity(Double.parseDouble(texts[3].toString()));
                Data.setDirection(Direction.valueOf(texts[4].toString()));
                Data.setHumidity(Integer.parseInt(texts[5].toString()));
                t = new Terrain(Integer.parseInt(texts[0].toString()), 10, Data.vegetation_probability, (int) (Data.percent_oak), Integer.parseInt(texts[2].toString()), Integer.parseInt(texts[1].toString()));
                option = Choice.GENERATE;
            }
            //option VEGETATION
            if (GraphicUtils.inBorders(Y, screensizeY - 260, screensizeY - 244)) {
                if (GraphicUtils.inBorders(X, 33, 85)) {
                    textures.chosenDensity = new Texture(assetsPath + "densitySparse.png");
                    Data.vegetation_probability = 50;
                }
                if (GraphicUtils.inBorders(X, 136, 177)) {
                    textures.chosenDensity = new Texture(assetsPath + "densityOpen.png");
                    Data.vegetation_probability = 20;
                }
                if (GraphicUtils.inBorders(X, 212, 255)) {
                    textures.chosenDensity = new Texture(assetsPath + "densityDense.png");
                    Data.vegetation_probability = 90;
                }
            }
            if (GraphicUtils.inBorders(Y, screensizeY - 308, screensizeY - 291)) {
                if (GraphicUtils.inBorders(X, 33, 114)) {
                    textures.chosenType = new Texture(assetsPath + "typeNeedleleaf.png");
                    Data.percent_oak = 15;
                }
                if (GraphicUtils.inBorders(X, 127, 198)) {
                    textures.chosenType = new Texture(assetsPath + "typeBroadleaf.png");
                    Data.percent_oak = 85;
                }
                if (GraphicUtils.inBorders(X, 210, 255)) {
                    textures.chosenType = new Texture(assetsPath + "typeMixed.png");
                    Data.percent_oak = 50;
                }
            }
            // writable options
            if (GraphicUtils.inBorders(X, 185, 263)) {
                if (GraphicUtils.inBorders(Y, screensizeY - 80, screensizeY - 60)) {
                    option = Choice.T_AREA;
                }
                if (GraphicUtils.inBorders(Y, screensizeY - 108, screensizeY - 86)) {
                    option = Choice.T_ROUGHNESS;
                }
                if (GraphicUtils.inBorders(Y, screensizeY - 135, screensizeY - 113)) {
                    option = Choice.T_MAXIMUM_HEIGHT;
                }
                if (GraphicUtils.inBorders(Y, screensizeY - 409, screensizeY - 387)) {
                    option = Choice.W_VELOCITY;
                }
                if (GraphicUtils.inBorders(Y, screensizeY - 436, screensizeY - 414)) {
                    option = Choice.W_DIRECTION;
                }
                if (GraphicUtils.inBorders(Y, screensizeY - 463, screensizeY - 4417)) {
                    option = Choice.W_HUMIDITY;
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