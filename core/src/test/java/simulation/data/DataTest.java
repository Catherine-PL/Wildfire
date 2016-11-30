package simulation.data;

import org.junit.Test;
import simulation.model.Direction;
import simulation.model.Wind;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Sebastian on 2016-11-30.
 */
public class DataTest {

    @Test
    public void testUpdateWind() throws Exception {
        //Given
        Data.windInfo = new Wind(Direction.S,Direction.S,Double.parseDouble("2"),Double.parseDouble("2"));

        //When
        Data.updateWind();

        //Then
        assertThat(Data.wind).isEqualTo(2D);
        assertThat(Data.winddir).isEqualTo(Direction.S);
    }
}