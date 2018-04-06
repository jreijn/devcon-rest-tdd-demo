package eu.luminis.devcon.resttdddemo.starwars.planets;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class PlanetResourceAssembler extends ResourceAssemblerSupport<Planet, PlanetResource> {

    public PlanetResourceAssembler() {
        super(PlanetRestController.class, PlanetResource.class);
    }

    @Override
    public PlanetResource toResource(Planet planet) {
        return createResource(planet);
    }

    private PlanetResource createResource(Planet planet) {
        Link link = linkTo(methodOn(PlanetRestController.class, planet.getId())
                .getPlanetById(planet.getId())).withSelfRel();
        return new PlanetResource(planet,link);
    }
}
