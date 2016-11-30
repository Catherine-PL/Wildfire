package simulation;

import com.badlogic.gdx.graphics.Texture;

/**
 * Textures contains textures used in rendering GUI
 *
 * @author Katarzyna
 */
public class Textures {
    public Texture treeGreen;
    public Texture chosenDensity;
    public Texture chosenType;
    public Texture chosenSoil;
    public Texture treeRed;
    public Texture noTree;
    public Texture background;
    public Texture guitext;
    public Texture treeBlack;
    public Texture report;

    /**
     * This is constructor that initializes textures with files
     * @param assetsPath path to directory with files
     */
    public Textures(String assetsPath) {
        background = new Texture(assetsPath + "background2.png");
        guitext = new Texture(assetsPath + "text2.png");
        treeRed = new Texture(assetsPath + "treered.jpg");
        noTree = new Texture(assetsPath + "notree.jpg");
        treeBlack = new Texture(assetsPath + "treeblack.jpg");
        treeGreen = new Texture(assetsPath + "treegreen.jpg");
        chosenDensity = new Texture(assetsPath + "densityOpen.png");
        chosenType = new Texture(assetsPath + "typeMixed.png");
        chosenSoil = new Texture(assetsPath + "soilDry.png");
        report = new Texture(assetsPath + "report.png");
    }
}
