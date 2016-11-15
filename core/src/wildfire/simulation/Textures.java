package wildfire.simulation;

import com.badlogic.gdx.graphics.Texture;
import javafx.scene.control.TextField;

/**
 * Created by USER on 2016-11-15.
 */
public class Textures
{
    public Texture treeGreen;
    public Texture chosenDensity;
    public Texture chosenType;
    public Texture treeRed;
    public Texture noTree;
    public Texture background;
    public Texture guitext;
    public Texture treeBlack;
    public Texture report;

    public Textures(String assetsPath) {
        background=new Texture(assetsPath + "background.png");
        guitext=new Texture(assetsPath + "text.png");
        treeRed = new Texture(assetsPath + "treered.jpg");
        noTree= new Texture(assetsPath + "notree.jpg");
        treeBlack = new Texture(assetsPath + "treeblack.jpg");
        treeGreen = new Texture(assetsPath + "treegreen.jpg");
        chosenDensity = new Texture(assetsPath + "densityOpen.png");
        chosenType = new Texture(assetsPath + "typeMixed.png");
        report = new Texture(assetsPath + "report.png");
    }
}
