package eu.luminis.devcon.resttdddemo.starwars.planets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlanetPatchInput {

    private final String name;
    private final Long population;

    @JsonCreator
    public PlanetPatchInput(@JsonProperty("name") String name, @JsonProperty("population") Long population) {
        this.name = name;
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public Long getPopulation() {
        return population;
    }
}
