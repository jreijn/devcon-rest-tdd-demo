package eu.luminis.devcon.resttdddemo;

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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.RequestDispatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
                .apply(documentationConfiguration(this.restDocumentation)
                        .operationPreprocessors()
                        .withResponseDefaults(prettyPrint()))
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

    @Test
    public void headersExample() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andDo(document("headers-example",
                        responseHeaders(
                                headerWithName("Content-Type")
                                        .description("The Content-Type of the payload, e.g. `application/hal+json`")
                        )));
    }

    @Test
    public void errorExample() throws Exception {
        this.mockMvc
                .perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/planets")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Validation failed. Field 'name' must not be null."))
                .andExpect(status().isBadRequest())
                .andDo(document("error-example",
                        responseFields(
                                fieldWithPath("error").description("The HTTP error that occurred, e.g. `Not Found`"),
                                fieldWithPath("message").description("A description of the cause of the error"),
                                fieldWithPath("path").description("The path to which the request was made"),
                                fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
                                fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred"))));
    }

    @Test
    public void indexExample() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andDo(document("index-example",
                        links(
                                linkWithRel("self").description("The resource itself"),
                                linkWithRel("planets").description("The <<resources-planets,Planets resource>>"),
                                linkWithRel("people").description("The <<resources-people,People resource>>")
                        ),

                        responseFields(
                                subsectionWithPath("_links").description("<<resources-index-links,Links>> to other resources"))));
    }

    @Test
    public void testGetAllPlanets() throws Exception {
        mockMvc.perform(get("/planets")
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("planets-list-example",
                        links(halLinks(),
                                linkWithRel("self").ignored()
                        ),
                        responseFields(
                                subsectionWithPath("_embedded.planets").description("A list of <<planets, Planet resources>>"),
                                subsectionWithPath("_links").description("")
                        )
                ));
    }

    @Test
    public void testGetPlanet() throws Exception {
        mockMvc.perform(get("/planets/{id}", planetFixture.getId())
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(document("planet-get-example",
                        pathParameters(
                                parameterWithName("id").description("Planet's id")
                        ),
                        links(halLinks(),
                                linkWithRel("self").ignored()
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of the planet"),
                                fieldWithPath("name").description("Name of the planet"),
                                fieldWithPath("population").description("Planet's population"),
                                subsectionWithPath("_links").ignored()
                        )))
                .andExpect(status().isOk());
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
                .andDo(document("planets-create-example",
                        requestFields(
                                fieldWithPath("id").description("Planet's ID"),
                                fieldWithPath("name").description("Planet's name"),
                                fieldWithPath("population").description("Planet's population").type(JsonFieldType.NUMBER)
                        )
                ));
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
                .andExpect(status().isNoContent())
                .andDo(document("planet-update-example",
                        requestFields(
                                fieldWithPath("name").description("Planet's name"),
                                fieldWithPath("population").description("Planet's population")
                        )
                ));
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
                .andDo(document("people-list-example",
                        links(halLinks(),
                                linkWithRel("self").ignored()
                        ),
                        responseFields(
                                subsectionWithPath("_embedded.persons").description("A list of <<people, Person resources>>"),
                                subsectionWithPath("_links").description("")
                        )
                ));
    }

    @Test
    public void testGetPerson() throws Exception {
        mockMvc.perform(get("/people/{id}", personFixture.getId()).accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(document("people-get-example",
                        pathParameters(
                                parameterWithName("id").description("Person's id")
                        ),
                        links(halLinks(),
                                linkWithRel("self").ignored()
                        ),
                        responseFields(
                                fieldWithPath("id").description("Person's id"),
                                fieldWithPath("name").description("Person's name"),
                                subsectionWithPath("_links").ignored()
                        )))
                .andExpect(status().isOk());
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
                .andDo(document("people-create-example",
                        requestFields(
                                fieldWithPath("id").description("Person's ID"),
                                fieldWithPath("name").description("Person's name")
                        )
                ));
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
                .andDo(document("people-update-example",
                        requestFields(
                                fieldWithPath("name").description("Person's name")
                        )
                ));
    }

}
