package com.efada.dummy;


//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.efada.entity.AppUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/dummyREST")
@Profile("dev")
public class DummyRESTController {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();

    public DummyRESTController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @GetMapping("/posts")
    public ResponseEntity<String> getPosts() {
        String url = "https://jsonplaceholder.typicode.com/posts";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.ok(response.getBody());
    }
    
    @GetMapping("/products")
    public String getProducts() {
        // 1. LOGIN with username/password
        String authUrl = "https://dummyjson.com/auth/login";

        // Build the login body
        String requestBody = """
                {
                  "username": "emilys",
                  "password": "emilyspass"
                }
                """;

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> loginRequest = new HttpEntity<>(requestBody, loginHeaders);

        // Call the login endpoint
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                authUrl,
                HttpMethod.POST,
                loginRequest,
                String.class
        );

        // Parse the token from JSON response
        String token = "";
        try {
            JsonNode json = objectMapper.readTree(loginResponse.getBody());
            token = json.get("accessToken").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse token", e);
        }

        // 2. Use token to call the protected endpoint
        String url = "https://dummyjson.com/auth/products";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Adds "Authorization: Bearer <token>"
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        return response.getBody();
    }
	@GetMapping
	public ResponseEntity<?> welcome(){
		System.out.println("welcome");
		return ResponseEntity.ok("welcome");
	}
	
	@GetMapping("/user")
	public AppUser returnUser(){
		AppUser user = new AppUser();
		user.setId(99L);
		user.setUsername("testUser");
		user.setEmail("test@gmail.com");
		user.setPassword("123");
		return user;
	}
	
	/*
	 * @GetMapping("/hateoas") public EntityModel<AppUser> returnHalResponse(){
	 * AppUser user = new AppUser(); user.setId(100L); user.setUsername("halUser");
	 * user.setEmail("test@gmail.com"); user.setPassword("123");
	 * 
	 * EntityModel<AppUser> entityModel = EntityModel.of(user); WebMvcLinkBuilder
	 * link = linkTo(methodOn(this.getClass()).returnUser());
	 * entityModel.add(link.withRel("user"));
	 * 
	 * return entityModel; }
	 */
}
