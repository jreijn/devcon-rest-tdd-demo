package eu.luminis.devcon.resttdddemo;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.luminis.devcon.resttdddemo.starwars.people.Person;
import eu.luminis.devcon.resttdddemo.starwars.people.PersonRepository;
import eu.luminis.devcon.resttdddemo.starwars.planets.Planet;
import eu.luminis.devcon.resttdddemo.starwars.planets.PlanetRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.RequestDispatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static capital.scalable.restdocs.response.ResponseModifyingPreprocessors.limitJsonArrayLength;
import static capital.scalable.restdocs.response.ResponseModifyingPreprocessors.replaceBinaryContent;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StarWarsRestDocsTests {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private PersonRepository personRepository;

    private Planet planetFixture;
    private Person personFixture;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .alwaysDo(JacksonResultHandlers.prepareJackson(objectMapper))
                .alwaysDo(commonDocumentation())
                .apply(documentationConfiguration(this.restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080)
                        .and().snippets()
                        .withDefaults(CliDocumentation.curlRequest(),
                                HttpDocumentation.httpRequest(),
                                HttpDocumentation.httpResponse(),
                                AutoDocumentation.requestFields(),
                                AutoDocumentation.responseFields(),
                                AutoDocumentation.pathParameters(),
                                AutoDocumentation.requestParameters(),
                                AutoDocumentation.description(),
                                AutoDocumentation.methodAndPath(),
                                AutoDocumentation.section()))
                .build();
        planetRepository.deleteAll();
        planetRepository.saveAll(createPlanets());
        List<Planet> all = planetRepository.findAll();
        planetFixture = all.get(0);

        personRepository.deleteAll();
        personRepository.saveAll(createPeople());
        List<Person> people = personRepository.findAll();

        personFixture = people.get(0);

    }

    protected RestDocumentationResultHandler commonDocumentation() {
        return document("{class-name}/{method-name}",
                preprocessRequest(), commonResponsePreprocessor());
    }

    protected OperationResponsePreprocessor commonResponsePreprocessor() {
        return preprocessResponse(replaceBinaryContent(), limitJsonArrayLength(objectMapper),
                prettyPrint());
    }

    @Test
    public void headersExample() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void errorExample() throws Exception {
        this.mockMvc
                .perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/planets")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Validation failed. Field 'name' must not be null."))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void indexExample() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void testGetAllPlanets() throws Exception {
        mockMvc.perform(get("/planets")
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void testGetPlanet() throws Exception {
        mockMvc.perform(get("/planets/{id}", planetFixture.getId())
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void testAddPlanet() throws Exception {

        Planet p = new Planet();
        p.setName("Kamino");


        mockMvc.perform(post("/planets")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
        ;
    }

    @Test
    public void testUpdatePlanet() throws Exception {

        Map<String, Object> planet = new HashMap<>();
        planet.put("name", "Kamino");
        planet.put("population", 1000000000);

        mockMvc.perform(patch("/planets/{id}", planetFixture.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(planet)))
                .andExpect(status().isNoContent());
    }

    private List<Planet> createPlanets() {

        List<Planet> planets = new ArrayList<>();
        Planet planetAlderaan = new Planet("Alderaan");
        planetAlderaan.setId(1L);
        planetAlderaan.setPopulation(2000000000L);

        Planet planetNaboo = new Planet("Naboo");
        planetNaboo.setId(2L);
        planetNaboo.setPopulation(4500000000L);

        planets.add(planetAlderaan);
        planets.add(planetNaboo);
        return planets;
    }

    private List<Person> createPeople() {

        List<Person> people = new ArrayList<>();
        Person luke = new Person("Luke Skywalker");
        Person obiwan = new Person("Obi-Wan Kenobi");

        people.add(luke);
        people.add(obiwan);
        return people;
    }

    @Test
    public void testGetAllPeople() throws Exception {
        mockMvc.perform(get("/people")
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void testGetPerson() throws Exception {
        mockMvc.perform(get("/people/{id}", personFixture.getId()).accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void testAddPeople() throws Exception {

        Person p = new Person();
        p.setName("Kamino");


        mockMvc.perform(post("/people")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
        ;
    }

    @Test
    public void testUpdatePeople() throws Exception {

        Map<String, Object> person = new HashMap<>();
        person.put("name", "C-3PO");

        mockMvc.perform(patch("/people/{id}", personFixture.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isNoContent())
        ;
    }

}
