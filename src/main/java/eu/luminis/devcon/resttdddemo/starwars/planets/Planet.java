package eu.luminis.devcon.resttdddemo.starwars.planets;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Planet {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private long diameter;
    private long population;

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

    public long getDiameter() {
        return diameter;
    }

    public void setDiameter(long diameter) {
        this.diameter = diameter;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }
}
