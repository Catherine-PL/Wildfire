package wildfire.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/**
 * Models contains all models useful in rendering Wildfire Simulation
 *
 * @author Katarzyna
 */
public class Models {
    public Model modelFree;
    public Model modelNorth;
    public Model modelOak;
    public Model modelPiny;
    public Model modelBurning;
    public Model modelBurnt;

    /**
     * Initializes all models for 3D simulation
     */
    public Models() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelNorth = modelBuilder.createBox(5f, 3f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelFree = modelBuilder.createBox(3f, 1f, 3f,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelOak = modelBuilder.createCone(3f, 3f, 3f, 3,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelPiny = modelBuilder.createCone(3f, 3f, 3f, 3,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelBurning = modelBuilder.createCone(3f, 3f, 3f, 3,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelBurnt = modelBuilder.createCone(3f, 3f, 3f, 3,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }
}
