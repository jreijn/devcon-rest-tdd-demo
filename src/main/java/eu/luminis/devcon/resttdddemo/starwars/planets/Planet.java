package eu.luminis.devcon.resttdddemo.starwars.planets;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Planet {

    /**
     * Unique ID for this planet.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Name of this planet.
     */
    @NotNull
    @Size(min = 1)
    private String name;

    private Long population = 0L;

    public Planet() {
    }

    public Planet(String name) {
        this.name = name;
    }

    public Planet(String name, Long population) {
        this.name = name;
        this.population = population;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }
}
