package spring.batch.weather.model;

import lombok.Data;

@Data
public class WeatherItem {
    private String id;
    private String name;
    private String state;
    private String country;
    private Coordinates coord;
}
