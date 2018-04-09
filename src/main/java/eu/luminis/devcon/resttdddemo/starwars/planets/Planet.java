package eu.luminis.devcon.resttdddemo.starwars.planets;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Planet {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 1)
    private String name;

    public Planet() {
    }

    public Planet(String name) {
        this.name = name;
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
}
