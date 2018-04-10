package eu.luminis.devcon.resttdddemo.starwars.people;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@ExposesResourceFor(PersonResource.class)
@RestController
public class PeopleRestController {

    private PersonRepository personRepository;
    private PersonResourceAssembler personResourceAssembler = new PersonResourceAssembler();

    @Autowired
    public PeopleRestController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Lists all people.
     *
     * @return list of all people
     */
    @GetMapping(value = "/people", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity<Resources<PersonResource>> getAllPeople() {
        List<Person> all = personRepository.findAll();
        Resources<PersonResource> personResources = personResourceAssembler.toPersonResources(all);
        return new ResponseEntity<>(personResources, HttpStatus.OK);
    }

    /**
     * Adds a new person.
     *
     * @param person Person information
     * @return response
     */
    @PostMapping(value = "/people", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity<PersonResource> addPerson(@RequestBody Person person) {
        Person savedPerson = personRepository.save(person);
        PersonResource personResource = personResourceAssembler.toResource(savedPerson);
        return ResponseEntity.created(URI.create(personResource.getLink(Link.REL_SELF).getHref())).build();
    }

    /**
     * Returns a person by ID.
     *
     * @param id ID of the person.
     * @return response
     */
    @GetMapping(value = "/people/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity<PersonResource> getPersonById(@PathVariable Long id) {
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) {
            PersonResource personResource = personResourceAssembler.toResource(person.get());
            return new ResponseEntity<>(personResource, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates existing people.
     *
     * @param id         Person ID.
     * @param personPatchInput Person information.
     */
    @PatchMapping(value = "/people/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePeople(@PathVariable("id") long id, @RequestBody PersonPatchInput personPatchInput) {
        Optional<Person> byId = personRepository.findById(id);
        if (byId.isPresent()) {
            Person person = byId.get();
            if (personPatchInput.getName() != null) {
                person.setName(personPatchInput.getName());
            }
            this.personRepository.save(person);
        }
    }

}
