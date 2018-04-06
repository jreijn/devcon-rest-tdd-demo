package eu.luminis.devcon.resttdddemo.starwars.planets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PlanetRestController {

    private PlanetRepository planetRepository;
    private PlanetResourceAssembler planetResourceAssembler = new PlanetResourceAssembler();

    @Autowired
    public PlanetRestController(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    @GetMapping("/planets")
    public ResponseEntity<Resources<PlanetResource>> getAllPlanets() {
        List<PlanetResource> planetResources = planetRepository.findAll().stream().map(p-> planetResourceAssembler.toResource(p)).collect(Collectors.toList());
        return new ResponseEntity<>(new Resources<>(planetResources), HttpStatus.OK);
    }

    @GetMapping("/planets/{id}")
    public ResponseEntity<PlanetResource> getPlanetById(@PathVariable Long id) {
        Planet one = planetRepository.findById(id).get();
        PlanetResource planetResource = planetResourceAssembler.toResource(one);
        return new ResponseEntity<>(planetResource, HttpStatus.OK);
    }

}
