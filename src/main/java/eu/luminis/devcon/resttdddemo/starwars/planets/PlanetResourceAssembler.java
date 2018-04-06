package eu.luminis.devcon.resttdddemo.starwars.planets;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Assembler for creating resources from planets
 */
class PlanetResourceAssembler extends ResourceAssemblerSupport<Planet, PlanetResource> {

    public PlanetResourceAssembler() {
        super(PlanetRestController.class, PlanetResource.class);
    }

    @Override
    public PlanetResource toResource(Planet planet) {
        Link link = linkTo(methodOn(PlanetRestController.class, planet.getId())
                .getPlanetById(planet.getId())).withSelfRel();
        return new PlanetResource(planet, link);
    }

    public Resources<PlanetResource> toPlanetResources(Iterable<? extends Planet> entities) {
        List<PlanetResource> planetResources = super.toResources(entities);
        Link link = linkTo(methodOn(PlanetRestController.class)
                .getAllPlanets()).withSelfRel();
        return new Resources<>(planetResources, link);
    }

}
