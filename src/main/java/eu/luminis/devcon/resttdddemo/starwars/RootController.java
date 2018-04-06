package eu.luminis.devcon.resttdddemo.starwars;

import eu.luminis.devcon.resttdddemo.starwars.planets.PlanetRestController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
class RootController {

	@GetMapping("/")
	public ResponseEntity<ResourceSupport> root() {

		ResourceSupport resourceSupport = new ResourceSupport();

		resourceSupport.add(linkTo(methodOn(RootController.class).root()).withSelfRel());
		resourceSupport.add(linkTo(methodOn(PlanetRestController.class).getAllPlanets()).withRel("planets"));

		return ResponseEntity.ok(resourceSupport);
	}

}
