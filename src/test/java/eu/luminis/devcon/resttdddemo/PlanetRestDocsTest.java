package eu.luminis.devcon.resttdddemo;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanetRestDocsTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private PlanetRepository planetRepository;

    private Planet planetFixture;

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

    }

    protected List<Planet> createPlanets() {

        List<Planet> planets = new ArrayList<>();
        Planet planetAlderaan = new Planet("Alderaan");
        planetAlderaan.setId(1l);
        Planet planetNaboo = new Planet("Naboo");
        planetNaboo.setId(2l);

        planets.add(planetAlderaan);
        planets.add(planetNaboo);
        return planets;
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/planets")
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("planets/get",
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
        mockMvc.perform(get("/planets/{id}", planetFixture.getId()).accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(document("planets/get-by-id",
                        pathParameters(
                          parameterWithName("id").description("Planet's id")
                        ),
                        links(halLinks(),
                                linkWithRel("self").ignored()
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of the planet"),
                                fieldWithPath("name").description("Name of the planet"),
                                subsectionWithPath("_links").ignored()
                        )))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddPlanet() throws Exception {

        Planet p = new Planet();
        p.setName("Kamino");

        ConstrainedFields fields = new ConstrainedFields(Planet.class);

        mockMvc.perform(post("/planets")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andDo(document("planets/post",
                        requestFields(
                                fields.withPath("id").description("Planet's ID"),
                                fields.withPath("name").description("Planet's name")
                        )
                ));
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }
}
