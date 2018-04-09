package eu.luminis.devcon.resttdddemo;

import com.google.common.collect.Lists;
import eu.luminis.devcon.resttdddemo.starwars.people.Person;
import eu.luminis.devcon.resttdddemo.starwars.people.PersonRepository;
import eu.luminis.devcon.resttdddemo.starwars.planets.Planet;
import eu.luminis.devcon.resttdddemo.starwars.planets.PlanetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.core.EvoInflectorRelProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Arrays;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
@EnableSwagger2
public class RestTddDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestTddDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner initPlanets(PlanetRepository planetRepository) {
        return (args) ->
                Arrays.asList("Alderaan,Bespin,Naboo,Coruscant,Kamino,Geonosis".split(","))
                        .forEach(planetName -> {
                            planetRepository.save(new Planet(planetName));
                        });
    }

    @Bean
    CommandLineRunner initPeople(PersonRepository personRepository) {
        return (args) ->
                Arrays.asList("Luke Skywalker,C-3PO,R2-D2,Darth Vader,Leia Organa,Obi-Wan Kenobi".split(","))
                        .forEach(name -> {
                            personRepository.save(new Person(name));
                        });
    }

    @Bean
    public RelProvider relProvider() {
        return new EvoInflectorRelProvider();
    }

    @Bean
    public Docket petApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("eu.luminis.devcon"))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(true);
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .validatorUrl(null)
                .build();
    }
}
