package wildfire.simulation;

public class DataTemplate {
    public DataSimulation simulation;
    public DataTerrain terrain;
    public DataVegetation vegetation;
    public DataWeather weather;

    class DataTerrain {
        public int a;
        public int b;
        public int roughness;
        public int height;
    }

    class DataVegetation {
        public String density;
        public String type;
        public String soil;
    }

    class DataSimulation {
        public int speed;
    }

    class DataWeather {
        public Double windVelocityMin;
        public Double windVelocityMax;
        public String windDirection1;
        public String windDirection2;
        public int humidity;
    }

    public void updateData(){
        //vegetation soil
        if (vegetation.soil.equals("dry")) {
            Data.soil = 0;
        } else if (vegetation.soil.equals("fertile")) {
            Data.soil = 150;
        } else {
            Data.soil = 50;
        }
        //vegetation probability
        if (vegetation.density.equals("open")) {
            Data.vegetation_probability = 20;
        } else if (vegetation.density.equals("sparse")) {
            Data.vegetation_probability = 50;
        } else {
            Data.vegetation_probability = 90;
        }
        //vegetation type
        if (vegetation.type.equals("mixed")) {
            Data.percent_oak = 50;
        } else if (vegetation.type.equals("needleleaf")) {
            Data.percent_oak = 15;
        } else {
            Data.percent_oak = 85;
        }
        //air humidity
        Data.setHumidity(weather.humidity);
        //wind
        Data.windInfo = new Wind(Data.Direction.valueOf(weather.windDirection1), Data.Direction.valueOf(weather.windDirection2),
                                weather.windVelocityMin, weather.windVelocityMax);
    }
}

