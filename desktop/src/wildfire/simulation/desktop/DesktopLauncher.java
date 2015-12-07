package wildfire.simulation.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import wildfire.simulation.Graphic;

public class DesktopLauncher {
   public static void main(String[] args) {
      LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
      config.title = "Wildfire";
      config.width = 900;
      config.height = 620;
      new LwjglApplication(new Graphic(), config);
   }
}
