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
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanetEndpointTest {


    // Add test for getAll (run test to make test fail)
    // Implement getAll in controller (run test to validate the controller is working)
    // Add dummy data to repository during test setup
    // Run test to show the response output (add andDo(print())
    // Add jsonPath assertion
    // Show generated ascii doc snippets
    //


    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation).operationPreprocessors()
                        .withResponseDefaults(prettyPrint()))
                .build();

        planetRepository.deleteAll();
        planetRepository.saveAll(createPlanets());
    }

    @Test
    public void testCreatePlanets() throws Exception {
        Planet planet = new Planet();
        planet.setName("Alderaan");
        planet.setPopulation(10000L);
        mockMvc.perform(post("/planets").accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planet)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAllPlanets() throws Exception {
        mockMvc.perform(get("/planets").accept(MediaType.APPLICATION_JSON))
        	.andExpect(status().isOk())
            .andExpect(jsonPath("$.length()",is(2)))
            .andDo(document("planets/get",
                    responseFields(
//                            fieldWithPath("[].id").description("The id of the planet").type(JsonFieldType.NUMBER),
//                            fieldWithPath("[].name").description("The name of the planet").type(JsonFieldType.STRING),
//                            fieldWithPath("[].diameter").description("The diameter of the planet").type(JsonFieldType.NUMBER),
//                            fieldWithPath("[].population").description("The diameter of the planet").type(JsonFieldType.NUMBER),
                            fieldWithPath("[]").description("An array of planets"))
                            .andWithPrefix("[].", planetFields)

            ))
            .andDo(print());
    }

    private List<Planet> createPlanets(){
        List<Planet> planets = new ArrayList<>();

        Planet planetEndor = new Planet();
        planetEndor.setName("Endor");
        planetEndor.setPopulation(30000000L);

        Planet planetNaboo = new Planet();
        planetNaboo.setName("Naboo");
        planetNaboo.setPopulation(4500000000L);

        planets.add(planetEndor);
        planets.add(planetNaboo);
        return planets;
    }


    private FieldDescriptor[] planetFields = new FieldDescriptor[] {
            fieldWithPath("id").description("The id of the planet").type(JsonFieldType.NUMBER),
            fieldWithPath("name").description("The name of the planet").type(JsonFieldType.STRING),
            fieldWithPath("diameter").description("The diameter of the planet").type(JsonFieldType.NUMBER),
            fieldWithPath("population").description("The population of the planet").type(JsonFieldType.NUMBER)
    };


}
