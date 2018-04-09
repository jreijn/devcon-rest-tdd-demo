package eu.luminis.devcon.resttdddemo.starwars.people;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonPatchInput {

    private final String name;

    @JsonCreator
    public PersonPatchInput(@JsonProperty("name") String name, @JsonProperty("population") Long population) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
