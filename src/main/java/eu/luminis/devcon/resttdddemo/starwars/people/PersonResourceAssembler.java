package eu.luminis.devcon.resttdddemo.starwars.people;

import eu.luminis.devcon.resttdddemo.starwars.planets.PlanetRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Assembler for creating resources from people
 */
class PersonResourceAssembler extends ResourceAssemblerSupport<Person, PersonResource> {

    public PersonResourceAssembler() {
        super(PlanetRestController.class, PersonResource.class);
    }

    @Override
    public PersonResource toResource(Person person) {
        Link link = linkTo(methodOn(PeopleRestController.class, person.getId())
                .getPersonById(person.getId())).withSelfRel();
        return new PersonResource(person, link);
    }

    public Resources<PersonResource> toPersonResources(Iterable<? extends Person> entities) {
        List<PersonResource> personResources = super.toResources(entities);
        Link link = linkTo(methodOn(PeopleRestController.class)
                .getAllPeople()).withSelfRel();
        return new Resources<>(personResources, link);
    }

}
