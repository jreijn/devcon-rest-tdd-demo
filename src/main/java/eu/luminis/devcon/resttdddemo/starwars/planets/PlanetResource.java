package eu.luminis.devcon.resttdddemo.starwars.planets;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.Relation;

@Relation(collectionRelation="planets")
public class PlanetResource extends Resource {

    public PlanetResource(Object content, Link... links) {
        super(content, links);
    }

}
