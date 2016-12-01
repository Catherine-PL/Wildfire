package simulation.data;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import simulation.model.Direction;
import simulation.model.Wind;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

/**
 * Class which represents transitive data. Used during loading files with data, especially json.
 */
public class DataTemplate {

    public TemplateSimulation simulation;
    public TemplateTerrain terrain;
    public TemplateVegetation vegetation;
    public TemplateWeather weather;

    public static DataTemplate loadFromFile(Path filePath) {
        DataTemplate result = new DataTemplate();
        Gson gson = new Gson();

        try {
            JsonReader jsonReader = new JsonReader(new FileReader(filePath.toString()));
            result = gson.fromJson(jsonReader, DataTemplate.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Update Data class with parameters from DataTemplate. It is required step to properly set data used in simulation.
     */
    public void updateData(){
        //vegetation soil
        if (vegetation.soil != null && vegetation.soil.equals("dry")) {
            Data.soil = 0;
        } else if (vegetation.soil != null && vegetation.soil.equals("fertile")) {
            Data.soil = 150;
        } else {
            Data.soil = 50;
        }
        //vegetation probability
        if (vegetation.density!= null && vegetation.density.equals("open")) {
            Data.vegetation_probability = 20;
        } else if (vegetation.density!= null && vegetation.density.equals("sparse")) {
            Data.vegetation_probability = 50;
        } else {
            Data.vegetation_probability = 90;
        }
        //vegetation type
        if (vegetation.type != null && vegetation.type.equals("mixed")) {
            Data.percent_oak = 50;
        } else if (vegetation.type != null && vegetation.type.equals("needleleaf")) {
            Data.percent_oak = 15;
        } else {
            Data.percent_oak = 85;
        }
        //air humidity
        Data.air_humidity = weather.humidity;
        //wind
        Data.windInfo = new Wind(Direction.valueOf(weather.windDirection1), Direction.valueOf(weather.windDirection2),
                                weather.windVelocityMin, weather.windVelocityMax);
    }
}

