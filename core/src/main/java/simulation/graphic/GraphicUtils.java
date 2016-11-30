package simulation.graphic;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * GraphicUtils contains helper methods for GraphicController
 *
 * @author Katarzyna
 */
public class GraphicUtils {

    /**
     * Truncates double value to format #.##
     * @param value value to be truncated
     * @return string containing truncated value
     */
    public static String displayDouble(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }

    /**
     * Checks if given position is between two values
     * @param position x or y position
     * @param smaller smaller value to check against
     * @param bigger bigger value to check against
     * @return True if position is between smaller and bigger
     */
    public static boolean inBorders(int position, int smaller, int bigger) {
        if ((position > smaller) && (position < bigger)) {
            return true;
        }
        return false;
    }
}
