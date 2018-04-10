package eu.luminis.devcon.resttdddemo.starwars.people;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Person {

    /**
     * The id of the person
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the person
     */
    @NotNull
    @Size(min = 1)
    private String name;

    public Person() {
    }

    public Person(String name) {
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
