package simulation.data;

import com.google.common.io.Resources;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import simulation.model.Direction;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Sebastian on 2016-11-30.
 */
public class DataTemplateTest {

    @Test
    public void shouldLoadDataFromJson() throws Exception {
        //Given
        Path json = Paths.get(Resources.getResource("example.json").toURI());

        //When
        DataTemplate dataTemplate = DataTemplate.loadFromFile(json);

        //Then
        assertThat(dataTemplate.simulation).hasFieldOrPropertyWithValue("speed", 1);
        assertThat(dataTemplate.terrain)
                .hasFieldOrPropertyWithValue("a", 10)
                .hasFieldOrPropertyWithValue("b", 100)
                .hasFieldOrPropertyWithValue("roughness", 10)
                .hasFieldOrPropertyWithValue("height", 5);

        assertThat(dataTemplate.vegetation)
                .hasFieldOrPropertyWithValue("density", "open")
                .hasFieldOrPropertyWithValue("type", "mixed")
                .hasFieldOrPropertyWithValue("soil", "dry");

        assertThat(dataTemplate.weather)
                .hasFieldOrPropertyWithValue("windVelocityMin", 2.0D)
                .hasFieldOrPropertyWithValue("windVelocityMax", 6.0D)
                .hasFieldOrPropertyWithValue("windDirection1", "N")
                .hasFieldOrPropertyWithValue("windDirection2", "N")
                .hasFieldOrPropertyWithValue("humidity", 72);
    }

    @Test
    public void testUpdateData() throws Exception {
        //Given
        DataTemplate dataTemplate = new DataTemplate();
        dataTemplate.weather = new TemplateWeather();
        dataTemplate.vegetation = new TemplateVegetation();

        dataTemplate.vegetation.type = "needleleaf";
        dataTemplate.weather.humidity = 80;
        dataTemplate.weather.windDirection1 = "N";
        dataTemplate.weather.windDirection2 = "N";
        dataTemplate.weather.windVelocityMin = 2.0D;
        dataTemplate.weather.windVelocityMax = 4.0D;

        //When
        dataTemplate.updateData();

        //Then
        assertThat(Data.soil).isEqualTo(50);
        assertThat(Data.vegetation_probability).isEqualTo(90);
        assertThat(Data.percent_oak).isEqualTo(15);
        assertThat(Data.air_humidity).isEqualTo(80);
        assertThat(Data.windInfo.getDirections()).isEqualTo(Pair.of(Direction.N, Direction.N));
        assertThat(Data.windInfo.getVelocities()).isEqualTo(Pair.of(2.0D, 4.0D));
    }
}