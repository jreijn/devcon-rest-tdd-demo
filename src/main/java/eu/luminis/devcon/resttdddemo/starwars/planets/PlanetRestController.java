package eu.luminis.devcon.resttdddemo.starwars.planets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@ExposesResourceFor(PlanetResource.class)
@RestController
public class PlanetRestController {

    private PlanetRepository planetRepository;
    private PlanetResourceAssembler planetResourceAssembler = new PlanetResourceAssembler();

    @Autowired
    public PlanetRestController(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    @GetMapping(value = "/planets", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity<Resources<PlanetResource>> getAllPlanets() {
        List<Planet> all = planetRepository.findAll();
        Resources<PlanetResource> planetResources = planetResourceAssembler.toPlanetResources(all);
        return new ResponseEntity<>(planetResources, HttpStatus.OK);
    }

    @PostMapping(value = "/planets", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity<PlanetResource> addPlanet(@RequestBody Planet planet) {
        Planet savedPlanet = planetRepository.save(planet);
        PlanetResource planetResource = planetResourceAssembler.toResource(savedPlanet);
        return ResponseEntity.created(URI.create(planetResource.getLink(Link.REL_SELF).getHref())).build();
    }

    @GetMapping(value = "/planets/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity<PlanetResource> getPlanetById(@PathVariable Long id) {
        Optional<Planet> planet = planetRepository.findById(id);
        if (planet.isPresent()) {
            PlanetResource planetResource = planetResourceAssembler.toResource(planet.get());
            return new ResponseEntity<>(planetResource, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
