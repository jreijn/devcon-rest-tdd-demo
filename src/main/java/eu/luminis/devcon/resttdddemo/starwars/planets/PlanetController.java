package eu.luminis.devcon.resttdddemo.starwars.planets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/planets")
public class PlanetController {

    private PlanetRepository planetRepository;

    @Autowired
    public PlanetController(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    @GetMapping
    public List<Planet> getAllPlanets() {
        return planetRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Planet createPlanet(@RequestBody Planet planet) {
        return planetRepository.save(planet);
    }
}
