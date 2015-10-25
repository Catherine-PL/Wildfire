package wildfire.simulation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import wildfire.simulation.Graphic;

public class DesktopLauncher {
   public static void main(String[] args) {
      LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
      config.title = "Drop";
      config.width = 500;
      config.height = 500;
      new LwjglApplication(new Graphic(), config);
   }
}
