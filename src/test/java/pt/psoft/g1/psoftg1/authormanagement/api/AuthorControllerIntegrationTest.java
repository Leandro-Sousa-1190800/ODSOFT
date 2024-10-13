package pt.psoft.g1.psoftg1.authormanagement.api;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.services.AuthorService;
import pt.psoft.g1.psoftg1.usermanagement.model.User;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/**
 * Based on https://www.baeldung.com/spring-boot-testing
 * <p>Adaptations to Junit 5 with ChatGPT
 */
@ExtendWith(SpringExtension.class)
//@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc
@SpringBootTest
//@ActiveProfiles("integrationtest")
public class AuthorControllerIntegrationTest {

    @MockBean
    private AuthorService authorService;
    private final MockMvc mockMvc;
    private JwtEncoder jwtEncoder;
    private User librarian;
    @MockBean
    private Author author1;
    private String librarianToken;
    private String readerToken;
    @Autowired
    public AuthorControllerIntegrationTest(MockMvc mockMvc, JwtEncoder encoder) {
        this.mockMvc = mockMvc;
        this.jwtEncoder = encoder;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        librarian = new User("maria@gmail.com","Mariaroberta!123");
        author1 = new Author("Robert", "Hello, I'm Robert", "");
        author1.setAuthorNumber(1L);
        final Instant now = Instant.now();
        final long expiry = 36000L; // 1 hours is usually too long for a token to be valid. adjust for production

        final JwtClaimsSet claims = JwtClaimsSet.builder().issuer("example.io").issuedAt(now)
                .expiresAt(now.plusSeconds(expiry)).subject(format("%s,%s", librarian.getId(), librarian.getUsername()))
                .claim("roles", "LIBRARIAN").build();

        librarianToken= this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Test
    void shouldCreateMockMvc(){
        assertNotNull(mockMvc);
    }

    @Test
    void getAuthorByIdShouldReturnUnauthorized() throws Exception {
        when(authorService.findByAuthorNumber(author1.getAuthorNumber())).thenReturn(Optional.ofNullable(author1));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/" + author1.getAuthorNumber()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
    @Test
    void shouldReturnAuthorById() throws Exception {
        when(authorService.findByAuthorNumber(author1.getAuthorNumber())).thenReturn(Optional.ofNullable(author1));
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/authors/1")
                                .header("Authorization", "Bearer " + librarianToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorNumber", Matchers.is(author1.getAuthorNumber().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bio", Matchers.is(author1.getBio())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(author1.getName())));

    }

}
