package wildfire.simulation;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class GraphicUtils
{
    public static String displayDouble(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }
}
