package eu.luminis.devcon.resttdddemo.starwars.people;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.Relation;

@Relation(collectionRelation = "people")
public class PersonResource extends Resource<Person> {

    public PersonResource(Person content, Link... links) {
        super(content, links);
    }

}
