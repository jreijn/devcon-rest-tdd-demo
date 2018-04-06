package eu.luminis.devcon.resttdddemo;

import eu.luminis.devcon.resttdddemo.starwars.planets.Planet;
import eu.luminis.devcon.resttdddemo.starwars.planets.PlanetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.core.EvoInflectorRelProvider;

import java.util.Arrays;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
public class RestTddDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestTddDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner init(PlanetRepository planetRepository) {
        return (args) ->
                Arrays.asList("Alderaan,Bespin,Naboo,Coruscant,Kamino,Geonosis".split(","))
                        .forEach(planetName -> {
                            planetRepository.save(new Planet(planetName));
                        });
    }

    @Bean
    public RelProvider relProvider() {
        return new EvoInflectorRelProvider();
    }
}
